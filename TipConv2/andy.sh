#!/bin/bash
#
# andy.sh - make a montage of 25 conversion examples
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

rm -f lenna2-???.tip lenna2-???.png && (
for offset in 000 014 028 043 057 072 086 100 115 129 144 158 172 187 201 216 230 244 259 273 288 302 316 331 345;
do
	./tc2 -f $offset -o lenna2-$offset.tip lenna2.png
	fail2png -o lenna2-$offset.png lenna2-$offset.tip
done ) && \
montage lenna2-???.png andy.png && \
rm -f lenna2-???.tip lenna2-???.png

