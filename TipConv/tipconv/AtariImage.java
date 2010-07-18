package tipconv;
import java.awt.*;
import java.awt.image.*;

interface AtariImage
{
	BufferedImage paintImage(int[][] palette);
	byte[] saveImage();
	String getExtension();
}
