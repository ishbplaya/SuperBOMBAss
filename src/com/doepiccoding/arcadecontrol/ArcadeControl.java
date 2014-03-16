package com.doepiccoding.arcadecontrol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.camera.simplemjpeg.MjpegActivity;
import com.camera.simplemjpeg.MjpegInputStream;
import com.camera.simplemjpeg.MjpegView;
import com.camera.simplemjpeg.MjpegActivity.DoRead;
import com.doepiccoding.arcadecontrol.joystick.ControlStick;
import com.doepiccoding.arcadecontrol.joystick.ControlStick.IStickerMoved;
import com.doepiccoding.arcadecontrol.util.IControlAsset.Orientation;

public class ArcadeControl extends Activity {

	private ControlStick stick;
	private SparseArray<String> actionsMap = new SparseArray<String>();
	private PrintWriter out;
	private Handler queuer;
	private Socket socket;
	
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";

    private MjpegView mv = null;
    String URL;
    
    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;
    
    private int width = 320;
    private int height = 240;
    
    private boolean suspending = false;
    
	final Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_arcade_control);
		
		int[] ids = {R.id.buttonStop, R.id.ButtonAuto, R.id.buttonA, R.id.buttonB, R.id.buttonC, R.id.buttonD};
		String[] actions = {"STOP","AUTO" ,"Z", "X", "A", "S"};
		
		mv = (MjpegView) findViewById(R.id.mv);
		if(mv != null){
        	mv.setResolution(width, height);
        }
		URL = "http://192.168.0.118:8081/video.mjpeg";
		new DoRead().execute(URL);
		for(int i = 0 ; i < ids.length ; i++){
			int id = ids[i];
			actionsMap.put(id, actions[i]);
			findViewById(id).setOnTouchListener(touchListener);
		}
		
		//Get reference of the stick...
