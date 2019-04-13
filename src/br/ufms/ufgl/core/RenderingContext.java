package br.ufms.ufgl.core;

import java.util.Arrays;

public class RenderingContext {

	private FrameBuffer frameBuffer;
	private int[] viewport;

	private RenderingContext() {}

	public static RenderingContext from(FrameBuffer frameBuffer) {
		if (frameBuffer == null) return null;
		RenderingContext ctx = new RenderingContext();
		ctx.frameBuffer = frameBuffer;
		ctx.viewport = new int[] { 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight() };
		return ctx;
	}

	public void clear(float r, float g, float b, float a) {
		Arrays.fill(frameBuffer.getPixels(), Util.pack(r, g, b, a));
	}

	public void clearDepth(float z) {
		Arrays.fill(frameBuffer.getZBuffer(), z);
	}

	public void pixel(int x, int y, float r, float g, float b, float a) {
		if (x < viewport[0] || x > viewport[0]+viewport[2] ||
			y < viewport[1] || y > viewport[1]+viewport[3]) return;
		frameBuffer.set(x, y, r, g, b, a);
	}

	public void line(int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
		int dx = Math.abs(x2 - x1);
		int sx = x1 < x2 ? 1 : -1;
		int dy = Math.abs(y2 - y1);
		int sy = y1 < y2 ? 1 : -1;
		int err = dx + dy;
		int e2 = 0;

		int x = x1;
		int y = y1;

		while (true) {
			pixel(x, y, r, g, b, a);

			if (x == x2 && y == y2) break;
			e2 = 2 * err;
			if (e2 >= dy) { err += dy; x += sx; }
			if (e2 <= dx) { err += dx; y += sy; }
		}
	}

	

}
