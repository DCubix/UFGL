package br.ufms.ufgl.core;

/**
 *
 * @author Twister
 */
public class Color {
	public float r, g, b, a;

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(int r, int g, int b, int a) {
		this.r = (float) r / 255.0f;
		this.g = (float) g / 255.0f;
		this.b = (float) b / 255.0f;
		this.a = (float) a / 255.0f;
	}

}
