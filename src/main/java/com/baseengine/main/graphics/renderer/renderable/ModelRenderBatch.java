package main.java.com.baseengine.main.graphics.renderer.renderable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector4f;

import main.java.com.baseengine.main.graphics.Camera;
import main.java.com.baseengine.main.graphics.renderer.Renderer;
import main.java.com.baseengine.main.graphics.renderer.model.Model;
import main.java.com.baseengine.main.graphics.renderer.renderable.utils.IndexArrayUtils;
import main.java.com.baseengine.main.primitive.Assets;
import main.java.com.baseengine.main.primitive.Shader;
import main.java.com.baseengine.main.primitive.Texture;
import main.java.com.baseengine.main.utility.DebugStatistics;



public class ModelRenderBatch extends Renderable
{
	private static final int MAX_BATCH_SIZE = 2048;
	
	// Vertex
	// =========
	// Pos                        Color                          TexCoords         TexId
	// float, float, float        float, float, float, float     float, float      float
	private final int POS_SIZE = 3;
	private final int COLOR_SIZE = 4;
	private final int TEX_COORD_SIZE = 2;
	private final int TEX_ID_SIZE = 1;
	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private final int TEX_ID_OFFSET = TEX_COORD_OFFSET + TEX_COORD_SIZE * Float.BYTES;
	
	private final int VERTEX_SIZE = 10;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE* Float.BYTES;
	
	private Model[] models;
	private Map<UUID, Model> modelLookup;
	private int modelCount;
	private boolean hasRoom;
	private float[] vertices;
	private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
	
	private List<Texture> textures;
	private Shader shader;
	
	private boolean rebufferData = true;
	
	Vector4f[] vector4Array = { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()};
	
	public ModelRenderBatch() {
		shader = Assets.getShader(Renderer.MODEL_SHADER_PATH);
		
		models = new Model[MAX_BATCH_SIZE];
		modelLookup = new HashMap<>();
		vertices = new float[MAX_BATCH_SIZE * 4 * VERTEX_SIZE];
		
		this.modelCount = 0;
		this.hasRoom = true;
		
		textures = new ArrayList<>();
	}
	
