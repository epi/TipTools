package tipconv;
import java.io.File;
import javax.swing.filechooser.*;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory() || f.getName().toLowerCase().matches("^.+.(tiff|tif|gif|jpeg|jpg|png)$")) {
            return true;
        }
		else return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Only Images";
    }
}
