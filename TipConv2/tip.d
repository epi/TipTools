/*
	tip.d - use color converter to convert an RGB image to TIP

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

import std.algorithm;
import std.math;

import image;
import colors;

ubyte[] encodeTip(Image img, ColorConverter cc, uint satThreshold)
{
	auto tipw = min(img.width, 160);
	auto tiph = min(img.height, 119);
	auto tipleft = (160 - tipw) / 2;
	auto gr9 = new ubyte[40 * tiph];
	auto gr10 = new ubyte[40 * tiph];
	auto gr11 = new ubyte[40 * tiph];
	ubyte[] header = [
		cast(ubyte) 'T',
		cast(ubyte) 'I',
		cast(ubyte) 'P',
		cast(ubyte) 1,
		cast(ubyte) 0,
		cast(ubyte) 160,
		cast(ubyte) tiph,
		cast(ubyte) ((tiph * 40) & 0xFF),
		cast(ubyte) (((tiph * 40) >> 8) & 0xFF)
	];
	foreach (y; 0 .. tiph)
	{
		auto tipx = tipleft;
		foreach (x; 0 .. tipw)
		{
			auto hsy = cc.rgbToHsy(img[x, y]);
			auto lum = hsy.lum >> 4;
			auto hue = (hsy.sat < satThreshold) ? 0 : ((hsy.hue >> 4) + 1);
			assert(lum >= 0 && lum <= 15);
			assert(hue >= 0 && hue <= 15);
			final switch (tipx & 3)
			{
			case 0:
				gr9[y * 40 + tipx / 4] = cast(ubyte) (lum << 4);
				gr11[y * 40 + tipx / 4] = cast(ubyte) (hue << 4);
				break;
			case 1:
				gr10[y * 40 + tipx / 4] = cast(ubyte) ((lum << 3) & 0x70);
				break;
			case 2:
				gr9[y * 40 + tipx / 4] = cast(ubyte) (gr9[y * 40 + tipx / 4] | lum);
				gr11[y * 40 + tipx / 4] = cast(ubyte) (gr11[y * 40 + tipx / 4] | hue);
				break;
			case 3:
				gr10[y * 40 + tipx / 4] = cast(ubyte) (gr10[y * 40 + tipx / 4] | (lum >> 1));
				break;
			}
			tipx++;
		}
	}
	return header ~ gr9 ~ gr10 ~ gr11;
}

