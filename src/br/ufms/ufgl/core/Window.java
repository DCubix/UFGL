package br.ufms.ufgl.core;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Twister
 */
public class Window extends JFrame {

	private final Canvas canvas;

	private FrameBuffer boundBuffer;
	private final FrameBuffer defaultBuffer;

	public Window(String title, int width, int height, int downScale) {
		super(title);

		downScale = Math.min(Math.max(downScale, 1), 8);

		this.defaultBuffer = new FrameBuffer(width / downScale, height / downScale, true);
		this.boundBuffer = defaultBuffer;

		final Dimension size = new Dimension(width, height);

		JPanel _panel = new JPanel(new BorderLayout());
		this.canvas = new Canvas();
		canvas.setBackground(java.awt.Color.BLACK);
		canvas.setSize(size);
		canvas.setPreferredSize(size);
		_panel.setSize(size);
		_panel.setPreferredSize(size);
		_panel.add(canvas);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setPreferredSize(size);
		getContentPane().setSize(size);
		getContentPane().add(_panel);
		pack();

		setVisible(true);
		setResizable(false);
		setSize(size);
		setPreferredSize(size);
	}

	public void swapBuffers() {
		BufferStrategy bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(2);
			bs = canvas.getBufferStrategy();
		}
		Graphics g = bs.getDrawGraphics();
		g.drawImage(
				boundBuffer.getImage(), 0, 0,
				getWidth(), getHeight(), null
		);
		g.dispose();
		bs.show();
	}

	public void bindBuffer(FrameBuffer frameBuffer) {
		if (frameBuffer == null) frameBuffer = defaultBuffer;
		boundBuffer = frameBuffer;
	}

	public FrameBuffer getFrameBuffer() {
		return boundBuffer;
	}

}
