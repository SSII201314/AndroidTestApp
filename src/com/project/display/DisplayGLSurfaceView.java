package com.project.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.creation.design.TDisplayTipo;
import com.game.data.Personaje;
import com.project.main.R;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private DisplayOpenGLRenderer renderer;
    private Context mContext;
    
    private TDisplayTipo tipoDisplay;
    
    private String nombre;
    private boolean personajeCargado, animacionFinalizada;
    
    private ExternalStorageManager manager;
	private CountDownTimer timer;
	private AudioPlayerManager player;
    
	/* SECTION Constructora */
	
    public DisplayGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);
       
        mContext = context;
        
        animacionFinalizada = true;
        
        timer = new CountDownTimer(TIME_DURATION, TIME_INTERVAL) {

			@Override
			public void onFinish() 
			{ 
				animacionFinalizada = true;
			}

			@Override
			public void onTick(long arg0) 
			{
				renderer.reproducirAnimacion();
				requestRender();
			}
        };
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m, TDisplayTipo e)
	{
		nombre = p.getNombre();
		manager = m;
		tipoDisplay = e;
		personajeCargado = true;
		
		renderer = new DisplayOpenGLRenderer(getContext(), p);
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
	}
	
	public void setParameters(TDisplayTipo e)
	{
		personajeCargado = false;
		tipoDisplay = e;
		
		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}
	
	/* SECTION M�todos abstractos OpenGLSurfaceView */
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) 
	{ 
		if(tipoDisplay == TDisplayTipo.Main)
		{
			if(personajeCargado)
			{
				int animacion = (int) Math.floor(Math.random()*4);
				
				if(animacionFinalizada)
				{
					switch(animacion)
					{
						case 0:
							seleccionarRun();
						break;
						case 1:
							seleccionarJump();
						break;
						case 2:
							seleccionarCrouch();
						break;
						case 3:
							seleccionarAttack();
						break;
					}
				}
			}
		}
	}
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION M�todos de Selecci�n de Estado */
	
	public void seleccionarRun() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarRun();
			requestRender();
			
			
			timer.start();
			player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_run));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarJump() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarJump();
			requestRender();
			
			timer.start();
			player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_jump));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarCrouch()
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarCrouch();
			requestRender();
			
			timer.start();
			player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_crouch));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarAttack() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarAttack();
			requestRender();
			
			timer.start();
			player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_attack));
			
			animacionFinalizada = false;
		}
	}
	
	public void seleccionarRetoque()
	{
		renderer.seleccionarRetoque(getHeight(), getWidth());
		setEstado(TTouchEstado.CamaraDetectors);
		
		requestRender();
	}
	
	public void seleccionarTerminado()
	{
		renderer.seleccionarTerminado();
		requestRender();
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	public boolean isEstadoReposo()
	{
		return renderer.isEstadoReposo();
	}
	
	public boolean isEstadoRetoque()
	{
		return renderer.isEstadoRetoque();
	}
	
	public boolean isEstadoTerminado()
	{
		return renderer.isEstadoTerminado();
	}
	
	public boolean isEstadoAnimacion()
	{
		return renderer.isEstadoAnimacion();
	}
	
	public Bitmap getCapturaPantalla()
	{
		renderer.seleccionarCaptura();
		setEstado(TTouchEstado.SimpleTouch);
		
		requestRender();
		return renderer.getCapturaPantalla();
	}
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
	public void saveData()
	{
		renderer.saveData();
	}
}
