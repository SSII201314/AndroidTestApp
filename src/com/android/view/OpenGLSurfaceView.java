 package com.android.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.touch.MoveDetector;
import com.android.touch.RotateDetector;
import com.android.touch.ScaleDetector;
import com.android.touch.TTouchEstado;

public abstract class OpenGLSurfaceView extends GLSurfaceView
{
	protected static final int NUM_HANDLES = 10;
	
	// Animaci�n
	protected static final long TIME_INTERVAL = 80;
	protected static final long TIME_DURATION = 24*TIME_INTERVAL;
	
	private Context mContext;
	private TTouchEstado estado;
	
	// Detectores de Gestos
	private ScaleDetector scaleDetector;
	private MoveDetector moveDetector;
	private RotateDetector rotateDetector;
    
	/* SECTION Constructora */
	
    public OpenGLSurfaceView(Context context, AttributeSet attrs, TTouchEstado estado)
    {
    	 super(context, attrs);
         
         // Tipo Multitouch
         this.estado = estado;
         this.mContext = context;
         
         // Activar Formato Texturas transparentes
         setEGLConfigChooser(8, 8, 8, 8, 0, 0); 
         getHolder().setFormat(PixelFormat.RGBA_8888);
         
         // Crear Contexto OpenGL ES 1.0
         setEGLContextClientVersion(1);
    }
    
    /* SECTION M�todos Abstractos */
	
    protected abstract void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract void onMultiTouchEvent();
    
    protected void setRenderer(OpenGLRenderer renderer)
    {	
    	// Renderer
    	super.setRenderer(renderer);
    	super.setRenderMode(RENDERMODE_WHEN_DIRTY);
    	
        // Detectors
    	scaleDetector = new ScaleDetector(mContext, renderer);
        moveDetector = new MoveDetector(renderer);
        rotateDetector = new RotateDetector(renderer);
        
        setEstado(estado);
    }
    
    public void setEstado(TTouchEstado estado)
    {
    	this.estado = estado;
    	
    	scaleDetector.setEstado(estado == TTouchEstado.CamaraDetectors);
    	moveDetector.setEstado(estado == TTouchEstado.CamaraDetectors);
    	rotateDetector.setEstado(estado == TTouchEstado.CamaraDetectors);
    }
    
    /* SECTION M�todos Listener onTouch */
    
    public boolean onTouch(View v, MotionEvent event)
    {
    	switch(estado)
    	{
    		case SimpleTouch:
    			return onSingleTouch(v, event);
    		case MultiTouch:
    			return onMultiTouch(v, event);
    		case CamaraDetectors:
    			return onDetectorsTouch(v, event);
    		case CoordDetectors:
    			return onDetectorsTouch(v, event);
    	}
    	
    	return false;
    }
    
    private boolean onSingleTouch(View v, MotionEvent event)
	{		
		if(event != null)
		{
			int action = event.getActionMasked();
			
			float pixelX = event.getX();
			float pixelY = event.getY();
			
			float screenWidth = getWidth();
			float screenHeight = getHeight();
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					onTouchDown(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_MOVE:
					onTouchMove(pixelX, pixelY, screenWidth, screenHeight, 0);	
				break;
				case MotionEvent.ACTION_UP:
					onTouchUp(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
			}
			
			requestRender();	
			return true;
		}
		
		return false;
	}
    
    private boolean onDetectorsTouch(View v, MotionEvent event)
    {
    	if(event != null)
    	{			
			float screenWidth = getWidth();
			float screenHeight = getHeight();
			
			if(event.getPointerCount() == 1)
			{				
				moveDetector.onTouchEvent(event, screenWidth, screenHeight);
			}
			else if(event.getPointerCount() == 2)
			{
				if(rotateDetector.onTouchEvent(event, screenWidth, screenHeight))
				{
					moveDetector.onStopEvent(event);
				}
				else
				{
					scaleDetector.onTouchEvent(event, screenWidth, screenHeight);
					moveDetector.onStopEvent(event);
				}
			}
			
			requestRender();
			return true;
    	}
    	
    	return false;
    }
    
	private boolean onMultiTouch(View v, MotionEvent event)
	{
		if(event != null)
		{
			int pointCount = event.getPointerCount();
			int action = event.getActionMasked();
			
			float screenWidth = getWidth();
			float screenHeight = getHeight();
			
			if(pointCount > NUM_HANDLES) pointCount = NUM_HANDLES;
			
			for(int i = 0; i < pointCount; i++)
			{
				float pixelX = event.getX(i);
				float pixelY = event.getY(i);
				
				int pointer = event.getPointerId(i);
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_MOVE:
						onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);	
					break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						onTouchUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
				}
			}
			
			onMultiTouchEvent();
			requestRender();
			return true;
		}
		
		return false;
	}
}