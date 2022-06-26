package com.baseengine.main.graphics.renderer;

import com.baseengine.main.graphics.renderer.renderable.ModelRenderBatch;
import com.baseengine.main.state.States;

public class RenderStateGameState extends RendererState
{
	
	
	public RenderStateGameState() {
		stateIndex = States.GAME_STATE;
	}
	
	public void init() {
		
	}
	
	protected void loadState() {
		
	}
	
	protected void unloadState() {
		for (ModelRenderBatch r : batches) {
			r.delete();
		}
	}

}
