package br.ufms.ufgl.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class FrameBuffer {

	private BufferedImage image;
	private int[] pixels;

	private float[] zbuffer;

	private int width, height;
	private boolean zbuffering;

	public FrameBuffer(int width, int height, boolean zbuffering) {
		this.width = width;
		this.height = height;
		this.zbuffering = zbuffering;

		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.zbuffer = new float[width * height];

		Arrays.fill(zbuffer, Float.MAX_VALUE);
	}

	private static final Color GetColor = new Color(0, 0, 0, 0);
	public Color get(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return null;
		int i = (x + y * width);
		int a = (pixels[i] & 0xFF000000) >> 24;
		int r = (pixels[i] & 0x00FF0000) >> 16;
		int g = (pixels[i] & 0x0000FF00) >> 8;
		int b = (pixels[i] & 0x000000FF);
		GetColor.r = (float) r / 255.0f;
		GetColor.g = (float) g / 255.0f;
		GetColor.b = (float) b / 255.0f;
		GetColor.a = (float) a / 255.0f;
		return GetColor;
	}

	public void set(int x, int y, float r, float g, float b, float a) {
		set(
				x, y,
				(int)(r * 255.0f),
				(int)(g * 255.0f),
				(int)(b * 255.0f),
				(int)(a * 255.0f)
		);
	}

	public void set(int x, int y, float z, float r, float g, float b, float a) {
		set(
				x, y,
				z,
				(int)(r * 255.0f),
				(int)(g * 255.0f),
				(int)(b * 255.0f),
				(int)(a * 255.0f)
		);
	}

	public void set(int x, int y, int r, int g, int b, int a) {
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		int i = (x + y * width);
		r = Math.min(Math.max(r, 0), 255);
		g = Math.min(Math.max(g, 0), 255);
		b = Math.min(Math.max(b, 0), 255);
		a = Math.min(Math.max(a, 0), 255);
		final int argb =
				(a & 0xFF) << 24 |
				(r & 0xFF) << 16  |
				(g & 0xFF) << 8  |
				(b & 0xFF);
		if (pixels[i] != argb)
			pixels[i] = argb;
	}

	public void set(int x, int y, float z, int r, int g, int b, int a) {
		if (!zbuffering) {
			set(x, y, r, g, b, a);
		} else {
			if (z < zbuffer[x + y * width]) {
				set(x, y, r, g, b, a);
				zbuffer[x + y * width] = z;
			}
		}
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		this.image.flush();
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.zbuffer = new float[width * height];

		Arrays.fill(zbuffer, Float.MAX_VALUE);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isZBuffering() {
		return zbuffering;
	}

	public void setZBuffering(boolean zbuffering) {
		this.zbuffering = zbuffering;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int[] getPixels() {
		return pixels;
	}

	public float[] getZBuffer() {
		return zbuffer;
	}

}
