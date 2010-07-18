package tipconv;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

class ImagePanel extends JComponent
{
	public BufferedImage image;
	
	public ImagePanel()
	{
		super();
		image = null;
	}

	public void paint(Graphics g)
	{
		if (image != null) {
			g.setColor(Color.black);
			int mul = 1;
			if (getWidth() > image.getWidth() * 1.5)
				mul = 2;
			g.drawImage(image, 2, 2, image.getWidth() * mul, image.getHeight() * mul, null);
		}
	}
}
