tipconv.jar: tipconv/*.java
	javac tipconv/TipConv.java
	jar cvfm tipconv.jar MANIFEST.MF tipconv/*.class

clean:
	rm -f tipconv.jar
	rm -f tipconv/*.class

.DELETE_ON_ERROR:

