package main.java.com.baseengine.main.input;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener
{
	private static KeyListener instance;
	
	private int arrayLength = 350;
	private boolean keyPressed[] = new boolean[arrayLength];
	private int keyRising = -1;
	private int nextKeyRising = -1;
	
	private KeyListener() {
		
	}
	
	public static KeyListener get() {
		if (instance == null) {
			instance = new KeyListener();
		}
		return instance;
	}
	
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS) {
			get().keyPressed[key] = true;
			get().nextKeyRising = key;
		} else if (action == GLFW_RELEASE) {
			get().keyPressed[key] = false;
		}
	}
	
	public static void endFrame() {
		get().keyRising = -1;
		if (get().nextKeyRising != -1) {
			get().keyRising = get().nextKeyRising;
			get().nextKeyRising = -1;
		}
	}
	
	public static boolean isKeyPressed(int i) {
		return get().keyPressed[i];
	}
	
	public static boolean isKeyRising(int i) {
		return get().keyRising == i;
	}
	
	public static int getKeyRising() {
		return get().keyRising;
	}
}
