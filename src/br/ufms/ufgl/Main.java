package br.ufms.ufgl;

import br.ufms.ufgl.core.FrameBuffer;
import br.ufms.ufgl.core.RenderingContext;
import br.ufms.ufgl.core.Texture;
import br.ufms.ufgl.core.Time;
import br.ufms.ufgl.core.VertexFormat;
import br.ufms.ufgl.core.Window;

public class Main {
	public static void main(String[] args) {
		Window win = new Window("Teste", 640, 480, 1);
		FrameBuffer fb = win.getFrameBuffer();
		RenderingContext ctx = RenderingContext.from(fb);

		ctx.getProjection().setPerspective((float) Math.toRadians(60.0f), 640.0f/480.0f, 0.01f, 1000.0f);
				
		final double timeStep = 1.0 / 60.0;
		double startTime = Time.get();
		double accum = 0.0;

		int frames = 0;
		double frameTime = 0.0;

		float t = 0.0f;

		final float S = 0.5f;
		float vertices[] = {
			-S, -S, -S,  0.0f, 0.0f,  0.0f, 1.0f, 1.0f, 1.0f,
			-S, -S,  S,  1.0f, 0.0f,  1.0f, 1.0f, 0.0f, 1.0f,
			-S,  S, -S,  1.0f, 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
			-S,  S,  S,  0.0f, 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
			 S, -S, -S,  0.0f, 0.0f,  1.0f, 0.0f, 0.0f, 1.0f,
			 S, -S,  S,  1.0f, 0.0f,  0.0f, 1.0f, 0.0f, 1.0f,
			 S,  S, -S,  1.0f, 1.0f,  0.0f, 0.0f, 1.0f, 1.0f,
			 S,  S,  S,  0.0f, 1.0f,  1.0f, 1.0f, 0.0f, 1.0f
		};
		int indices[] = {
			0, 6, 4,
			0, 2, 6,
			0, 3, 2,
			0, 1, 3,
			2, 7, 6,
			2, 3, 7,
			4, 6, 7,
			4, 7, 5,
			0, 4, 5,
			0, 5, 1,
			1, 5, 7,
			1, 7, 3
		};
		
		Texture tex = new Texture("pak:/br/ufms/ufgl/bricks.png");
		
		while (win.isVisible()) {
			boolean render = false;

			double currTime = Time.get();
			double delta = currTime - startTime;
			startTime = currTime;

			accum += delta;

			while (accum >= timeStep) {
				if ((frameTime += timeStep) >= 1.0) {
					System.out.println(frames + "fps");
					frameTime = 0;
					frames = 0;
				}
				render = true;
				accum -= timeStep;

				ctx.getModelView().setTranslation(0, 0, -2.0f);
				ctx.getModelView().rotateY((float) timeStep);
				
				t += timeStep;
			}

			if (render) {
				ctx.clear(0.0f, 0.0f, 0.0f, 1.0f);
				
				ctx.setBoundTexture(tex);
				ctx.fillMesh(vertices, indices, VertexFormat.PosUVColor);
				
				win.swapBuffers();
				frames++;
			}
		}
	}
}
