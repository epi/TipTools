/*
	palconv.d - RGB -> YUV -> HSY color conversion

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

import std.math;

import colors;

private enum YuvWeight
{
	Red = cast(uint) (0.299 * 65536.0),
	Green = cast(uint) (0.587 * 65536.0),
	Blue = cast(uint) (0.114 * 65536.0),
	U = cast(uint) (0.492 * 65536.0),
	V = cast(uint) (0.877 * 65536.0)
}

class PalColorConverter : ColorConverter
{
	this()
	{
		computeGammaDecode();		
	}

	override HSY rgbToHsy(RGB rgb)
	{
		auto phaseShiftOffsetRad = phaseShiftOffset / 180.0 * PI;
		auto phaseShiftStepRad = phaseShiftStep / 16.0 / 180.0 * PI;

		auto lum =
			 (YuvWeight.Red * gammaDecode_[rgb.red]
			+ YuvWeight.Green * gammaDecode_[rgb.green]
			+ YuvWeight.Blue * gammaDecode_[rgb.blue]) >> 16;
		auto u = (YuvWeight.U * (gammaDecode_[rgb.blue] - lum)) >> 16;
		auto v = (YuvWeight.V * (gammaDecode_[rgb.red] - lum)) >> 16;
		auto sat = cast(uint) sqrt(cast(float) (v * v + u * u));
		auto phi = atan2(cast(double) v, cast(double) u);
		if (phi < 0)
			phi += 2.0 * PI;
		auto hue = cast(uint) ((phaseShiftOffsetRad + phi) / phaseShiftStepRad) % 240;
		return HSY(hue, sat, lum);
	}

	@property double gamma()
	{
		return gamma_;
	}

	@property void gamma(double gamma)
	{
		gamma_ = gamma;
		computeGammaDecode();
	}

	double phaseShiftOffset = 45.0;
	double phaseShiftStep = 24.0;

private:
	void computeGammaDecode()
	{
		foreach (i, ref v; gammaDecode_)
		{
			v = cast(ubyte) (255.0 * ((i / 255.0) ^^ gamma_));
		}
	}

	ubyte[256] gammaDecode_;

	double gamma_ = 1.0;
}

