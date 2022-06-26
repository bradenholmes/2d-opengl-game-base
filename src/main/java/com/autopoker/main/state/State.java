package com.autopoker.main.state;

public abstract class State
{
	
	public abstract void init();
	public abstract void loadState();
	public abstract void unloadState();
	
	public abstract void update(float deltaTime);
	
}
