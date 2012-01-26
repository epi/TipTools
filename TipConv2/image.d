/*
	image.d - simple Image class with interface to a C helper to read
	          PNG files.

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

import std.stdio;
import std.string;
import colors;

pragma(lib, "png");

extern (C)
int pngread(const char* filename, uint* function(void* obj_ptr, uint width, uint height) allocate_pixels, void* obj_ptr);

extern (C)
static uint* allocate_pixels(void* obj_ptr, uint width, uint height)
{
	Image img;
	try
	{
		img = cast(Image) obj_ptr;
		auto pixels = new uint[width * height];
		img.pixels_ = pixels;
		img.width_ = width;
		img.height_ = height;
		return pixels.ptr;
	}
	catch (Exception e)
	{
		stderr.writeln("tipconv2: ", e.msg);
		return null;
	}
}

class Image
{
	static Image loadPngFile(string filename)
	{
		Image img = new Image();
		if (pngread(filename.toStringz(), &allocate_pixels, cast(void*) img) < 0)
			throw new Exception("Failed to load " ~ filename);
		return img;
	}

	pure nothrow @property size_t width()
	{
		return width_;
	}

	pure nothrow @property size_t height()
	{
		return height_;
	}

	pure nothrow RGB opIndex(size_t x, size_t y)
	{
		return RGB(pixels_[y * width_ + x]);
	}

private:
	this() {}

	size_t width_;
	size_t height_;
	uint[] pixels_;
}

