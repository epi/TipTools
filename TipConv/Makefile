tipconv.jar: tipconv/*.java
	javac tipconv/TipConv.java
	jar cvfm tipconv.jar MANIFEST.MF tipconv/*.class

release: release/tipconv-1.1.1.zip
.PHONY: release

release/tipconv-1.1.1.zip: tipconv.jar $(wildcard icons/*) $(wildcard Palette/*)
	mkdir -p release/tipconv-1.1.1
	cp -r tipconv.jar icons Palette release/tipconv-1.1.1
	cd release && zip -r tipconv-1.1.1.zip tipconv-1.1.1

clean:
	rm -f tipconv.jar
	rm -f tipconv/*.class
	rm -f release/tipconv-1.1.1.zip
.PHONY: clean

.DELETE_ON_ERROR:
