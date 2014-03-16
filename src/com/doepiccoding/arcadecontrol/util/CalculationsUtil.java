package com.doepiccoding.arcadecontrol.util;

import android.util.FloatMath;

public class CalculationsUtil {

	/**
	 * Method that calculates distance from two points
	 * @param posX
	 * @param posY
	 * @param posX2
	 * @param posY2
	 */
	public static float getDistanceFromTwoPoints(int posX, int posY, int posX2, int posY2){
		
		//Get the distance...
		int dX = posX - posX2;
		int dY = posY - posY2;
		return FloatMath.sqrt(dX * dX + dY * dY);
	}
	
	/**
	 * Proper angle in degrees from 0 to 360.
	 * <br/><b>Note: Use this method only for angles represented from -180 to 180</b>
	 * @param radians
	 */
	public static int getCurrentAngleDegrees(float radians){
		
		int degree = (int) (radians * 180 / Math.PI);
		if(degree <= 0){
			return degree * -1;
		}else{
			return 360 - degree;
		}
	}
	
	/**
	 * Proper angle in degrees from 0 to 360...
	 * @param degrees
	 */
	public static int getCurrentAngleDegrees(int degrees){
		
		if(degrees < 0){
			return degrees * -1;
		}else{
			return Math.abs(360 - degrees);
		}
	}
	
	/**
	 * Method to calculate the angle in "DEGREES" from 0 to 360 between two points
	 * @param posX
	 * @param posY
	 * @param posX2
	 * @param posY2
	 */
	public static int getAngleBetweenTwoPoints(int posX, int posY, int posX2, int posY2){
		int dX = posX - posX2;
		int dY = posY - posY2;
		float radiansAngle = (float)Math.atan2(dY, dX);
		
		int angleDegrees = (int)(radiansAngle * 180 / Math.PI);
		angleDegrees = angleDegrees < 0 ? angleDegrees * -1 :  360 - angleDegrees;
		return angleDegrees;
	}
	
	/**
	 * Based on two angles decide whether if the main character is in sight of this enemy or not...
	 * @param angleToMainCharacter
	 * @param enemyAngle
	 * @param sightAngle
	 */
	public static boolean isMainInSight(int angleToMainCharacter, int enemyAngle,int sightAngle){
		boolean biggerThan360 = angleToMainCharacter + sightAngle/2 > 360;
		boolean lessThan0 = angleToMainCharacter - sightAngle/2 < 0;
		if(biggerThan360 || lessThan0){
			if(biggerThan360){
				int offset = (angleToMainCharacter + sightAngle/2) - 360;
				return enemyAngle > angleToMainCharacter - sightAngle/2 || enemyAngle < offset;
			}else{
				int offset = Math.abs((angleToMainCharacter - sightAngle/2));
				return enemyAngle < angleToMainCharacter + sightAngle/2 || enemyAngle > 360 - offset;
			}
			
		}else{
			int difference = Math.abs(enemyAngle - angleToMainCharacter);
			//Take decision based on different...
			return difference <= sightAngle;
		}
	}
	
	/**
	 * Calculate experience based on minimum experience provided by enemy...
	 * @param baseExperience
	 * 
	 */
	public static int getExperience(int baseExperience){
		float random = (float)Math.random();
		return (int)(baseExperience + ((baseExperience / 2) * random));
	}
	
	/**
	 * Calculate experience based on minimum experience provided by enemy...
	 * @param baseExperience
	 * 
	 */
	public static int getGold(int baseGold){
		float random = (float)Math.random();
		return (int)(baseGold + (baseGold * random));
	}
	
}
