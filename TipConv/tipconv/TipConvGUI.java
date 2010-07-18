/*
 * TipConvGUI.java - very simple GUI for TipConv
 *
 * Copyright (C) 2008-2010  Adrian Matoga
 *
 * This file is part of TipConv,
 * see http://epi.atari8.info/tiptools.php
 *
 * TipConv is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * TipConv is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TipConv; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package tipconv;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.util.prefs.*;

class TipConvGUI extends JFrame implements ActionListener
{
	private ImagePanel srcImagePanel;
	private ImagePanel destImagePanel;
	private JLabel statLabel;

	private BufferedImage srcImage;
	private AtariImage atariImage;
	
	private Converter.Format prevFormat;
	private Converter.Resize prevResizeMode;

	private Converter converter;
	private int[][] dispPalette;

	private String convPaletteName;

	private File srcFile, destFile;

	public TipConvGUI()
	{
		super("TipConv 1.1.1");
		setSize(652, 292);

		converter = new Converter();
		convPaletteName = "Jakub.act";

		converter.setPalette("Palette" + File.separator + convPaletteName);
		dispPalette = Converter.readPalette("Palette" + File.separator + convPaletteName, 1);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar;
		JMenu menu;
		JMenu subMenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;

		// pasek menu
		menuBar = new JMenuBar();

		// menu plik
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// elementy menu plik
		menuItem = new JMenuItem("Open...", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("openfile");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save as...", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		menuItem.setActionCommand("savefile");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();
		
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// menu obrazek
		menu = new JMenu("Image");
		menu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(menu);
		
		// elementy menu obrazek
		menuItem = new JMenuItem("Convert", KeyEvent.VK_C);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		menuItem.setActionCommand("convert");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// submenu z wyborem formatu poprzez radio buttony
		subMenu = new JMenu("Format");
		subMenu.setMnemonic(KeyEvent.VK_F);
		ButtonGroup group1 = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("TIP 0.1");
		if (converter.getFormat() == Converter.Format.TIP01)
			rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_T);
		rbMenuItem.setActionCommand("setformattip01");
		rbMenuItem.addActionListener(this);
		group1.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		rbMenuItem = new JRadioButtonMenuItem("HIP");
		if (converter.getFormat() == Converter.Format.HIP)
			rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_H);
		rbMenuItem.setActionCommand("setformathip");
		rbMenuItem.addActionListener(this);
		group1.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		menu.add(subMenu);
		
		// submenu z wyborem sposobu resizowania
		subMenu = new JMenu("Resize");
		subMenu.setMnemonic(KeyEvent.VK_R);
		ButtonGroup group2 = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("Fit");
		if (converter.getResizeMode() == Converter.Resize.FIT)
			rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_F);
		rbMenuItem.setActionCommand("setresizefit");
		rbMenuItem.addActionListener(this);
		group2.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		rbMenuItem = new JRadioButtonMenuItem("Crop to fill");
		if (converter.getResizeMode() == Converter.Resize.CROP)
			rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_C);
		rbMenuItem.setActionCommand("setresizecrop");
		rbMenuItem.addActionListener(this);
		group2.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		rbMenuItem = new JRadioButtonMenuItem("Stretch to fill");
		if (converter.getResizeMode() == Converter.Resize.STRETCH)
			rbMenuItem.setSelected(true);
		rbMenuItem.setActionCommand("setresizestretch");
		rbMenuItem.addActionListener(this);
		rbMenuItem.setMnemonic(KeyEvent.VK_S);
		group2.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		menu.add(subMenu);

		// menu paleta
		menu = new JMenu("Palette");
		menu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(menu);
		
		// elementy menu paleta
		// submenu z wyborem palety konwersji poprzez radio buttony
		menu.add(generatePaletteMenu("Conversion"));
		menu.add(generatePaletteMenu("Display"));

		menu.addSeparator();

		// submenu z wyborem nasycenia palety konwersji
		subMenu = new JMenu("Saturation");
		subMenu.setMnemonic(KeyEvent.VK_S);
		String s[] = { "10", "15", "20", "25", "30", "40", "60", "80" };
		ButtonGroup group3 = new ButtonGroup();
		for (int i = 0; i < 8; i++) {
			rbMenuItem = new JRadioButtonMenuItem("x" + s[i].charAt(0) + "." + s[i].charAt(1));
			if (s[i] == "40") rbMenuItem.setSelected(true);
			rbMenuItem.setActionCommand("satfactor" + s[i]);
			rbMenuItem.addActionListener(this);
			group3.add(rbMenuItem);
			subMenu.add(rbMenuItem);
		}
		menu.add(subMenu);

		// submenu z wyborem zrodla jasnosci
		subMenu = new JMenu("Brightness");
		subMenu.setMnemonic(KeyEvent.VK_B);
		ButtonGroup group4 = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("Palette");
		rbMenuItem.setSelected(true);
		rbMenuItem.setActionCommand("brightnesspalette");
		rbMenuItem.addActionListener(this);
		group4.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		rbMenuItem = new JRadioButtonMenuItem("Linear");
		rbMenuItem.setActionCommand("brightnesslinear");
		rbMenuItem.addActionListener(this);
		group4.add(rbMenuItem);
		subMenu.add(rbMenuItem);
		menu.add(subMenu);

		// menu pomoc
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		// elementy menu pomoc
		menuItem = new JMenuItem("HOWTO", KeyEvent.VK_H);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menuItem.setActionCommand("howtodialog");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("About...", KeyEvent.VK_A);
		menuItem.setActionCommand("aboutdialog");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// ustawienie paska menu i ikony
		setJMenuBar(menuBar);
		setIconImage(new ImageIcon("icons" + File.separator + "logo32x32.png").getImage());

		// panel z obrazkiem zrodlowym i docelowym
		JPanel panel = new JPanel(new GridLayout(0, 2));
		srcImagePanel = new ImagePanel();
		panel.add(srcImagePanel);
		srcImagePanel.setSize(324, 242);
		destImagePanel = new ImagePanel();
		panel.add(destImagePanel);
		destImagePanel.setSize(324, 242);

		Container contentPane = getContentPane();
		contentPane.add(panel, BorderLayout.CENTER);

		// i na koniec etykietka statusu
		statLabel = new JLabel("Ready");
		statLabel.setSize(600, 20);
		contentPane.add(statLabel, BorderLayout.PAGE_END);
		
		setVisible(true);
	}

	private JMenu generatePaletteMenu(String name)
	{
		JMenu menu = new JMenu(name);
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem menuItem;
		int i;
		String[] t = new File("Palette" + File.separator).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().matches("^.+\\.act$");
			}
		});
		for (i = 0; i < t.length; i++) {
			menuItem = new JRadioButtonMenuItem(t[i]);
			if (t[i].equalsIgnoreCase(convPaletteName)) menuItem.setSelected(true);
			menuItem.setActionCommand(name + "palette" + t[i]);
			menuItem.addActionListener(this);
			group.add(menuItem);
			menu.add(menuItem);
		}
		return menu;
	}

	public void actionPerformed(ActionEvent e)
	{
		String c = e.getActionCommand();

		if (c.equals("exit")) System.exit(0);
		else if (c.equals("setformattip01"))
			converter.setFormat(Converter.Format.TIP01);
		else if (c.equals("setformathip"))
			converter.setFormat(Converter.Format.HIP);
		else if (c.equals("setresizefit"))
			converter.setResizeMode(Converter.Resize.FIT);
		else if (c.equals("setresizecrop"))
			converter.setResizeMode(Converter.Resize.CROP);
		else if (c.equals("setresizestretch"))
			converter.setResizeMode(Converter.Resize.STRETCH);
		else if (c.equals("howtodialog")) {
			JOptionPane.showMessageDialog(
				this,
				TipConv.getHowTo(),
				"TipConv HOWTO",
				JOptionPane.INFORMATION_MESSAGE);
		}
		else if (c.equals("aboutdialog")) {
			JOptionPane.showMessageDialog(
				this,
				TipConv.getInfo(),
				"About TipConv",
				JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon("icons" + File.separator + "logo32x32.png"));
		}
		else if (c.equals("convert")) {
			if (prevResizeMode != converter.getResizeMode() || prevFormat != converter.getFormat()) {
				srcImagePanel.image = createSrcPreview(srcImage);
			}
			atariImage = converter.convert((BufferedImage)srcImagePanel.image);
			if (atariImage != null) {
				statLabel.setText("" + atariImage);
				destImagePanel.image = atariImage.paintImage(dispPalette);
			}
			repaint();
		}
		else if (c.equals("openfile")) {
			// wybieraczka plikow, z opcja tylko obrazki
			Preferences prefs = Preferences.userNodeForPackage(TipConvGUI.class);
			String path = prefs.get("open_path", null);
			JFileChooser fc = new JFileChooser(path);
			ImageFilter imgf = new ImageFilter();
			fc.addChoosableFileFilter(imgf);
			fc.setAcceptAllFileFilterUsed(true);
			fc.setFileFilter(imgf);
			// wyswietlamy wybieraczke i wczytujemy plik, jesli user jakis wybral
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				prefs.put("open_path", fc.getCurrentDirectory().getAbsolutePath());
				srcFile = fc.getSelectedFile();
				try {
					srcImage = null;
					srcImage = ImageIO.read(srcFile);
					srcImagePanel.image = createSrcPreview(srcImage);
					statLabel.setText("Source image: " + srcFile.getName() + " (" +
									  srcImage.getWidth(null) + "x" +
									  srcImage.getHeight(null) + ")");
				} catch (IOException ex) {
					statLabel.setText("Error loading image: " + srcFile.getName());
					JOptionPane.showMessageDialog(this, ex.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
				}
				repaint();
			}
		}
		else if (c.equals("savefile")) {
			try {
				File destFile = new File(srcFile.getParent() + File.separator +
										 srcFile.getName().replaceFirst("\\..*$", atariImage.getExtension()));
				System.out.println("" + destFile);
				JFileChooser fc = new JFileChooser(destFile.getParentFile());
				fc.setSelectedFile(destFile);
				if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					FileOutputStream fo = new FileOutputStream(fc.getSelectedFile());
					fo.write(atariImage.saveImage());
					fo.close();
				}
			} catch (NullPointerException ex) {
				JOptionPane.showMessageDialog(this, "Nothing to save", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (c.startsWith("Displaypalette")) {
			dispPalette = Converter.readPalette("Palette" + File.separator + c.substring(14), 1);
			if (atariImage != null) {
				destImagePanel.image = atariImage.paintImage(dispPalette);
				repaint();
			}
		}
		else if (c.startsWith("Conversionpalette")) {
			convPaletteName = c.substring(17);
			converter.setPalette("Palette" + File.separator + convPaletteName);
		}
		else if (c.startsWith("satfactor"))
			converter.setSatFactor(new Float(c.substring(9))/10);
		else if (c.equals("brightnesspalette"))
			converter.setBrightnessMode(Converter.Brightness.PALETTE);
		else if (c.equals("brightnesslinear"))
			converter.setBrightnessMode(Converter.Brightness.LINEAR);
	}	

	private BufferedImage createSrcPreview(BufferedImage src)
	{
		prevResizeMode = converter.getResizeMode();
		prevFormat = converter.getFormat();
		return converter.prepare(src);
	}
}
