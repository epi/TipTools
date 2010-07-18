/*
 * TIP01Image.java - conversion, display, and file output for TIP
 * (Taquart Interlace Picture) images
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

class TIP01Image implements AtariImage
{
	private byte[][] gr9, gr10, gr11;
	private int[][] pal;

	private int height, posY;

	public TIP01Image(BufferedImage img, int[][] plt, boolean brPal, int iPosX, int iPosY, int iWidth, int iHeight)
	{
		if (img != null) {
			height = iHeight / 2;
			posY = iPosY;
			gr9 = new byte[height][40];
			gr10 = new byte[height][40];
			gr11 = new byte[height][40];
			int x, y, j, m, diff, n, d, e, lu, ru, ld, rd;
			int r[] = new int[4], g[] = new int[4], b[] = new int[4], c[] = new int[4];
			for (y = 0; y < height; y++) {
				for (x = 0; x < 40; x++) {
					for (j = 0; j < 4; j++) {
						// usrednienie czterech sasiednich pikseli
						lu = img.getRGB(x*8 + j*2, y*2 + iPosY);
						ru = img.getRGB(x*8 + j*2 + 1, y*2 + iPosY);
						ld = img.getRGB(x*8 + j*2, y*2 + iPosY + 1);
						rd = img.getRGB(x*8 + j*2 + 1, y*2 + iPosY + 1);
						r[j] = (((lu >> 16) & 0xFF) + ((ru >> 16) & 0xFF) + ((ld >> 16) & 0xFF) + ((rd >> 16) & 0xFF))/4;
						g[j] = (((lu >> 8) & 0xFF) + ((ru >> 8) & 0xFF) + ((ld >> 8) & 0xFF) + ((rd >> 8) & 0xFF))/4;
						b[j] = ((lu & 0xFF) + (ru & 0xFF) + (ld & 0xFF) + (rd & 0xFF))/4;
						// znajdz najblizszy kolor w palecie - wg. sumy kwadratow wspolrzednych
						diff = 0x7fffffff; n = 0;
						for (m = 0; m < 256; m++) {
							e = (plt[m][0] - r[j]); d = e*e;
							e = (plt[m][1] - g[j]); d += e*e;
							e = (plt[m][2] - b[j]); d += e*e;
							if (d < diff) {
								diff = d; n = m;
							}
						}
						c[j] = n; b[j] = (r[j] + g[j] + b[j]) / 48;
					}
					if (brPal) {
						gr9[y][x] = (byte)(((c[0] & 0x0f) << 4) | (c[2] & 0x0f));
						gr10[y][x] = (byte)(((c[1] & 0x0e) << 3) | ((c[3] & 0x0e) >> 1));
					} else {
						gr9[y][x] = (byte)(((b[0]) << 4) | b[2]);
						gr10[y][x] = (byte)(((b[1] & 0x0e) << 3) | ((b[3] & 0x0e) >> 1));
					}						
					gr11[y][x] = (byte)((c[0] & 0xf0) | ((c[2] & 0xf0) >> 4));
				}
			}
		}
	}

	public byte[] saveImage()
	{
		byte[] f = new byte[9 + height*120];
		f[0] = 0x54;
		f[1] = 0x49;
		f[2] = 0x50;
		f[3] = 0x01;
		f[4] = 0x00;
		f[5] = (byte)0xa0;
		f[6] = (byte)height;
		f[7] = (byte)(height*40 & 0xff);
		f[8] = (byte)((height*40 >> 8) & 0xff);
		for (int y = 0; y < height; y++) {
			System.arraycopy(gr9[y], 0, f, 9 + y*40, 40);
			System.arraycopy(gr10[y], 0, f, 9 + (y+height)*40, 40);
			System.arraycopy(gr11[y], 0, f, 9 + (y+height*2)*40, 40);
		}
		return f;
	}

	public String toString()
	{
		return "TIP (160x" + height + ")";
	}

	public String getExtension()
	{
		return ".tip";
	}

	private final void plot(BufferedImage i, int x, int y, int br1, int br2, int h)
	{
		int br = 	((pal[br1 | h][0] + pal[br2 | h][0])/2)* 0x010000 +
					((pal[br1 | h][1] + pal[br2 | h][1])/2)* 0x000100 +
					((pal[br1 | h][2] + pal[br2 | h][2])/2);
		i.setRGB(x*2, y*2 + posY, br);
		i.setRGB(x*2 + 1, y*2 + posY, br);
		i.setRGB(x*2, y*2 + posY + 1, (br & 0xe0e0e0) >> 1);
		i.setRGB(x*2 + 1, y*2 + posY + 1, (br & 0xe0e0e0) >> 1);
	}

	public BufferedImage paintImage(int[][] palette)
	{
		if (gr9 != null && gr10 != null) {
			pal = palette;
			BufferedImage img = new BufferedImage(320, 238, BufferedImage.TYPE_3BYTE_BGR);
			int x, y, g9, g10, g11;
			for (y = 0; y < height; y++) {
				for (x = 0; x < 40; x++) {
					g9 = gr9[y][x] & 0xff;	// "odznakowanie"
					g10 = gr10[y][x] & 0xff;
					g11 = gr11[y][x] & 0xff;
					plot(img, x*4 + 0, y, (g9 >> 4), (x == 0 ? 0 : (gr10[y][x - 1] & 0xf)*2), g11 & 0xf0);
					plot(img, x*4 + 1, y, (g9 >> 4), (g10 >> 3) & 0x0e, g11 & 0xf0);
					plot(img, x*4 + 2, y, (g9 & 0xF), (g10 >> 3) & 0x0e, (g11 & 0x0f) << 4);
					plot(img, x*4 + 3, y, (g9 & 0xF), (g10 & 0xf)*2, (g11 & 0x0f) << 4);
				}
			}
			return img;
		}
		else return null;
	}
}
