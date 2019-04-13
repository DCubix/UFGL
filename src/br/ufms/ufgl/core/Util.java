package br.ufms.ufgl.core;

public class Util {
	public static int pack(float r, float g, float b, float a) {
		return (int)(a * 255.0f) << 24 |
				(int)(r * 255.0f) << 16 |
				(int)(g * 255.0f) << 8 |
				(int)(b * 255.0f);
	}
}
