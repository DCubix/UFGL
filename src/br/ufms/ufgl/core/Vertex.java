package br.ufms.ufgl.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Twister
 */
public class Vertex {
	
	private Vector4f position, color;
	private Vector3f normal;
	private Vector2f texCoord;

	public Vertex() {
		this.position = new Vector4f();
		this.color = new Vector4f(1.0f);
		this.normal = new Vector3f();
		this.texCoord = new Vector2f();
	}
	
	public Vertex(Vertex v) {
		this.position = v.position;
		this.color = v.color;
		this.normal = v.normal;
		this.texCoord = v.texCoord;
	}
	
	public Vertex(Vector4f position, Vector3f normal, Vector2f texCoord, Vector4f color) {
		this.position = position;
		this.color = color;
		this.normal = normal;
		this.texCoord = texCoord;
	}
	
	public Vertex(Vector4f position) {
		this();
		this.position = position;
	}
	
	public Vertex(Vector4f position, Vector4f color) {
		this();
		this.position = position;
		this.color = color;
	}
	
	public Vertex lerp(Vertex b, float t) {
		return new Vertex(
				position.lerp(b.position, t, new Vector4f()),
				normal.lerp(b.normal, t, new Vector3f()),
				texCoord.lerp(b.texCoord, t, new Vector2f()),
				color.lerp(b.color, t, new Vector4f())
		);
	}
	
	public Vertex transform(Matrix4f mat) {
		Vertex v = new Vertex();
		v.position = mat.transform(position, new Vector4f());
		v.normal = mat.transformDirection(normal, new Vector3f());
		v.color = color;
		v.texCoord = texCoord;
		return v;
	}
	
	public Vector4f getPosition() {
		return position;
	}

	public Vector4f getColor() {
		return color;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

}
