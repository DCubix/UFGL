package br.ufms.ufgl;

import br.ufms.ufgl.core.FrameBuffer;
import br.ufms.ufgl.core.RenderingContext;
import br.ufms.ufgl.core.Window;

public class Main {
	public static void main(String[] args) {
		Window win = new Window("Teste", 640, 480, 2);
		FrameBuffer fb = win.getFrameBuffer();
		RenderingContext ctx = RenderingContext.from(fb);

		final double timeStep = 1.0 / 60.0;
		double startTime = System.currentTimeMillis() / 1000.0;
		double accum = 0.0;

		int frames = 0;
		double frameTime = 0.0;

		float t = 0.0f;

		while (win.isVisible()) {
			boolean render = false;

			double currTime = System.currentTimeMillis() / 1000.0;
			double delta = currTime - startTime;
			startTime = currTime;

			accum += delta;

			while (accum >= timeStep) {
				float dt = (float) Math.min(timeStep, delta);
				if ((frameTime += dt) >= 1.0) {
					System.out.println(frames + "fps");
					frameTime = 0;
					frames = 0;
				}
				render = true;
				accum -= timeStep;

				t += dt;
			}

			if (render) {
				float k = (float) Math.sin(t) * 0.5f + 0.5f;
				for (int y = 0; y < fb.getHeight(); y++) {
					float fy = (float)y / fb.getWidth();
					//ctx.line(0, y, fb.getWidth(), y, fy * (1.0f - k), (1.0f - fy) * k, 0.0f, 1.0f);
				}
				win.swapBuffers();
				frames++;
			}
		}
	}
}
