package com.example.m04surfaceviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawTest extends SurfaceView implements SurfaceHolder.Callback {

	//呼叫getHolder()方法來取得 SurfaceHolder
	SurfaceHolder holder =getHolder();
	Bitmap bp;
    Canvas canvas;
    int x=100,y=250;  //貼圖在螢幕上的 x,y 座標
	
	public DrawTest(Context context) 
	{
		super(context);

		//把這個 class 本身(extends SurfaceView)
        //透過 holder 的 Callback()方法連結起來
		holder.addCallback(this);
		
		bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//在 canvas 畫布上貼圖的三個步驟
		
		 //1. 鎖住畫布
		canvas = holder.lockCanvas();
		//2. 在畫布上貼圖
		canvas.drawBitmap(bp, x, y, null);
		//3. 解鎖並po出畫布
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
