package com.autopoker.main.input;

import static org.lwjgl.glfw.GLFW.*;

import com.autopoker.main.graphics.Window;

public class MouseListener
{
	private static MouseListener instance;
	
	public static final int LEFT = 0;
	public static final int MIDDLE = 2;
	public static final int RIGHT = 1;
	
	private double scrollX, scrollY;
	private double xPos, yPos, lastX, lastY;
	private boolean mouseButtonDown[] = new boolean[3];
	private boolean mouseButtonRising[] = new boolean[3];
	private boolean isDragging;
	
	
	private static boolean isCursorEnabled = true;
	
	private MouseListener() {
		this.scrollX = 0.0f;
		this.scrollY = 0.0f;
		this.xPos = 0.0f;
		this.yPos = 0.0f;
		this.lastX = 0.0f;
		this.lastY = 0.0f;

	}
	
	public static MouseListener get() {
		if (instance == null) {
			instance = new MouseListener();
		}
		return instance;
	}
	
	public static void mousePosCallback(long window, double xpos, double ypos) {
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		get().xPos = xpos;
		get().yPos = ypos;
		
		get().isDragging = get().mouseButtonDown[LEFT] || get().mouseButtonDown[MIDDLE] || get().mouseButtonDown[RIGHT];
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			if (button < get().mouseButtonDown.length) {
				get().mouseButtonDown[button] = true;
				get().mouseButtonRising[button] = true;
			}
		} else if (action == GLFW_RELEASE) {
			if (button < get().mouseButtonDown.length) {
				get().mouseButtonDown[button] = false;
				get().mouseButtonRising[button] = false;
				get().isDragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}
	
	public static void endFrame() {
		get().scrollX = 0;
		get().scrollY = 0;
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		
		get().mouseButtonRising[LEFT] = false;
		get().mouseButtonRising[MIDDLE] = false;
		get().mouseButtonRising[RIGHT] = false;
	}
	
	public static float getX() {
		return (float)get().xPos;
	}
	
	public static float getY() {
		return (float)get().yPos;
	}
	
	public static void setCursorDisabled() {
		if (!isCursorEnabled) {
			return;
		}
		isCursorEnabled = false;
		Window.get().setCursorDisabled();
	}
	
	public static void setCursorEnabled() {
		if (isCursorEnabled) {
			return;
		}
		isCursorEnabled = true;
		Window.get().setCursorEnabled();
	}
	
	public static boolean isCursorEnabled() {
		return isCursorEnabled;
	}
	
	public static float getDX() {
		return (float)(get().xPos - get().lastX);
	}
	
	public static float getDY() {
		return (float)(get().yPos - get().lastY);
	}
	
	public static float getScrollX() {
		return (float)get().scrollX;
	}
	
	public static float getScrollY() {
		return (float)get().scrollY;
	}
	
	public static boolean isDragging() {
		return get().isDragging;
	}
	
	//true for as long as the button is held
	public static boolean mouseButtonDown(int mouseButton) {
		return get().mouseButtonDown[mouseButton];
	}
	
	//only true for the one frame the mouse is clicked on
	public static boolean mouseButtonRising(int mouseButton) {
		return get().mouseButtonRising[mouseButton];
	}
	
}
