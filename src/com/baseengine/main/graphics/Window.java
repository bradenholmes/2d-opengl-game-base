package com.baseengine.main.graphics;

import static org.lwjgl.openal.ALC10.*;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.opengl.GL;

import com.baseengine.main.graphics.renderer.Renderer;
import com.baseengine.main.input.KeyListener;
import com.baseengine.main.input.MouseListener;
import com.baseengine.main.state.StateController;
import com.baseengine.main.state.States;
import com.baseengine.main.utility.DebugStatistics;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Window
{
	private int width, height;
	private String title;
	private long glfwWindow;
	
	private static Window window = null;
	
	private boolean hasResized = false;
	
	private long audioContext;
	private long audioDevice;
	
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
	private String glslVersion = null;
	
	private float timeOpen = 0;
	
	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "WINDOW NAME";
	}
	
	public static Window get() {
		if (Window.window == null) {
			Window.window = new Window();
		}
		
		return Window.window;
	}
	
	public void run() {
		init();
		
		loop();
		
		//Free audio device
		alcDestroyContext(audioContext);
		alcCloseDevice(audioDevice);
		
		//Free ImGui
		ImGui.destroyContext();
		
		//Free memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	

	
	public void init() {
		//Error callback
		GLFWErrorCallback.createPrint(System.err).set();
		
		//Initialize GLFW
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW.");
		}
		
		//Configure
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		
		//Create Window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create window.");
		}
		
		//Window resizing
		GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height){
				 setWidthHeight(width, height);
			}
		};
		glfwSetWindowSizeCallback(glfwWindow, windowSizeCallback);
		
		//Input callbacks
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		
		//setCursorDisabled();
		
		
		glfwMakeContextCurrent(glfwWindow);
		glfwSwapInterval(1);
		
		glfwShowWindow(glfwWindow);
		
		//Audio Stuff
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		audioDevice = alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		audioContext = alcCreateContext(audioDevice, attributes);
		
		alcMakeContextCurrent(audioContext);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		if (!alCapabilities.OpenAL10) {
			System.out.println("ERROR: Audio Library not supported!");
		}
		
		//Start OpenGL
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glFrontFace(GL_CCW); 
		
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		ImGui.createContext();
		imGuiGlfw.init(glfwWindow, true);
		glslVersion = "#version 130";
		
		imGuiGl3.init(glslVersion);
		
		
		
		StateController.setState(States.GAME_STATE);
		
		Renderer.initialize();
	}
	
	public void loop() {
		float beginTime = (float)glfwGetTime();
		float endTime;
		float deltaTime = -1.0f;
		
		//GAME LOOP
		while(!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();
			
			imGuiGlfw.newFrame();
			ImGui.newFrame();
			
			//Handles window resizing
			if (hasResized) {
				glViewport(0, 0, width, height);
				Camera.get().updateWindowSize();
				Renderer.initialize();
			}
			
			
			
			if (deltaTime >= 0)
				StateController.update(deltaTime);
			
			hasResized = false;
			
			Renderer.render();
			
			
			ImGui.render();
			imGuiGl3.renderDrawData(ImGui.getDrawData());
			
			if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
				final long backupWindowPtr = glfwGetCurrentContext();
				ImGui.updatePlatformWindows();
				ImGui.renderPlatformWindowsDefault();
				glfwMakeContextCurrent(backupWindowPtr);
			}
				
			glfwSwapBuffers(glfwWindow);
			
			//System.out.println(DebugStatistics.getFramesPerSecond());
			DebugStatistics.endFrame(deltaTime);
			timeOpen += deltaTime;

			
			//Update time
			endTime = (float)glfwGetTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;
		}
	}
	
	public void close() {
		glfwSetWindowShouldClose(glfwWindow, true);
	}
	
	public void setCursorEnabled() {
		glfwSetInputMode(glfwWindow , GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public void setCursorDisabled() {
		glfwSetInputMode(glfwWindow , GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	public void setWidthHeight(int width, int height) {
		this.width = width;
		this.height = height;
		this.hasResized = true;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean hasResized() {
		return hasResized;
	}
	
	public float getResolution() {
		return (float) width / (float) height;
	}
	
	public float getInverseResolution() {
		return (float) height / (float) width;
	}
	
	public float getTimeOpen() {
		return timeOpen;
	}
}
