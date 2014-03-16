package com.doepiccoding.arcadecontrol.util;

import com.doepiccoding.arcadecontrol.util.IControlAsset.Orientation;

public class OrientationUtil {

	/**
	 * Get the orientation value for a set of degrees, notice that values must be between 0 - 360 degrees...
	 * @param degrees
	 */
	public static Orientation getOrientation(int degrees){
		
		//Make sure that numbers bigger than 360, math the pattern of 0 - 360
		if(degrees > 360){
			degrees = degrees - 360;
		}
		
		if((degrees <= 22.5 && degrees >= 0) || degrees >= 337){
			return Orientation.EAST;
		}else
		if(degrees > 22.5 && degrees <= 67.5){
			return Orientation.NORTH_EAST;
		}else
		if(degrees > 67.5 && degrees <= 112.5){
			return Orientation.NORTH;
		}else
		if(degrees > 112.5 && degrees <= 157.5){
			return Orientation.NORTH_WEST;
		}else
		if(degrees > 157.5 && degrees <= 202.5){
			return Orientation.WEST;
		}else
		if(degrees > 202.5 && degrees <= 247.5){
			return Orientation.SOUTH_WEST;
		}else
		if(degrees > 247.5 && degrees <= 292.5){
			return Orientation.SOUTH;
		}else
			return Orientation.SOUTH_EAST;
	}
	
	/**
	 * Get the degrees according to the orientation received.
	 * Notice that this values are positive clockwise, and negative not clock wise
	 * @param orientation
	 */
	public static int getDegreesFromOrientation(Orientation orientation){
		switch(orientation){
			case NORTH:
				return 90;
			case NORTH_EAST:
				return 45;
			case EAST:
				return 0;
			case SOUTH_EAST:
				return 315;
			case SOUTH:
				return 270;
			case SOUTH_WEST:
				return 255;
			case WEST:
				return 180;
			case NORTH_WEST:
				return 135;
		}
		return 0;
	}
	
	/**
	 * Get radiants according to the orientation received.
	 * This method should always be used to calculate X-Y position
	 * Notice that this values are positive clockwise, and negative not clock wise
	 * @param orientation
	 */
	public static float getRadiansFromOrientation(Orientation orientation){
		switch(orientation){
			case NORTH:
				return -(float)Math.PI / 2;
			case NORTH_EAST:
				return -(float)Math.PI / 4;
			case EAST:
				return 0;
			case SOUTH_EAST:
				return (float)Math.PI / 4;
			case SOUTH:
				return (float)Math.PI / 2;
			case SOUTH_WEST:
				return (float)(3 * Math.PI / 4);
			case WEST:
				return (float)Math.PI;
			case NORTH_WEST:
				return -(float)(3 * Math.PI / 4);
		}
		return 0;
	}
	
}
