package com.android.touch;

import android.view.MotionEvent;

import com.android.view.OpenGLRenderer;

public class MoveDetector
{
	private OpenGLRenderer renderer;

	private boolean modoCamara;
	private boolean bloqueado;

	private float lastPixelX, lastPixelY;

	/* Constructora */

	public MoveDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.bloqueado = false;
		this.modoCamara = true;
	}

	/* Métodos de Modificación de Estado */

	public void setEstado(boolean camara)
	{
		this.modoCamara = camara;
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event, float screenWidth, float screenHeight)
	{
		int action = event.getActionMasked();
		
		if (!bloqueado)
		{
			if(modoCamara)
			{
				return onTouchEventCamara(event, screenWidth, screenHeight);
			}
			else
			{
				return onTouchEventCoords(event, screenWidth, screenHeight);
			}
		}
		else
		{
			if (action == MotionEvent.ACTION_UP)
			{
				bloqueado = false;
			}
		}

		return true;
	}
	
	private boolean onTouchEventCamara(MotionEvent event, float screenWidth, float screenHeight)
	{
		int action = event.getActionMasked();

		float pixelX = event.getX();
		float pixelY = event.getY();

		switch (action) 
		{
			case MotionEvent.ACTION_DOWN:
				dragOnCamaraDown(pixelX, pixelY, screenWidth, screenHeight);
			break;
			case MotionEvent.ACTION_MOVE:
				dragOnCamaraMove(pixelX, pixelY, screenWidth, screenHeight);
			break;
		}

		return true;
	}
	
	private boolean onTouchEventCoords(MotionEvent event, float screenWidth, float screenHeight)
	{
		int action = event.getActionMasked();

		float pixelX = event.getX();
		float pixelY = event.getY();

		switch (action) 
		{
			case MotionEvent.ACTION_DOWN:
				dragOnCoordsDown(pixelX, pixelY, screenWidth, screenHeight);
			break;
			case MotionEvent.ACTION_MOVE:
				dragOnCoordsMove(pixelX, pixelY, screenWidth, screenHeight);
			break;
		}

		return true;
	}

	public boolean onStopEvent()
	{
		bloqueado = true;
		return true;
	}

	private void dragOnCamaraDown(float pixelX, float pixelY, float screenWith, float screenHeight)
	{
		// Drag
		lastPixelX = pixelX;
		lastPixelY = pixelY;

		renderer.salvarCamara();
	}

	private void dragOnCamaraMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		renderer.recuperarCamara();
		renderer.camaraDrag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
	}

	private void dragOnCoordsDown(float pixelX, float pixelY, float screenWith, float screenHeight)
	{
		lastPixelX = pixelX;
		lastPixelY = pixelY;
	}

	private void dragOnCoordsMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		renderer.coordsDrag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);

		lastPixelX = pixelX;
		lastPixelY = pixelY;
	}
}
