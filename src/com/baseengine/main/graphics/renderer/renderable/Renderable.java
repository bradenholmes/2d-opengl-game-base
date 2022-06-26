package com.baseengine.main.graphics.renderer.renderable;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import com.baseengine.main.graphics.renderer.model.Model;
import com.baseengine.main.primitive.Texture;





public abstract class Renderable
{
	
	protected int vaoId, vboId, eboId;
	
	public abstract void start();
	public abstract void render();
	public abstract void preRender();
	public abstract void postRender();
	public abstract boolean addModel(Model m);
	public abstract boolean removeModel(Model m);
	public abstract boolean containsModel(Model m);
	public abstract boolean hasRoom();
	public abstract boolean hasTexture(Texture t);
	public abstract boolean hasTextureRoom();
	public abstract boolean isEmpty();
	
	public void delete() {
		glDeleteVertexArrays(vaoId);
		glDeleteBuffers(vboId);
		glDeleteBuffers(eboId);
	}
}
