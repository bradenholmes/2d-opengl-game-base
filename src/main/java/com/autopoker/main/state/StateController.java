package com.autopoker.main.state;

import com.autopoker.main.graphics.renderer.Renderer;

public class StateController
{
	private static States state = null;
	
	static {
		for (States s : States.values()) {
			s.getStateObject().init();
			s.getRendererState().init();
		}
	}
	
	public static void update(float deltaTime) {
		if (state != null) {
			state.getStateObject().update(deltaTime);
		}
	}
	
	public static void setState(States newState) {
		System.out.println("SET STATE TO: " + newState);
		if (state != null) {
			state.getStateObject().unloadState();
		}
		
		Renderer.setRendererState(newState);
		
		state = newState;
		state.getStateObject().loadState();
		
	}
	
	public static States getCurrentState() {
		return state;
	}
}