//        stick = (ControlStick)findViewById(R.id.stickControlView);
//        stick.setOnMovedListener(stickerMovedListener);
		
		//Create socket and make this thread the "reader"...
		new Thread(new Runnable() { 
			@Override
			public void run() {
				try{
					InetAddress serverAddr = InetAddress.getByName("192.168.0.118");
					socket = new Socket(serverAddr, 2312);
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					
					//TODO: Keep reading until the app gets destroyed...
					
				}catch(Exception e){e.printStackTrace();}
			}
		}).start();
		
		//Create the Thread that will be handling the pool of messages...
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				queuer = new Handler();
				Looper.loop();
			}
		}).start();
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int action = event.getAction();
			if(action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP)return false;
			
			String buttonState = action == MotionEvent.ACTION_DOWN ? "1" : "0";
			String buttonAction = actionsMap.get(view.getId());
			
			sendMessage(buttonAction + buttonState);
			
			return false;
		}
	};
	
	private SparseArray<String> joyStickActions = new SparseArray<String>();
	{
		joyStickActions.put(1, "UP");
		joyStickActions.put(2, "RIGHT");
		joyStickActions.put(3, "DOWN");
		joyStickActions.put(4, "LEFT");
	}
	private int[] previousIds = new int[2];
	/**
     * Stick Events Listener
     */
    private IStickerMoved stickerMovedListener = new IStickerMoved() {
		@Override
		public void onOrientationChanged(Orientation orientation) {
			
			int id1 = 0;
            int id2 = 0;
           
            //Assign the proper action index for each orientation in joystick...
            if(orientation == Orientation.NORTH){
                id1 = 1;
            }else if(orientation == Orientation.NORTH_EAST){
                id1 = 1;
                id2 = 2;
            }else if(orientation == Orientation.EAST){
                id2 = 2;
            }else if(orientation == Orientation.SOUTH_EAST){
                id1 = 3;
                id2 = 2;
            }else if(orientation == Orientation.SOUTH){
                id1 = 3;
            }else if(orientation == Orientation.SOUTH_WEST){
                id1 = 3;
                id2 = 4;
            }else if(orientation == Orientation.WEST){
                id2 = 4;
            }else if(orientation == Orientation.NORTH_WEST){
                id1 = 1;
                id2 = 4;
            }else {
                id1 = previousIds[0];
                id2 = previousIds[1];
               
                //Release Commands...
                if(id1 > 0){
                    sendMessage(joyStickActions.get(id1) + "0");
                }
                if(id2 > 0){
                    sendMessage(joyStickActions.get(id2) + "0");
                }
                previousIds[0] = 0;
                previousIds[1] = 0;
                return;
            }
           
            //Release Previous commands if necessary...
            if(previousIds[0] > 0 && previousIds[0] != id1){
                sendMessage(joyStickActions.get(previousIds[0]) + "0");
            }
            if(previousIds[1] > 0 && previousIds[1] != id2){
                sendMessage(joyStickActions.get(previousIds[1]) + "0");
            }
           
            //Press Commands
            if(id1 > 0 && previousIds[0] != id1){
                sendMessage(joyStickActions.get(id1) + "1");
            }
            if(id2 > 0 && previousIds[1] != id2){
                sendMessage(joyStickActions.get(id2) + "1");
            }
            
            //Store the previous state...
            previousIds[0] = id1;
            previousIds[1] = id2;
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	if(mv!=null){
    		mv.freeCameraMemory();
    	}
    	
		if(out != null){
			// Send the command...
			out.print("closeAcknowledgeMsg");
			out.flush();
			
			//Give the socket time to read the last command...
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
			out.close();
		}
		if(socket != null && !socket.isClosed()){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Stop the Looper...
		if(queuer != null){
			Looper queueLooper = queuer.getLooper();
			if(queueLooper != null){
				queueLooper.quit();
			}
		}
	};	
	
	public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mv!=null){
        	if(suspending){
        		new DoRead().execute(URL);
        		suspending = false;
        	}
        }

    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mv!=null){
        	if(mv.isStreaming()){
		        mv.stopPlayback();
		        suspending = true;
        	}
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }
	//Send the messages queued, one at the time in order
	private synchronized void sendMessage(final String message) {
		queuer.post(new Runnable() {
			@Override
			public void run() {
				try {
					// Send the command...
					out.print(message);
					out.flush();
					Thread.sleep(50);
				}catch(Exception e){e.printStackTrace();}
			}
		});
	}
	
	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		private static final boolean DEBUG=false;
	    private static final String TAG = "MJPEG";

	    protected MjpegInputStream doInBackground(String... url) {
	        //TODO: if camera has authentication deal with it and don't just not work
	        HttpResponse res = null;         
	        DefaultHttpClient httpclient = new DefaultHttpClient(); 
	        HttpParams httpParams = httpclient.getParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
	        HttpConnectionParams.setSoTimeout(httpParams, 5*1000);
	        if(DEBUG) Log.d(TAG, "1. Sending http request");
	        try {
	            res = httpclient.execute(new HttpGet(URI.create(url[0])));
	            if(DEBUG) Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
	            if(res.getStatusLine().getStatusCode()==401){
	                //You must turn off camera User Access Control before this will work
	                return null;
	            }
	            return new MjpegInputStream(res.getEntity().getContent());  
	        } catch (ClientProtocolException e) {
	        	if(DEBUG){
	                e.printStackTrace();
	                Log.d(TAG, "Request failed-ClientProtocolException", e);
	        	}
	            //Error connecting to camera
	        } catch (IOException e) {
	        	if(DEBUG){
	                e.printStackTrace();
	                Log.d(TAG, "Request failed-IOException", e);
	        	}
	            //Error connecting to camera
	        }
	        return null;
	    }

	    protected void onPostExecute(MjpegInputStream result) {
	        mv.setSource(result);
	        if(result!=null){
	        	result.setSkip(1);
	        }
	        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
	        mv.showFps(false);
	    }
	}

	public class RestartApp extends AsyncTask<Void, Void, Void> {
	    protected Void doInBackground(Void... v) {
	    	ArcadeControl.this.finish();
	        return null;
	    }

	    protected void onPostExecute(Void v) {
	    	startActivity((new Intent(ArcadeControl.this, MjpegActivity.class)));
	    }
	}
	
}
