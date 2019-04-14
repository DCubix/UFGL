package br.ufms.ufgl.core;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Twister
 */
public class ScanData {
	
	public int x;
	public Vector4f color;
	public Vector3f normal;
	public Vector2f texCoord;
	
	public ScanData(int x, Vector4f color, Vector3f normal, Vector2f texCoord) {
		this.x = x;
		this.color = color;
		this.normal = normal;
		this.texCoord = texCoord;
	}
	
}
