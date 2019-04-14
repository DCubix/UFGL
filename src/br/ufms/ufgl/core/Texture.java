package br.ufms.ufgl.core;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Twister
 */
public class Texture {

	private BufferedImage image;
	private int[] pixels;
	
	public Texture(String path) {
		InputStream is = null;
		if (path.toLowerCase().startsWith("pak:")) {
			is = Texture.class.getResourceAsStream(path.substring(4));
		} else {
			try {
				is = new FileInputStream(path);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		if (is != null) {
			try {
				image = convert(ImageIO.read(is));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		}
	}
	
	private static final Color GetColor = new Color(0, 0, 0, 0);

	public Color get(int x, int y) {
		if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
			return null;
		}
		int i = (x + y * image.getWidth());
		int a = (pixels[i] & 0xFF000000) >> 24;
		int r = (pixels[i] & 0x00FF0000) >> 16;
		int g = (pixels[i] & 0x0000FF00) >> 8;
		int b = (pixels[i] & 0x000000FF);
		if (a < 0) a += 256;
		GetColor.r = (float) r / 255.0f;
		GetColor.g = (float) g / 255.0f;
		GetColor.b = (float) b / 255.0f;
		GetColor.a = (float) a / 255.0f;
		return GetColor;
	}

	public void set(int x, int y, float r, float g, float b, float a) {
		set(
				x, y,
				(int) (r * 255.0f),
				(int) (g * 255.0f),
				(int) (b * 255.0f),
				(int) (a * 255.0f)
		);
	}

	public void set(int x, int y, float z, float r, float g, float b, float a) {
		set(
				x, y,
				z,
				(int) (r * 255.0f),
				(int) (g * 255.0f),
				(int) (b * 255.0f),
				(int) (a * 255.0f)
		);
	}

	public void set(int x, int y, int r, int g, int b, int a) {
		if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
			return;
		}
		int i = (x + y * image.getWidth());
		r = Math.min(Math.max(r, 0), 255);
		g = Math.min(Math.max(g, 0), 255);
		b = Math.min(Math.max(b, 0), 255);
		a = Math.min(Math.max(a, 0), 255);
		final int argb
				= (a & 0xFF) << 24
				| (r & 0xFF) << 16
				| (g & 0xFF) << 8
				| (b & 0xFF);
		if (pixels[i] != argb) {
			pixels[i] = argb;
		}
	}
	
	private static BufferedImage convert(BufferedImage img) {
		BufferedImage cimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = cimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		img.flush();
		return cimg;
	}

	public BufferedImage getImage() {
		return image;
	}
	
}
