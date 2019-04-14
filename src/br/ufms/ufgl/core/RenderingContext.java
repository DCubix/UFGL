package br.ufms.ufgl.core;

import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Twister
 */
public class RenderingContext {

	// Transformações
	private Matrix4f projection, modelView;
	
	private FrameBuffer frameBuffer;
	private int[] viewport;
	
	private Texture boundTexture;
	
	private ScanData[] scanBuffer;

	private RenderingContext() {
		this.projection = new Matrix4f().identity();
		this.modelView = new Matrix4f().identity();
	}

	public static RenderingContext from(FrameBuffer frameBuffer) {
		if (frameBuffer == null) return null;
		RenderingContext ctx = new RenderingContext();
		ctx.frameBuffer = frameBuffer;
		ctx.viewport = new int[] { 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight() };
		ctx.scanBuffer = new ScanData[frameBuffer.getHeight() * 2]; // min,max
		for (int i = 0; i < ctx.scanBuffer.length; i++) {
			ctx.scanBuffer[i] = new ScanData(-99, null, null, null);
		}
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

	public Matrix4f getProjection() {
		return projection;
	}

	public Matrix4f getModelView() {
		return modelView;
	}

	public Texture getBoundTexture() {
		return boundTexture;
	}

	public void setBoundTexture(Texture boundTexture) {
		this.boundTexture = boundTexture;
	}

	// 3D
	public void fillMesh(float[] data, int[] indices, VertexFormat format) {
		final ArrayList<Vertex> vertices = new ArrayList<>();
		int s = 0;
		while (s < data.length) {
			switch (format) {
				case Pos: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s+1], data[s+2], 1.0f)
					));
					s += 3;
				} break;
				case PosCol: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector4f(data[s + 3], data[s + 4], data[s + 5], data[s + 6])
					));
					s += 7;
				} break;
				case PosNorm: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector3f(data[s + 3], data[s + 4], data[s + 5]),
							new Vector2f(), new Vector4f(1.0f)
					));
					s += 6;
				} break;
				case PosNormUV: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector3f(data[s + 3], data[s + 4], data[s + 5]),
							new Vector2f(data[s + 6], data[s + 7]), new Vector4f(1.0f)
					));
					s += 8;
				} break;
				case PosUV: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector3f(),
							new Vector2f(data[s + 3], data[s + 4]), new Vector4f(1.0f)
					));
					s += 5;
				} break;
				case PosUVColor: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector3f(),
							new Vector2f(data[s + 3], data[s + 4]),
							new Vector4f(data[s + 5], data[s + 6], data[s + 7], data[s + 8])
					));
					s += 9;
				} break;
				case PosNormUVCol: {
					vertices.add(new Vertex(
							new Vector4f(data[s], data[s + 1], data[s + 2], 1.0f),
							new Vector3f(data[s + 3], data[s + 4], data[s + 5]),
							new Vector2f(data[s + 6], data[s + 7]),
							new Vector4f(data[s + 8], data[s + 9], data[s + 10], data[s + 11])
					));
					s += 12;
				}
				break;
			}
		}
		
		final ArrayList<Triangle> triangles = new ArrayList<>();
		for (int i = 0; i < indices.length; i += 3) {
			Vertex v0 = vertices.get(indices[i + 0]);
			Vertex v1 = vertices.get(indices[i + 1]);
			Vertex v2 = vertices.get(indices[i + 2]);
			
			ArrayList<Vertex> verticesProc = triangleProcess(v0, v1, v2);
			for (int j = 0; j < verticesProc.size(); j+=3) {
				Vertex vp0 = verticesProc.get(j + 0);
				Vertex vp1 = verticesProc.get(j + 1);
				Vertex vp2 = verticesProc.get(j + 2);
				
				Triangle tri = createTriangle(vp0, vp1, vp2);
				if (tri != null) {
					triangles.add(tri);
				}
			}
		}
		
		for (Triangle tri : triangles) {
			fillTriangle(tri.v0, tri.v1, tri.v2);
		}
		boundTexture = null;
	}
	
	public void fillTriangle(Vertex min, Vertex mid, Vertex max) {
		Vertex[] verts = new Vertex[] { min, mid, max };
		
		// Ordena os vértices pela posição Y
		Arrays.sort(verts, (Vertex a, Vertex b) -> {
			if (a.getPosition().y < b.getPosition().y) return -1;
			else if (a.getPosition().y > b.getPosition().y) return 1;
			return 0;
		});
		
		min = verts[0];
		mid = verts[1];
		max = verts[2];
		
		final Vector2f e1 = new Vector2f(
				max.getPosition().x - min.getPosition().x,
				max.getPosition().y - min.getPosition().y
		);
		final Vector2f e2 = new Vector2f(
				mid.getPosition().x - min.getPosition().x,
				mid.getPosition().y - min.getPosition().y
		);
		final float area = e1.x * e2.y - e1.y * e2.x; // Área do triângulo
		
		scanConvertTriangle(min, mid, max, area >= 0 ? 1 : 0 /* Determina o "lado" do triângulo */);
		fillShape((int) Math.ceil(min.getPosition().y), (int) Math.ceil(max.getPosition().y));
	}
	
	private static Vertex viewportTransform(final Vertex v, int width, int height) {
		Vertex vv = new Vertex(v);
		vv.getPosition().x = (float) Math.floor(0.5f * width * (v.getPosition().x + 1.0f));
		vv.getPosition().y = (float) Math.floor(0.5f * height * (v.getPosition().y + 1.0f));
		return vv;
	}
	
	private void drawScanBufferS(
			int y,
			int x, Vector4f color, Vector3f normal, Vector2f texCoord,
			int side
	) {
		final int i = y * 2 + side;
		if (i < 0 || i >= scanBuffer.length) return;
		scanBuffer[i].x = x;
		scanBuffer[i].color = color;
		scanBuffer[i].normal = normal;
		scanBuffer[i].texCoord = texCoord;
	}
	
	private void fillShape(int ymin, int ymax) {
		for (int y = ymin; y < ymax; y++) {
			final int i = y * 2;
			final int j = y * 2 + 1;
			if (i < 0 || i >= scanBuffer.length || j < 0 || j >= scanBuffer.length) {
				continue;
			}
			ScanData min = scanBuffer[i];
			ScanData max = scanBuffer[j];
			if (min.x < 0 || max.x < 0) continue;
						
			final float xStep = 1.0f / (float)(max.x - min.x);
			float cx = 0.0f;
			for (int x = min.x; x < max.x; x++) {
				/* INTERPOLAÇÃO DE ATRIBUTOS EM X*/
				final Vector4f col = min.color.lerp(max.color, cx, new Vector4f());
				final Vector3f nrm = min.normal.lerp(max.normal, cx, new Vector3f());
				final Vector2f tex = min.texCoord.lerp(max.texCoord, cx, new Vector2f());
				
				if (boundTexture != null) {
					int u = (int) ((tex.x % 1.0f) * ((boundTexture.getImage().getWidth()-1) + 0.5f));
					int v = (int) ((tex.y % 1.0f) * ((boundTexture.getImage().getHeight()-1) + 0.5f));
					Color tcol = boundTexture.get(u, v);
					col.mul(tcol.r, tcol.g, tcol.b, tcol.a);
				}
				
				pixel(x, y, col.x, col.y, col.z, col.w);
				
				cx += xStep;
			}
		}
	}
	
	private void scanConvertLine(final Vertex min, final Vertex max, int side) {
		int yStart = (int) Math.ceil(min.getPosition().y);
		int yEnd   = (int) Math.ceil(max.getPosition().y);
		
		final Vector4f dist = max.getPosition().sub(min.getPosition(), new Vector4f());
				
		if (dist.y <= 0) return;
		
		float xStep = dist.x / dist.y;
		float yPreStep = yStart - min.getPosition().y;
		float curX = min.getPosition().x + yPreStep * xStep;
		
		final float yStep = 1.0f / (float)(dist.y);
		float curY = 0.0f;
				
		for (int y = yStart; y < yEnd; y++) {
			drawScanBufferS(
					y,
					(int) Math.ceil(curX),
					/* INTERPOLAÇÃO DE ATRIBUTOS EM Y (COR, NORMAL, UV) */
					min.getColor().lerp(max.getColor(), curY, new Vector4f()),
					min.getNormal().lerp(max.getNormal(), curY, new Vector3f()),
					min.getTexCoord().lerp(max.getTexCoord(), curY, new Vector2f()),
					side
			);
			curX += xStep;
			curY += yStep;
		}
	}
	
	private void scanConvertTriangle(final Vertex min, final Vertex mid, final Vertex max, int side) {
		scanConvertLine(min, max, 0 + side);
		scanConvertLine(min, mid, 1 - side);
		scanConvertLine(mid, max, 1 - side);
	}
	
	private Triangle createTriangle(final Vertex min, final Vertex mid, final Vertex max) {
		Triangle tri = new Triangle();
		tri.v0 = min;
		tri.v1 = mid;
		tri.v2 = max;
		
		tri.p0 = new Vector4f(min.getPosition());
		tri.p1 = new Vector4f(mid.getPosition());
		tri.p2 = new Vector4f(max.getPosition());
		
		// Divide por W (perspective divide)
		tri.v0.getPosition().div(tri.v0.getPosition().w);
		tri.v1.getPosition().div(tri.v1.getPosition().w);
		tri.v2.getPosition().div(tri.v2.getPosition().w);
		
		// Backface Culling
		Vector4f vs1 = tri.v1.getPosition().sub(tri.v0.getPosition(), new Vector4f());
		Vector4f vs2 = tri.v2.getPosition().sub(tri.v0.getPosition(), new Vector4f());
		
		Vector3f vt0p = new Vector3f(
				tri.v0.getPosition().x,
				tri.v0.getPosition().y,
				tri.v0.getPosition().z
		);
		
		Vector3f _vs1 = new Vector3f(vs1.x, vs1.y, vs1.z);
		Vector3f _vs2 = new Vector3f(vs2.x, vs2.y, vs2.z);
		
		Vector3f eye = modelView.getColumn(3, new Vector3f());
		Vector3f tV = vt0p.sub(eye).normalize();
		Vector3f tN = _vs1.cross(_vs2);
		
		if (tN.dot(tV) <= 0.0f) return null;
		//
		
		int w = viewport[2] - viewport[0];
		int h = viewport[3] - viewport[1];
		tri.v0 = viewportTransform(tri.v0, w, h);
		tri.v1 = viewportTransform(tri.v1, w, h);
		tri.v2 = viewportTransform(tri.v2, w, h);
		
		return tri;
	}
	
	private ArrayList<Vertex> triangleProcess(final Vertex min, final Vertex mid, final Vertex max) {
		final Matrix4f mvp = projection.mul(modelView, new Matrix4f());
		final ArrayList<Vertex> triangles = new ArrayList<>();
		
		triangles.add(min.transform(mvp));
		triangles.add(mid.transform(mvp));
		triangles.add(max.transform(mvp));
		
		// TODO: Clipping
		
		return triangles;
	}
	
}
