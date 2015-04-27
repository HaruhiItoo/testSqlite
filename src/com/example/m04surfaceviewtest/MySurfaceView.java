package com.example.m04surfaceviewtest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{


	private RcActivity mActivity;
	private Paint paint;
	private int currentAlpha = 0;
	private int screenWidth;
	private int screenHeight;
	private Bitmap currentLogo;
	private int currentX;
	private int currentY;
	private List<Bitmap> logos;
	private int sleepSpan;


	public MySurfaceView(RcActivity activity){
		super(activity);	
	
		mActivity = activity;	
		getHolder().addCallback(this);
		
		logos = new ArrayList<Bitmap>();
		logos.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo1));
		logos.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo2));
		
		// Get display size.
		//WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		
		paint = new Paint();
	}
	
	public void DrawCanvas(Canvas canvas)
	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(255);
		// Draw black rectangle.
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		
		// Draw bitmap. 
		if (currentLogo == null) 
			return;
		paint.setAlpha(currentAlpha);
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Create a thread 
		// to change the alpha value to make animation effect.
		new Thread(){
			// Override method "run()"
			public void run()
			{
				for(Bitmap bm:logos)
				{
					currentLogo = bm;
					currentX = screenWidth/2 - bm.getWidth()/2;
					currentY = screenHeight/2 - bm.getHeight()/2;
					// Change alpha.
					//for(int i=255; i>-10; i-=10)
					for(int i=255; i>-20; i-=20)
					{
						currentAlpha = i;
						if(currentAlpha < 0)
							currentAlpha=0;
						
						// To draw on canvas.
						// Get surface holder.
						SurfaceHolder holder = MySurfaceView.this.getHolder();
						// 1. Lock canvas.
						Canvas canvas = holder.lockCanvas();
						try
						{
							synchronized(holder)
							{
								// 2. Draw
								DrawCanvas(canvas);
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}finally
						{
							// 3. Unlock canvas.
							if(canvas != null)
								holder.unlockCanvasAndPost(canvas);
						}
						
						try
						{
							if(i==255)
							{
								// At beginning drawing picture, wait 1s.  
								//Thread.sleep(1000);
								Thread.sleep(700);
							}
							Thread.sleep(sleepSpan);
						}catch(Exception e)
						{ e.printStackTrace(); }
					} // End: for-loop
				}// End: for bitmaps
				
				// Send message to RcActivity. It will go to Main UI.
				mActivity.hd.sendEmptyMessage(0);
				
			}// End: method "run".
		}.start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