	public void start() {
		//VAO
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
		
		eboId = glGenBuffers();
		int[] indices = IndexArrayUtils.generateIndicesForQuads(MAX_BATCH_SIZE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
		glEnableVertexAttribArray(3);
	}
	
	public boolean addModel(Model m) {
		if (!(m instanceof Model)) {
			System.err.println("Tried to add model of the wrong model type to batch");
			return false;
		}
		
		Model fm = (Model) m;
		
		int index = modelCount;
		models[index] = fm;
		modelLookup.put(m.getUUID(), fm);
		modelCount++;
		
		if (fm.getTexture() != null) {
			if (!textures.contains(fm.getTexture())) {
				textures.add(fm.getTexture());
			}
		}
		
		loadVertexProperties(index);
		
		if (modelCount >= MAX_BATCH_SIZE) {
			hasRoom = false;
		}
		
		return true;
	}
	
	public boolean removeModel(Model m) {
		if (!(m instanceof Model)) {
			System.err.println("Tried to remove model of the wrong model type from batch");
			return false;
		}
		
		if (modelLookup.containsKey(m.getUUID())) {
			for(int i = 0; i < modelCount; i++) {
				if (models[i].getUUID().equals(m.getUUID())) {
					for (int j = i; j < modelCount - 1; j++) {
						models[j] = models[j+1];
						models[j].setDirty();
					}
					hasRoom = true;
					modelCount--;
					modelLookup.remove(m.getUUID());
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void preRender() {
		shader.use();
		if (Camera.get().hasMatrixChanged()) {
			shader.uploadMat4f("uCameraMatrix", Camera.get().getRendererMatrix());
		}

		shader.uploadIntArray("uTextures", texSlots);
	}


	
	public void render() {
		for (int i = 0; i < modelCount; i++) {
			Model fm = models[i];
			if (fm.isDirty()) {
				loadVertexProperties(i);
				fm.setClean();
				rebufferData = true;
			}
		}

		if (rebufferData) {
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
			rebufferData = false;
		}
		
		for (int i = 0; i < textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + Renderer.FB_TEXTURE_OFFSET);
			textures.get(i).bind();
		}
		
		glBindVertexArray(vaoId);
		
		glDrawElements(GL_TRIANGLES, this.modelCount * 6, GL_UNSIGNED_INT, 0);
		DebugStatistics.countDrawCall();
		
		glBindVertexArray(0);
	}
	
	public void postRender() {
		for (int i = 0; i < textures.size(); i++) {
			textures.get(i).unbind();
		}
		
		shader.detach();
	}
	
	private void loadVertexProperties(int index) {
		int offset = index * 4 * VERTEX_SIZE;
		
		Model model = models[index];
		
		Vector2f position = new Vector2f();
		Vector2f size = new Vector2f();
		int drawDepth = 0;
		Vector4f color = new Vector4f();
		Vector2f[] texCoords = { new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f() };
		int texId = 0;
		float rotation = 0;

		if (model.getSprite() != null){
		
			position = model.getPosition();
			size = model.getSize();
			drawDepth = model.getDrawDepth();
			color = model.getColor();
			texCoords = model.getSprite().getTexCoords();
			
			texId = 0;
			if (model.getTexture() != null) {
				for (int i = 0; i < textures.size(); i++) {
					if (textures.get(i) == model.getTexture()) {
						texId = i + Renderer.FB_TEXTURE_OFFSET;
						break;
					}
				}
			}
		}
		
		Matrix4f transform = new Matrix4f().identity();
		transform = transform.translate(position.x, position.y, drawDepth);
		
		vector4Array[0].set(0 + size.x, 0, 0, 1.0f);
		vector4Array[1].set(0 + size.x, 0 + size.y, 0, 1.0f);
		vector4Array[2].set(         0, 0 + size.y, 0, 1.0f);
		vector4Array[3].set(         0, 0, 0, 1.0f);
		
		//Apply transformations
		transform = transform.translate(size.x / 2, size.y / 2, 0); //Move origin
		
		rotation = model.getRotation();
		
		if (model.getMirroredHorizontal()) {
			transform.scale(-1.0f, 1.0f, 1.0f);
		}
		
		if (model.getMirroredVertical()) {
			transform.scale(1.0f, -1.0f, 1.0f);
		}
		
		Quaternionf quat = new Quaternionf();
		quat.rotateXYZ(0, 0, rotation);
		transform.rotate(quat);
		
		transform = transform.translate(-size.x / 2, -size.y / 2, 0); //Reset origin
		
		for (int i = 0; i < 4; i++) {
			Vector4f prod = vector4Array[i].mul(transform);

			vertices[offset] = prod.x;
			vertices[offset + 1] = prod.y;
			vertices[offset + 2] = prod.z;
			
			
			vertices[offset + 3] = color.x;
			vertices[offset + 4] = color.y;
			vertices[offset + 5] = color.z;
			vertices[offset + 6] = color.w;
			
			vertices[offset + 7] = texCoords[i].x;
			vertices[offset + 8] = texCoords[i].y;
			vertices[offset + 9] = texId;
			
			offset += VERTEX_SIZE;
		}
	}
	
	public boolean hasRoom() {
		return hasRoom;
	}
	
	public boolean isEmpty() {
		return modelCount == 0;
	}
	
	public boolean hasTextureRoom() {
		return this.textures.size() < 8;
	}
	
	public boolean hasTexture(Texture tex) {
		return this.textures.contains(tex);
	}

	public boolean containsModel(Model m){
		if (modelLookup.containsKey(m.getUUID())) {
			return true;
		}
		return false;
	}
}
