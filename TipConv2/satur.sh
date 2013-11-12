#!/bin/bash
#
# satur.sh - make a montage of 9 conversion examples
#
# Copyright (C) 2008-2012 Adrian Matoga
#
# This file is part of TipConv, see http://epi.atari8.info/tiptools.php
#
# TipConv is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# TipConv is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with TipConv.  If not, see <http://www.gnu.org/licenses/>.
#

rm -f lenna2-st-???.tip lenna2-st-???.png && (
for thresh in 001 004 008 016 024 032 040 048 064;
do
	./tc2 -t $thresh -o lenna2-st-$thresh.tip lenna2.png
	fail2png -o lenna2-st-$thresh.png lenna2-st-$thresh.tip
done ) && \
montage lenna2-st-???.png satur.png && \
rm -f lenna2-st-???.tip lenna2-st-???.png

