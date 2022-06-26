package com.autopoker.main.graphics.renderer;

import java.util.ArrayList;
import java.util.List;

import com.autopoker.main.graphics.renderer.model.Model;
import com.autopoker.main.graphics.renderer.renderable.ModelRenderBatch;
import com.autopoker.main.primitive.Texture;
import com.autopoker.main.state.States;
import com.autopoker.main.utility.DebugStatistics;




public abstract class RendererState
{
	protected States stateIndex;
	protected List<ModelRenderBatch> batches = new ArrayList<>();
	
	public abstract void init();
	protected abstract void loadState();
	protected abstract void unloadState();
	
	protected States getStateIndex() {
		return stateIndex;
	}
	
	public void render() {
		if (!batches.isEmpty()){
			batches.get(0).preRender();
			
			for (ModelRenderBatch r : batches) {
				r.render();
			}
			
			batches.get(0).postRender();
		}
	}
	
	public void addModel(Model model) {
		boolean added = false;
		for (ModelRenderBatch r : batches) {
			if (r.hasRoom()) {
				Texture tex = model.getTexture();
				if (tex == null || (r.hasTexture(tex) || r.hasTextureRoom())) {
					if (r.addModel(model)) {
						added = true;
						break;
					}
				}
			}
		}
		
		//If there's no room in any, create a new batch
		if (!added) {
			ModelRenderBatch ren = new ModelRenderBatch();
			ren.start();
			ren.addModel(model);
			batches.add(ren);
			DebugStatistics.countModelBatch();
		}
	}
	
	public void removeModel(Model model) {
		
		for (ModelRenderBatch r : batches) {
			if (r.removeModel(model)) {
				if (r.isEmpty()) {
					batches.remove(r);
					DebugStatistics.removeModelBatch();
				}
				return;
			}
		}
	}
}
