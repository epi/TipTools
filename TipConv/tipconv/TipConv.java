/*
 * TipConv.java - startup code, handling command line
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
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

class TipConv
{
	public static String getHowTo()
	{
		return
			"1. Load source image\n" +
			"2. Select destination format (Image > Format)\n" +
			"3. Select resize mode (Image > Resize)\n" +
			"4. Select conversion palette (Palette > Conversion)\n" +
			"5. Select saturation factor for conversion (Palette > Saturation)\n" +
			"6. Convert image (Image > Convert)\n" +
			"7. Repeat steps 2-6 until you achieve the best results\n" +
			"8. Save converted image";
	}

	public static String getInfo()
	{
		return
			"TipConv v1.1.1\nAdrian Matoga (Epi/Tristesse)\n2009-11-25\n" +
			"Palette files from Atari800WinPLus";
	}

    public static void main(String[] args)
	{
		if (args.length == 0) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
			}
			new TipConvGUI();
		}
		else {
			Converter converter = new Converter();
			converter.setPalette("Palette" + File.separator + "Jakub.act");
			boolean showHelp = false;
			for (String arg : args) {
				try {
					if (arg.startsWith("-")) {
						if (arg.equals("--tip") || arg.equals("-t"))
							converter.setFormat(Converter.Format.TIP01);
						else if (arg.equals("--hip") || arg.equals("-h"))
							converter.setFormat(Converter.Format.HIP);
						else if (arg.equals("--fit") || arg.equals("-f"))
							converter.setResizeMode(Converter.Resize.FIT);
						else if (arg.equals("--crop") || arg.equals("-c"))
							converter.setResizeMode(Converter.Resize.CROP);
						else if (arg.equals("--stretch") || arg.equals("-s"))
							converter.setResizeMode(Converter.Resize.STRETCH);
						else if (arg.startsWith("--palette="))
							converter.setPalette(arg.substring(10));
						else if (arg.startsWith("-p"))
							converter.setPalette(arg.substring(2));
						else if (arg.equals("--linear") || arg.equals("-l"))
							converter.setBrightnessMode(Converter.Brightness.LINEAR);
						else if (arg.startsWith("--saturation="))
							converter.setSatFactor(new Float(arg.substring(13)));
						else if (arg.startsWith("-a"))
							converter.setSatFactor(new Float(arg.substring(2)));
						else if (arg.equals("--version")) {
							System.out.println(getInfo());
							return;
						}
						else {
							showHelp = true;
							break;
						}
					}
				} catch (Exception e) {
					System.err.println(arg + ": " + e);
					return;
				}
			}
			if (!showHelp) {
				showHelp = true;
				for (String arg : args) {
					if (!arg.startsWith("-")) {
						showHelp = false;
						try {
							BufferedImage srcImage = ImageIO.read(new File(arg));
							srcImage = converter.prepare(srcImage);
							AtariImage atariImage = converter.convert(srcImage);
							int d = arg.lastIndexOf('.');
							if (d >= 0)
								arg = arg.substring(0, d);
							arg += atariImage.getExtension();
							FileOutputStream fo = new FileOutputStream(arg);
							fo.write(atariImage.saveImage());
							fo.close();
						} catch (IOException e) {
							System.err.println(arg + ": " + e);
						}
					}
				}
			}
			if (showHelp) {
				System.out.println(
					"Usage:\n" +
					"java -jar tipconv.jar [-t | -h] [-f | -c | -s] [-l] [-aS] [-pFile] file...\n" +
					"java -jar tipconv.jar [--help | --version]\n" +
					"\nWhen no arguments (options or files) given, GUI is opened\n" +
					"\nOutput format:\n" +
					"-t      --tip           convert to TIP image (default)\n" +
					"-h      --hip           convert to HIP image\n" +
					"\nResize mode:\n" +
					"-f      --fit           resize to fit maximum size, keep aspect ratio (default)\n" +
					"-c      --crop          crop to fill the whole area for selected format,\n" +
					"                        keep aspect ratio\n" +
					"-s      --stretch       stretch to fill the whole area for selected format,\n" +
					"                        keep all image contents, but ignore aspect ratio\n" +
					"\nColor mapping and palette:\n" +
					"-l      --linear        enable separate linear brightness conversion\n" +
					"-aS     --saturation=S  sets saturation multiplier S (default 4.0)\n" +
					"-pFile  --palette=File  use palette file \"File\"\n" + 
					"\nSome redundant messages:\n" +
					"        --help          print this message\n" +
					"        --version       print version info");
			}
		}
    }
}
