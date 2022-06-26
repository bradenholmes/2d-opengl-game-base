package com.baseengine.main.graphics.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.joml.Vector3f;

import com.baseengine.main.graphics.Camera;
import com.baseengine.main.graphics.Window;
import com.baseengine.main.graphics.renderer.model.Model;
import com.baseengine.main.graphics.renderer.renderable.DebugRenderer;
import com.baseengine.main.primitive.Assets;
import com.baseengine.main.primitive.Shader;
import com.baseengine.main.state.States;



public class Renderer
{
	public static final int MAX_BATCH_SIZE = 1000;
	public static final String MODEL_SHADER_PATH = "res/shaders/model.glsl";
	public static final String FB_SHADER_PATH = "res/shaders/framebuffer.glsl";
	public static final String DEBUG_SHADER_PATH = "res/shaders/debug.glsl";
	
	public static final int FB_TEXTURE_OFFSET = 1;
	
	public static final Vector3f RED = new Vector3f(1, 0, 0);
	public static final Vector3f GREEN = new Vector3f(0, 1, 0);
	public static final Vector3f BLUE = new Vector3f(0, 0, 1);
	public static final Vector3f PINK = new Vector3f(1, 0, 1);
	public static final Vector3f YELLOW = new Vector3f(1, 1, 0);
	public static final Vector3f TEAL = new Vector3f(0, 1, 1);
	
	private static boolean initialized = false;
	private static RendererState currentRendererState = null;
	
	private static DebugRenderer debugRenderer = new DebugRenderer();;
	
	//Frame buffer settings
	private static int fbo;
	private static int fbMainTexture;
	private static int rbo;
	private static int rectVao, rectVbo;
	
	static Shader fbShader = Assets.getShader(FB_SHADER_PATH);
	
	private static float[] rectangleVertices = {
		 1.0f, -1.0f, 1.0f, 0.0f,
		-1.0f, -1.0f, 0.0f, 0.0f,
		-1.0f,  1.0f, 0.0f, 1.0f,
		
		 1.0f,  1.0f, 1.0f, 1.0f,
		 1.0f, -1.0f, 1.0f, 0.0f,
		-1.0f,  1.0f, 0.0f, 1.0f
	};
	
	public static void initialize() {
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		glActiveTexture(GL_TEXTURE0);
		fbMainTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, fbMainTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.get().getWidth(), Window.get().getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		rbo = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Window.get().getWidth(), Window.get().getHeight());
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("FRAMEBUFFER ERROR: " + glCheckFramebufferStatus(GL_FRAMEBUFFER));
		}
		
		rectVao = glGenVertexArrays();
		rectVbo = glGenBuffers();
		glBindVertexArray(rectVao);
		glBindBuffer(GL_ARRAY_BUFFER, rectVbo);
		glBufferData(GL_ARRAY_BUFFER, rectangleVertices, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);
		
		fbShader.use();
		fbShader.uploadTexture("mainTexture", 0);
		
		fbShader.detach();
		
		debugRenderer.start();
		
		initialized = true;
	}
	
	public static void render() {
		if (!initialized) {
			System.out.println("ERROR: Tried to render, but Renderer has not been initialized!");
			return;
		}
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		
		//Main Batch Rendering
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, fbMainTexture);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fbMainTexture, 0);
		glAlphaFunc(GL_GREATER, 0f);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		debugRenderer.render();
		
		if (currentRendererState != null) {
			currentRendererState.render();
		}
		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_ALPHA_TEST);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		//Combine all and render to screen
		fbShader.use();
		
		glBindVertexArray(rectVao);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindVertexArray(0);
		fbShader.detach();
		

		
		Camera.get().endFrame();
	}
	
	public static void setRendererState(States state) {
		currentRendererState = state.getRendererState();
		currentRendererState.loadState();
	}
	
	// -----------------------------------------------------------------------------
	// Add and remove models
	// -----------------------------------------------------------------------------
	public static void addModel(Model model) {
		if (currentRendererState == null) {
			System.err.println("ERROR: Cannot add model to null renderer state");
			return;
		}
		
		currentRendererState.addModel(model);
	}
	
	public static void removeModel(Model model) {
		if (currentRendererState == null) {
			System.err.println("ERROR: Cannot remove model from null renderer state");
			return;
		}
		
		currentRendererState.removeModel(model);
	}
	
	// -----------------------------------------------------------------------------
	// Get Debug Renderer
	// -----------------------------------------------------------------------------
	public static DebugRenderer debug() {
		return debugRenderer;
	}
}
