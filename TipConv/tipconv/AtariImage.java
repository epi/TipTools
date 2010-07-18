/*
 * AtariImage.java - interface for target image formats
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

interface AtariImage
{
	BufferedImage paintImage(int[][] palette);
	byte[] saveImage();
	String getExtension();
}
