#	Makefile - build rules for TipConv
# 
#	Copyright (C) 2008-2012 Adrian Matoga
#
#	This file is part of TipConv, see http://epi.atari8.info/tiptools.php
#
#	TipConv is free software: you can redistribute it and/or modify
#	it under the terms of the GNU General Public License as published by
#	the Free Software Foundation, either version 3 of the License, or
#	(at your option) any later version.
#
#	TipConv is distributed in the hope that it will be useful,
#	but WITHOUT ANY WARRANTY; without even the implied warranty of
#	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#	GNU General Public License for more details.
#
#	You should have received a copy of the GNU General Public License
#	along with TipConv.  If not, see <http://www.gnu.org/licenses/>.
#

XEDISK := xedisk

all: tc2 andy.png satur.png
.PHONY: all

tc2: tc2.d image.d colors.d palconv.d tip.d pngread.o
	dmd $^ -unittest -debug -of$@

pngread.o: pngread.c
	gcc -O2 -Wall -c $< -o $@

andy.png: tc2 lenna2.png andy.sh
	./andy.sh

satur.png: tc2 lenna2.png satur.sh
	./satur.sh

gamma100.tip: lenna2.png tc2
	./tc2 -o $@ $<

gamma130.tip: lenna2.png tc2
	./tc2 -o $@ -t 0 -g 1.3 $<

gamma160.tip: lenna2.png tc2
	./tc2 -o $@ -t 0 -g 1.6 $<

gamma190.tip: lenna2.png tc2
	./tc2 -o $@ -t 0 -g 1.9 $<

gamma220.tip: lenna2.png tc2
	./tc2 -o $@ -t 0 -g 2.2 $<

test.atr: tv.xex gamma100.tip gamma130.tip gamma160.tip gamma190.tip gamma220.tip
	$(XEDISK) n $@ -Fmydos -s720 && \
	$(XEDISK) b $@ -d450 && \
	$(XEDISK) a $@ $^

clean:
	rm -f pngread.o tc2 lenna2-???.tip lenna2-???.png lenna2-st-???.tip lenna2-st-???.png andy.png satur.png test.atr gamma100.tip gamma130.tip gamma160.tip gamma190.tip gamma220.tip
.PHONY:clean

.DELETE_ON_ERROR:
