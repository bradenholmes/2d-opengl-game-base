package com.autopoker.main.state;

import com.autopoker.main.graphics.renderer.RendererState;
import com.autopoker.main.graphics.renderer.RenderStateGameState;

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
