VERSION_MAJOR = 1
VERSION_MINOR = 1
VERSION = $(VERSION_MAJOR).$(VERSION_MINOR)
YEAR = 2011
ATRNAME = tipview-$(VERSION).atr

XASM = xasm
XEBIN = xebin
XEDISK = xedisk
ZIP = 7z a -mx=9 -tzip $@
RM = rm -f

all: $(ATRNAME)

$(ATRNAME): tv.xex
	$(XEDISK) n $(ATRNAME) -f mydos -s 720
	$(XEDISK) write-dos $(ATRNAME) -D 450
	$(XEDISK) a $(ATRNAME) tv.xex

tv.xex: tv.obx
	$(XEBIN) pack $^ -o $@

tv.obx: tv.asx neo6.fnt
	$(XASM) $< /o:$@ /d:VERSION_MAJOR=$(VERSION_MAJOR) /d:VERSION_MINOR=$(VERSION_MINOR)

tipview-$(VERSION).zip: $(ATRNAME)
	$(RM) $@
	$(ZIP) $^

clean:
	$(RM) tipview-$(VERSION).zip $(ATRNAME) tv.xex tv.obx

.PHONY: clean all

.DELETE_ON_ERROR:
