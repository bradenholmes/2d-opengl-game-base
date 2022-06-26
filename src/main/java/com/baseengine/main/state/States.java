package main.java.com.baseengine.main.state;

import main.java.com.baseengine.main.graphics.renderer.RendererState;
import main.java.com.baseengine.main.graphics.renderer.RenderStateGameState;

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
