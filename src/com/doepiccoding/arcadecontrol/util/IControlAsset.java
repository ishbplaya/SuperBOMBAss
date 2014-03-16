package com.doepiccoding.arcadecontrol.util;

public interface IControlAsset {

	public void initAsset();
	public void release();
	public void switchAsset();
	
	public enum State{
		RUN,
		CAST,
		ATTACK,
		RELEASED;
		
		public String getState(){
			return name().toLowerCase();
		}
	}
	
	public enum Orientation{
		IDLE,
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST;
		public String getOrientation(){
			return name().toLowerCase();
		}
	}
	
}
