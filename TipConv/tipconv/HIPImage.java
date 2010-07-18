/*
 * HIPImage.java - conversion, display, and file output for HIP
 * (Hard Interlace Picture) images
 *
 * Copyright (C) 2008-2010  Adrian Matoga
 *
 * This file is part of TipConv,
 * see http://epi.atari8.info/tiptools.php
 *
 * TipConv is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * TipConv is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TipConv; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package tipconv;
import java.awt.*;
import java.awt.image.*;

class HIPImage implements AtariImage
{
	private byte[][] gr9, gr10;
	private int[][] pal;

	private static final int SY = 19;

	public HIPImage(BufferedImage img, int[][] palette, boolean brPal, int iPosX, int iPosY, int iWidth, int iHeight)
	{
		if (img != null) {
			gr9 = new byte[200][40];
			gr10 = new byte[200][40];
			int x, y, l, r, pix0, pix1, pix2, pix3;
			for (y = 0; y < 200; y++) {
				for (x = 0; x < 40; x++) {
					l = img.getRGB(x*8, y + SY);
					r = img.getRGB(x*8 + 1, y + SY);
					pix0 = ((l & 0xFF) + ((l >> 8) & 0xFF) + ((l >> 16) & 0xFF) +
							(r & 0xFF) + ((r >> 8) & 0xFF) + ((r >> 16) & 0xFF))/6;
					l = img.getRGB(x*8 + 2, y + SY);
					r = img.getRGB(x*8 + 3, y + SY);
					pix1 = ((l & 0xFF) + ((l >> 8) & 0xFF) + ((l >> 16) & 0xFF) +
							(r & 0xFF) + ((r >> 8) & 0xFF) + ((r >> 16) & 0xFF))/6;
					l = img.getRGB(x*8 + 4, y + SY);
					r = img.getRGB(x*8 + 5, y + SY);
					pix2 = ((l & 0xFF) + ((l >> 8) & 0xFF) + ((l >> 16) & 0xFF) +
							(r & 0xFF) + ((r >> 8) & 0xFF) + ((r >> 16) & 0xFF))/6;
					l = img.getRGB(x*8 + 6, y + SY);
					r = img.getRGB(x*8 + 7, y + SY);
					pix3 = ((l & 0xFF) + ((l >> 8) & 0xFF) + ((l >> 16) & 0xFF) +
							(r & 0xFF) + ((r >> 8) & 0xFF) + ((r >> 16) & 0xFF))/6;
					gr9[y][x] = (byte)((pix0 & 0xF0) | (pix2 >> 4));
					gr10[y][x] = (byte)((((pix1 & 0xE0) >> 1) | (pix3 >> 5)) + 0x11);
				}
			}
		}
	}

	public byte[] saveImage()
	{
		byte[] f = new byte[16009];
		byte[] p = { 0, 0, 2, 4, 6, 8, 10, 12, 14 };
		for (int y = 0; y < 200; y++) {
			System.arraycopy(gr9[y], 0, f, y*40, 40);
			System.arraycopy(gr10[y], 0, f, (y+200)*40, 40);
		}
		System.arraycopy(p, 0, f, 16000, 9);
		return f;
	}

	public String toString()
	{
		return "HIP (160x200)";
	}

	public String getExtension()
	{
		return ".hip";
	}

	private final void plot(BufferedImage i, int x, int y, int br1, int br2)
	{
		if (br1 < 0) br1 = 0;
		if (br2 < 0) br2 = 0;
		int br = 	(pal[br1][0] + pal[br2][0])/2 * 0x010000 +
					(pal[br1][1] + pal[br2][1])/2 * 0x000100 +
					(pal[br1][2] + pal[br2][2])/2;
		i.setRGB(x*2, y + SY, br);
		i.setRGB(x*2 + 1, y + SY, br);
	}

	public BufferedImage paintImage(int[][] palette)
	{
		if (gr9 != null && gr10 != null) {
			pal = palette;
			BufferedImage img = new BufferedImage(320, 238, BufferedImage.TYPE_3BYTE_BGR);
			int x, y, g9, g10;
			for (y = 0; y < 200; y++) {
				for (x = 0; x < 40; x++) {
					g9 = gr9[y][x] & 0xff;	// "odznakowanie"
					g10 = gr10[y][x] & 0xff;
					plot(img, x*4 + 0, y, (g9 >> 4), (x == 0 ? 0 : (((gr10[y][x-1] & 0xF) - 1)*2)));
					plot(img, x*4 + 1, y, (g9 >> 4), ((g10 >> 3) - 2));
					plot(img, x*4 + 2, y, (g9 & 0xF), ((g10 >> 3) - 2));
					plot(img, x*4 + 3, y, (g9 & 0xF), ((g10 & 0xF) - 1)*2);
				}
			}
			return img;
		}
		else return null;
	}
}
