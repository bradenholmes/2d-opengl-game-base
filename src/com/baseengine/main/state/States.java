package com.baseengine.main.state;

import com.baseengine.main.graphics.renderer.RenderStateGameState;
import com.baseengine.main.graphics.renderer.RendererState;

public enum States
{
	GAME_STATE(new GameState(), new RenderStateGameState());
	
	private final State stateObject;
	private final RendererState rendererState;
	
	private States(State stateObject, RendererState rendererState) {
		this.stateObject = stateObject;
		this.rendererState = rendererState;
	}
	
	public State getStateObject() {
		return stateObject;
	}
	
	public RendererState getRendererState() {
		return rendererState;
	}
}
