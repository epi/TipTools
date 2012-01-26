/*
	pngread.c - read png file

	Copyright (C) 2008-2012 Adrian Matoga

	This file is part of TipConv, see http://epi.atari8.info/tiptools.php

	TipConv is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	TipConv is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with TipConv.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <stdio.h>
#include <stdlib.h>
#include <png.h>

int pngread(const char *filename, unsigned *(*allocate_pixels)(void *obj_ptr, unsigned width, unsigned height), void *obj_ptr)
{
	int result = -1;
	FILE *fp = fopen(filename, "rb");
	if (!fp)
		goto ret;
	
	unsigned char header[8];
	if (fread(header, 1, 8, fp) != 8)
		goto ret_close;
	if (png_sig_cmp(header, 0, 8))
		goto ret_close;

	png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
	if (!png_ptr)
		goto ret_close;

	png_infop info_ptr = png_create_info_struct(png_ptr);
	if (!info_ptr)
		goto ret_destroy_read_struct;

	png_infop end_info = png_create_info_struct(png_ptr);
	if (!end_info)
		goto ret_destroy_read_struct;

	if (setjmp(png_jmpbuf(png_ptr)))
		goto ret_destroy_read_struct;

	png_init_io(png_ptr, fp);
	png_set_sig_bytes(png_ptr, 8);
	png_read_png(png_ptr, info_ptr, 0, NULL);

	unsigned width = png_get_image_width(png_ptr, info_ptr);
	unsigned height = png_get_image_height(png_ptr, info_ptr);
	png_byte color_type = png_get_color_type(png_ptr, info_ptr);
	int x;
	int y;
	int i = 0;
	png_bytep *row_pointers = png_get_rows(png_ptr, info_ptr);
	if (color_type != PNG_COLOR_TYPE_RGB)
		goto ret_destroy_read_struct;
	unsigned *pixels = allocate_pixels(obj_ptr, width, height);

	if (!pixels)
		goto ret_destroy_read_struct;
	for (y = 0; y < height; ++y)
	{
		png_bytep row = row_pointers[y];
		for (x = 0; x < width; ++x)
		{
			pixels[i++] =
				(row[x * 3] << 16) + (row[x * 3 + 1] << 8) + (row[x * 3 + 2]);
		}
	}

	result = 0;		
ret_destroy_read_struct:
	png_destroy_read_struct(&png_ptr, (png_infopp)NULL, (png_infopp)NULL);
ret_close:
	fclose(fp);
ret:	
	return result;
}

