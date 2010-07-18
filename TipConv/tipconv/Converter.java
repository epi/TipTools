/*
 * Converter.java - class that prepares source image according to selected
 * conversion options and passes it to appropriate handler
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
import java.io.*;
import java.awt.*;
import java.awt.image.*;

class Converter
{
	public enum Format {
		TIP01, HIP
	}
	public enum Resize {
		FIT, CROP, STRETCH
	}
	public enum Brightness {
		LINEAR, PALETTE
	}
	
	private Format format;
	private Resize resizeMode;
	private Brightness brightness;
	private int maxWidth;
	private int maxHeight;

	private int posx;
	private int posy;
	private int width;
	private int height;

	int[][] palette;
	private String paletteName;
	
	private float satF;

	public Converter()
	{
		format = Format.TIP01;
		resizeMode = Resize.FIT;
		maxWidth = 320;
		maxHeight = 238;
		satF = (float)4.0;
		brightness = Brightness.PALETTE;
	}

	public void setPalette(String fileName)
	{
		palette = readPalette(fileName, satF);
		paletteName = fileName;
	}

	public void setFormat(Format fmt)
	{
		format = fmt;
		switch (fmt) {
		case TIP01:
			setMaxSize(320, 238);
			break;
		case HIP:
			setMaxSize(320, 200);
		}
	}

	public Format getFormat()
	{
		return format;
	}

	public void setBrightnessMode(Brightness b)
	{
		brightness = b;
	}

	public void setSatFactor(float s)
	{
		satF = s;
		palette = readPalette(paletteName, satF);
	}

	public void setResizeMode(Resize r)
	{
		resizeMode = r;
	}

	public Resize getResizeMode()
	{	
		return resizeMode;
	}

	private void setMaxSize(int w, int h)
	{
		maxWidth = w;
		maxHeight = h;
	}

	private void adjustSize(int[] wh)
	{
		int width = wh[0];
		int height = wh[1];
		double ratio = (double)width / height;
		switch (resizeMode) {
		case FIT:
			if (ratio > (double)maxWidth/maxHeight) { width = maxWidth; height = -1; }
			else { width = -1; height = maxHeight; }
			break;
		case CROP:
			if (ratio < (double)maxWidth/maxHeight) { width = maxWidth; height = -1; }
			else { width = -1; height = maxHeight; }
			break;
		case STRETCH:
			width = maxWidth; height = maxHeight;
		}
		wh[0] = width;
		wh[1] = height;
	}

	public AtariImage convert(BufferedImage image)
	{
		switch (format) {
		case HIP:
			return new HIPImage(image, palette, brightness == Brightness.PALETTE, posx, posy, width, height);
		case TIP01:
			return new TIP01Image(image, palette, brightness == Brightness.PALETTE, posx, posy, width, height);
		}
		return null;
	}

	private BufferedImage cropImage(BufferedImage j)
	{
		if (j.getHeight(null) > maxHeight) {
			j = j.getSubimage(0, (j.getHeight(null) - maxHeight)/2, j.getWidth(null), maxHeight);
		}
		if (j.getWidth(null) > maxWidth) {
			j = j.getSubimage((j.getWidth(null) - maxWidth)/2, 0, maxWidth, j.getHeight(null));
		}
		return j;
	}

	// podglad obrazka zrodlowego z zastosowana odpowiednia metoda zmiany rozmiarow
	public BufferedImage prepare(BufferedImage src)
	{
		if (src != null) {
			BufferedImage prev = new BufferedImage(320, 238, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = prev.createGraphics();
			g.setBackground(Color.BLACK);
			// ustalenie wymiarow docelowych
			int[] wh = new int[2];
			wh[0] = src.getWidth(null);
			wh[1] = src.getHeight(null);
			adjustSize(wh);
			width = wh[0];
			height = wh[1];
			// skalowanie
			Image i = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage j = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
			j.createGraphics().drawImage(i, 0, 0, null);
			// obciecie obrazka
			j = cropImage(j);
			// ustawienie wspolrzednych
			width = j.getWidth(null);
			height = j.getHeight(null);
			posx = 160 - width / 2;
			posy = 119 - height / 2;
			// wyswietlenie podgladu
			g.drawImage(j, posx, posy, null);
			return prev;
		}
		else return null;
	}

	public static int[][] readPalette(String fileName, float satF)
	{
		byte[] d = new byte[768];
		try {
			FileInputStream f = new FileInputStream(new File(fileName));
			f.read(d);
			f.close();
		} catch (IOException e) {
			return null;
		}
		int[][] pal = new int[256][3];
		int i = 0, y, r, g, b, rgb;
		float[] hsb = new float[3];
		for (y = 0; y < 256; y++) {
			r = d[i++] & 0xFF;
			g = d[i++] & 0xFF;
			b = d[i++] & 0xFF;
			Color.RGBtoHSB(r, g, b, hsb);
			rgb = Color.HSBtoRGB(hsb[0], hsb[1] / satF, hsb[2]);
			pal[y][0] = (rgb >> 16) & 0xFF;
			pal[y][1] = (rgb >> 8) & 0xFF;
			pal[y][2] = (rgb) & 0xFF;
		}
		return pal;
	}
}
