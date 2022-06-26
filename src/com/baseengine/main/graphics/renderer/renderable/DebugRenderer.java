package com.baseengine.main.graphics.renderer.renderable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.baseengine.main.graphics.Camera;
import com.baseengine.main.graphics.renderer.Renderer;
import com.baseengine.main.graphics.renderer.model.Model;
import com.baseengine.main.primitive.Assets;
import com.baseengine.main.primitive.Shader;
import com.baseengine.main.primitive.Texture;
import com.baseengine.main.utility.DebugStatistics;




public class DebugRenderer extends Renderable
{
	private static final int MAX_BATCH_SIZE = 10000;
	
	// Vertex
	// =========
	// Pos                 Color
	// float, float        float, float, float
	private final int POS_SIZE = 2;
	private final int COLOR_SIZE = 3;
	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	
	private final int VERTEX_SIZE = 5;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE* Float.BYTES;
	
	private float[] vertices;
	
	private int debugLineCount = 0;
	
	private Shader shader;
	
	public DebugRenderer() {
		shader = Assets.getShader(Renderer.DEBUG_SHADER_PATH);
		
		vertices = new float[MAX_BATCH_SIZE * 2 * VERTEX_SIZE];
	}
	
	public void start() {
		//VAO
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
		
		eboId = glGenBuffers();
		int[] indices = generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
	}
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		
		shader.use();
		
		shader.uploadMat4f("uCameraMatrix", Camera.get().getRendererMatrix());
		
		glBindVertexArray(vaoId);
		
		glDrawElements(GL_LINES, debugLineCount * 2, GL_UNSIGNED_INT, 0);
		DebugStatistics.countDrawCall();
		
		glBindVertexArray(0);
	
		shader.detach();
		
		debugLineCount = 0;
	}
	
	public void drawLine(Vector2f start, Vector2f end, Vector3f color) {
		int index = debugLineCount * 2 * VERTEX_SIZE;
		
		if (index >= vertices.length) {
			return;
		}
		
		//added .1f helps geometry not overlap
		float add = 0.001f;
		vertices[index] = start.x + add;
		vertices[index + 1] = start.y + add;
		
		vertices[index + 2] = color.x;
		vertices[index + 3] = color.y;
		vertices[index + 4] = color.z;
	
		vertices[index + 5] = end.x + add;
		vertices[index + 6] = end.y + add;
		
		vertices[index + 7] = color.x;
		vertices[index + 8] = color.y;
		vertices[index + 9] = color.z;
		
		debugLineCount++;
		
	}
	
	public void drawPoint(Vector2f location, Vector3f color) {
		drawLine(location, new Vector2f().set(location).add(0, 0.02f), color);
	}
	
	/**
	 * @param center
	 * @param radius
	 * @param numLines
	 */
	public void drawCircle(Vector2f center, float radius, int numLines, Vector3f color) {
		int index = debugLineCount * 2 * VERTEX_SIZE;
		
		if (index + numLines * 2 * VERTEX_SIZE >= vertices.length) {
			return;
		}
		
		Vector3f radialVector = new Vector3f(radius, 0, 0);
		
		float angle = (float) Math.toRadians(360 / numLines);
		
		Vector2f pos1 = new Vector2f();
		Vector2f pos2 = new Vector2f();
		
		for (int i = 0; i < numLines; i++) {
			//Add current position (start)
			pos1.set(center.x + radialVector.x, center.y + radialVector.y);
			
			//Rotate around normal
			radialVector.rotateAxis(angle, 0, 0, 1);
			
			
			//Add new position (end)
			pos2.set(center.x + radialVector.x, center.y + radialVector.y);
			
			//Draw the line defined by those positions
			drawLine(pos1, pos2, color);
		}
	}
	
	private int[] generateIndices() {
		int[] elements = new int[2 * MAX_BATCH_SIZE];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = i;
		}
		
		return elements;
	}

	@Override
	public void preRender()
	{

	}

	@Override
	public void postRender()
	{

	}

	@Override
	public boolean addModel(Model m)
	{
		return false;
	}

	@Override
	public boolean removeModel(Model m)
	{
		return false;
	}

	@Override
	public boolean containsModel(Model m)
	{
		return false;
	}

	@Override
	public boolean hasRoom()
	{
		return false;
	}

	@Override
	public boolean hasTexture(Texture t)
	{
		return false;
	}

	@Override
	public boolean hasTextureRoom()
	{
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
