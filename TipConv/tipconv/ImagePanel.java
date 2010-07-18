/*
 * ImagePanel.java - simple panel displaying an image
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
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

class ImagePanel extends JComponent
{
	public BufferedImage image;
	
	public ImagePanel()
	{
		super();
		image = null;
	}

	public void paint(Graphics g)
	{
		if (image != null) {
			g.setColor(Color.black);
			int mul = 1;
			if (getWidth() > image.getWidth() * 1.5)
				mul = 2;
			g.drawImage(image, 2, 2, image.getWidth() * mul, image.getHeight() * mul, null);
		}
	}
}
