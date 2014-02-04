package com.project.main;

import com.android.touch.DoubleTouchGestureListener;
import com.android.touch.DragGestureDetector;
import com.android.touch.ScaleGestureListener;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public abstract class OpenGLSurfaceView extends GLSurfaceView
{
	protected static final int NUM_HANDLES = 10;
	
	private Context mContext;
	private OpenGLRenderer renderer;
	private TTouchEstado estado;
	
	 // Detectores de Gestos
    private ScaleGestureDetector scaleDectector;
    private GestureDetector doubleTouchDetector;
    private DragGestureDetector dragDetector;
    
    public OpenGLSurfaceView(Context context, AttributeSet attrs, TTouchEstado estado)
    {
    	 super(context, attrs);
         
         // Multitouch
         this.estado = estado;
         this.mContext = context;
         
         // Crear Contexto OpenGL ES 1.0
         setEGLContextClientVersion(1);
    }
    
    public void setRenderer(OpenGLRenderer renderer)
    {		
    	this.renderer = renderer;
    	
    	super.setRenderer(renderer);
    	super.setRenderMode(RENDERMODE_WHEN_DIRTY);
    	
    	this.scaleDectector = new ScaleGestureDetector(mContext, new ScaleGestureListener(renderer));
        this.doubleTouchDetector = new GestureDetector(mContext, new DoubleTouchGestureListener(renderer));
        this.dragDetector = new DragGestureDetector(renderer);
    }
    
    public void setEstado(TTouchEstado estado)
    {
    	this.estado = estado;
    }
    
    public boolean onTouch(View v, MotionEvent event)
    {
    	switch(estado)
    	{
    		case SimpleTouch:
    			return onSingleTouch(v, event);
    		case Detectors:
    			return onDetectorsTouch(v, event);
    		case MultiTouch:
    			return onMultiTouch(v, event);
    	}
    	
    	return false;
    }
    
    private boolean onSingleTouch(View v, MotionEvent event)
	{		
		if(event != null)
		{
			int action = event.getActionMasked();
			
			float x = event.getX();
			float y = event.getY();
			
			float width = getWidth();
			float height = getHeight();
			
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					renderer.onTouchDown(x, y, width, height, 0);
				break;
				case MotionEvent.ACTION_MOVE:
					renderer.onTouchMove(x, y, width, height, 0);	
				break;
				case MotionEvent.ACTION_UP:
					renderer.onTouchUp(x, y, width, height, 0);
				break;
				default:
					return false;
			}
			
			requestRender();	
		}
		
		return true;
	}
    
    private boolean onDetectorsTouch(View v, MotionEvent event)
    {
    	if(event != null)
    	{
    		float x = event.getX();
			float y = event.getY();
			
			float width = getWidth();
			float height = getHeight();
			
			if(event.getPointerCount() == 1)
			{
				dragDetector.onTouchEvent(event, x, y, width, height);
				doubleTouchDetector.onTouchEvent(event);
			}
			else
			{
				scaleDectector.onTouchEvent(event);
			}
			
			requestRender();
    	}
    	
    	return true;
    }
    
	private boolean onMultiTouch(View v, MotionEvent event)
	{
		if(event != null)
		{
			int pointCount = event.getPointerCount();
			int action = event.getActionMasked();
			
			float width = getWidth();
			float height = getHeight();
			
			if(pointCount > NUM_HANDLES) pointCount = NUM_HANDLES;
			
			for(int i = 0; i < pointCount; i++)
			{
				float x = event.getX(i);
				float y = event.getY(i);
				
				int id = event.getPointerId(i);
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						renderer.onTouchDown(x, y, width, height, id);
					break;
					case MotionEvent.ACTION_MOVE:
						renderer.onTouchMove(x, y, width, height, id);	
					break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						renderer.onTouchUp(x, y, width, height, id);
					break;
					default:
						return false;
				}
			}
			
			renderer.onMultiTouchEvent();
			requestRender();
		}
		
		return true;
	}
}
