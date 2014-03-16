package com.doepiccoding.arcadecontrol.joystick;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.doepiccoding.arcadecontrol.R;
import com.doepiccoding.arcadecontrol.util.CalculationsUtil;
import com.doepiccoding.arcadecontrol.util.IControlAsset.Orientation;
import com.doepiccoding.arcadecontrol.util.OrientationUtil;

public class ControlStick extends FrameLayout{

	private final int RADIUS;
	private int stickCenterX;
	private int stickCenterY;
	private ImageView stick;
	private float radians;
	private IStickerMoved listener;
	private Orientation lastOrientation = Orientation.IDLE;
	private FrameLayout.LayoutParams params;
	
	public enum ControlStickState{
		GREEN,
		RED
	};
	
	public ControlStick(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//Start the listener for motion events related to this widget...
		setOnTouchListener(stickMotionListener);
		Resources res = context.getResources();
		//Get the dimens for this device before creating the view...
		int width = (int) res.getDimension(R.dimen.joystick_middle_width);
		int height = (int) res.getDimension(R.dimen.joystick_middle_width);
		RADIUS = (int) res.getDimension(R.dimen.joystick_radius);
		params = new FrameLayout.LayoutParams(width, height, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if(stick == null){
			stickCenterX = (right - left)/2;
			stickCenterY = (bottom - top)/2;
			//Get a reference of the stick image view...
			//stick = ImageView.class.cast(findViewById(R.id.stickImage));
		}
		
	}

	private OnTouchListener stickMotionListener = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					radians = (float)getAngleRadians(event.getX(), event.getY());
					float distX = (float)Math.cos(radians) * RADIUS;
					float distY = (float)Math.sin(radians) * RADIUS;
					params.leftMargin = (int)distX;
					params.topMargin = (int)distY;
					stick.setLayoutParams(params);
					if(listener != null){
						Orientation currentOrientation = OrientationUtil.getOrientation(CalculationsUtil.getCurrentAngleDegrees(radians));
						if(lastOrientation != currentOrientation){
							lastOrientation = currentOrientation;
							listener.onOrientationChanged(currentOrientation);
						}
					}
				break;
				case MotionEvent.ACTION_UP:
					radians = 0;
					params.leftMargin = 0;
					params.topMargin = 0;
					stick.setLayoutParams(params);
					if(listener != null){
						lastOrientation = Orientation.IDLE;
						listener.onOrientationChanged(lastOrientation);						
					}
				break;
			}
			return false;
		}
	};
	
	
	
	public void setOnMovedListener(IStickerMoved listener){
		this.listener = listener;
	}
	
	/**
	 * Return the angle of the direction set by the sticker
	 * @param stickX
	 * @param stickY
	 */
	private float getAngleRadians(float stickX, float stickY){
		float distX = stickX - stickCenterX;
		float distY = stickY - stickCenterY;
		float radian = (float) Math.atan2(distY, distX);
		return (float)radian;
	}
	
	/**
	 * Return the angle of the direction set by the sticker
	 */
	public int getCurrentAngleDegrees(){
		
		return CalculationsUtil.getCurrentAngleDegrees(radians);
	}
	
	/**
	 * Interface used to be notified about changes on tget lucky daft punkhe sticker controller
	 */
	public interface IStickerMoved{
		public void onOrientationChanged(Orientation orientation);
	}
}
