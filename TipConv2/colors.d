/*
	colors.d - color space definitions

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

template GenColor3CompType(string name, string comp1, string comp2, string comp3)
{
	const string GenColor3CompType =
		"struct " ~ name ~ "\n" ~
		"{\n" ~
		"	pure nothrow this(uint value) { value_ = value; }\n" ~ 
		"	pure nothrow this(uint " ~ comp1 ~ ", uint " ~ comp2 ~ ", uint " ~ comp3 ~ ")\n" ~
		"	{\n" ~
		"		value_ = (" ~ comp1 ~ " & 0xFF) | ((" ~ comp2 ~ " & 0xFF) << 8) | ((" ~ comp3 ~ " & 0xFF) << 16);\n" ~
		"	}\n" ~
		"	pure nothrow @property uint " ~ comp1 ~ "() { return value_ & 0xFF; }\n" ~
		"	pure nothrow @property uint " ~ comp2 ~ "() { return (value_ >> 8) & 0xFF; }\n" ~
		"	pure nothrow @property uint " ~ comp3 ~ "() { return (value_ >> 16) & 0xFF; }\n" ~
		"	private uint value_;\n" ~
		"}\n";
}

mixin(GenColor3CompType!("RGB", "red", "green", "blue"));

// hue = 0 .. 239, wrapping; GTIA hue' = (hue >> 4) + 1;
// sat = 0 .. 255;           if (sat < threshold) hue' = 0;
// lum = 0 .. 255;           GTIA lum' = (lum >> 4);
mixin(GenColor3CompType!("HSY", "hue", "sat", "lum"));

interface ColorConverter
{
	HSY rgbToHsy(RGB rgb);
	//RGB HsyToRgb(HSY hsy);
}

unittest
{
	RGB rgb = RGB(0xDEADBEEF);
	assert(rgb.red == 0xEF);
	assert(rgb.green == 0xBE);
	assert(rgb.blue == 0xAD);

	HSY hsy = HSY(0xC0, 0x0F, 0xEE);
	assert(hsy.hue == 0xC0);
	assert(hsy.sat == 0x0F);
	assert(hsy.lum == 0xEE);
}

