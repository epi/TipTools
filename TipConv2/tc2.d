/*
	tc2.d - user interface

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
import std.file;
import std.string;
import std.conv;
import std.c.stdlib;

import image;
import palconv;
import tip;

int main(string[] args)
{
	void printHelp()
	{
		write("Usage: " ~ args[0] ~ " [OPTIONS] INPUTFILE...
Options (default values in parentheses):
-g G     --gamma=G      use gamma decoding of input image with gamma=G (1.0)
-f PSO   --offset=PSO   set chroma phase shift offset to PSO degrees (45)
-s PSS   --step=PSS     set chroma phase shift step size to PSS degrees (24)
-t ST    --threshold=T  set saturation threshold to ST (8)
-o FILE  --output=FILE  set output file name to FILE (INPUTFILE.tip)
-h       --help         show this message
");
	}

	if (args.length < 2)
	{
		printHelp();
		return 1;
	}

	auto cc = new PalColorConverter();
	uint saturationThreshold = 8;
	string outname;
	size_t files;
	auto xargs = args[1 .. $];

	string getArg()
	{
		if (xargs.length == 0)
		{
			stderr.writeln("tipconv2: Missing option parameter");
			exit(1);
		}
		string result = xargs[0];
		xargs = xargs[1 .. $];
		return result;
	}

	while (xargs.length)
	{
		auto arg = xargs[0];
		xargs = xargs[1 .. $];
		if (arg.startsWith("-"))
		{
			if (arg == "-g" || arg == "--gamma")
				cc.gamma = to!double(getArg());
			else if (arg.startsWith("--gamma="))
				cc.gamma = to!double(arg[8 .. $]);
			else if (arg == "-f" || arg == "--offset")
				cc.phaseShiftOffset = to!double(getArg());
			else if (arg.startsWith("--offset="))
				cc.phaseShiftOffset = to!double(arg[9 .. $]);
			else if (arg == "-s" || arg == "--step")
				cc.phaseShiftStep = to!double(getArg());
			else if (arg.startsWith("--step="))
				cc.phaseShiftStep = to!double(arg[7 .. $]);
			else if (arg == "-t" || arg == "--threshold")
				saturationThreshold = to!ubyte(getArg());
			else if (arg.startsWith("--threshold="))
				saturationThreshold = to!ubyte(arg[12 .. $]);
			else if (arg == "-o" || arg == "--output")
				outname = getArg();
			else if (arg.startsWith("--output="))
				outname = arg[9 .. $];
			else if (arg == "-h" || arg == "--help")
			{
				printHelp();
				return 0;
			}
			else
			{
				stderr.writeln("tipconv2: Invalid option " ~ arg);
				return 1;
			}
		}
		else
		{
			++files;
			if (!outname.length)
				outname = format("%s.tip", arg);
			std.file.write(outname, encodeTip(Image.loadPngFile(arg), cc, saturationThreshold));
			outname = "";
		}
	}

	if (!files)
	{
		stderr.writeln("tipconv2: No input files");
		return 1;
	}
	return 0;
}

