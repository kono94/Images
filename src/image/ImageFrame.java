package image;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageFrame extends JFrame {
	private Vector<BufferedImage> images = new Vector<BufferedImage>();
	private JScrollPane preview;
	private JPanel previewPanel;
	private CenterImage centerImage;
	private Vector<PreviewImage> previewImages = new Vector<PreviewImage>();
	private int switchTime = 500;
	private boolean switchActive;

	public ImageFrame() {
		setSize(1600, 900);
		setLayout(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		previewPanel = new JPanel();
		previewPanel.setLayout(new FlowLayout());
		preview = new JScrollPane(previewPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		preview.setVisible(true);
		add(preview, BorderLayout.SOUTH);
		centerImage = new CenterImage(null);
		add(centerImage, BorderLayout.CENTER);
		new MyFileChooser(this);
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JMenuItem load = new JMenuItem("Load Images");
		load.addActionListener(e -> new MyFileChooser(this));
		setJMenuBar(bar);
		bar.add(menu);
		menu.add(load);
		JMenu switchy = new JMenu("Switchtime");
		JMenuItem increaseHundred = new JMenuItem("+100 ms");
		increaseHundred.addActionListener(e -> switchTime += 100);
		increaseHundred.setAccelerator(KeyStroke.getKeyStroke("control A"));
		JMenuItem decreaseHundred = new JMenuItem("-100 ms");
		decreaseHundred.addActionListener(e -> {
			if (switchTime > 100) {
				switchTime -= 100;
			}
		});
		bar.add(switchy);
		switchy.add(increaseHundred);
		switchy.add(decreaseHundred);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				switchActive = false;
				dispose();
			};
		});
		setVisible(true);
		new ImageSwitch();
	}

	private void addImages() {
		switchActive = false;
		for (int i = 0; i < images.size(); i++) {
			PreviewImage tmp = new PreviewImage(images.get(i));
			previewImages.add(tmp);
			previewPanel.add(tmp);
		}
		preview.revalidate();
		images.removeAllElements();
		switchActive = true;
	}

	public static void main(String[] args) {
		new ImageFrame();
	}

	class PreviewImage extends JComponent {
		private BufferedImage imageToPaint;
		private boolean selected;
		private double ratio;
		private int w, h;

		public PreviewImage(BufferedImage imageToPaint) {
			this.imageToPaint = imageToPaint;
			selected = false;
			ratio = imageToPaint.getWidth() / (double) imageToPaint.getHeight();
			setPreferredSize(new Dimension((int) (100 * ratio), 100));
			w = (getPreferredSize().width - (int) (17 * ratio));
			h = (getPreferredSize().height - 17);
			System.out.println(w);
			System.out.println(h);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						selected = !selected;
						System.out.println(selected);
						previewPanel.repaint();
					} else
						centerImage.imageToPaint = imageToPaint;
					centerImage.repaint();
					previewPanel.repaint();
				}
			});
			setVisible(true);
			repaint();
		}

		public void paintComponent(Graphics g) {
			if (selected) {
				g.setColor(Color.RED);
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(4));
				g.drawImage(imageToPaint, getWidth() / 2 - (w / 2), 0, w, h, this);
				g2.drawRect(2 + getWidth() / 2 - (w / 2), 2, w - 4, h - 4);
			} else {
				if (imageToPaint == centerImage.imageToPaint) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(new BasicStroke(4));
					g.drawImage(imageToPaint, getWidth() / 2 - (w / 2), 0, w, h, this);
					g2.drawRect(2 + getWidth() / 2 - (w / 2), 2, w - 4, h - 4);
				} else {
					g.drawImage(imageToPaint, getWidth() / 2 - (w / 2), 0, w, h, this);
				}
			}
		}
	}

	class MyFileChooser extends JFileChooser {
		public MyFileChooser(JFrame owner) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png");
			setFileFilter(filter);
			setMultiSelectionEnabled(true);
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			super.updateUI();
			showOpenDialog(owner);
			File[] files = getSelectedFiles();
			try {
				for (int i = 0; i < files.length; i++) {
					images.add(ImageIO.read(files[i]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			addImages();
		}
	}

	class CenterImage extends JComponent {
		private BufferedImage imageToPaint;
		private double ratio;

		public CenterImage(BufferedImage imageToPaint) {
			this.imageToPaint = imageToPaint;
			setVisible(true);
			// if (centerImage != null) {
			// ratio = imageToPaint.getWidth()/(double)imageToPaint.getHeight();
			// }
			// setPreferredSize(new Dimension((int) (900*ratio), 900));
			// repaint();
			// }
			//
			// public void updateSize() {
			// if (centerImage != null) {
			// ratio = imageToPaint.getWidth()/(double)imageToPaint.getHeight();
			// }
			// setPreferredSize(new Dimension((int) (900*ratio), 900));
			// repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			if (imageToPaint != null) {
				ratio = imageToPaint.getWidth() / (double) imageToPaint.getHeight();
			}
			g.drawImage(imageToPaint, (int) ((getWidth() / 2) - (getHeight() * ratio / 2)), 0,
					(int) (getHeight() * ratio), getHeight(), this);
		}
	}

	class ImageSwitch implements Runnable {
		@Override
		public void run() {
			while (switchActive) {
				for (int i = 0; i < previewImages.size(); i++) {
					if (previewImages.get(i).selected) {
						centerImage.imageToPaint = previewImages.get(i).imageToPaint;
						centerImage.repaint();
						try {
							Thread.sleep(switchTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					previewPanel.repaint();
				}
			}
		}

		public ImageSwitch() {
			switchActive = true;
			Thread th = new Thread(this);
			th.start();
		}
	}
}
