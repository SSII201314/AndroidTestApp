package com.game.game;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.game.data.Level;
import com.game.data.Personaje;
import com.project.main.R;

public class GameOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private GameOpenGLRenderer renderer;
    private OnGameListener listener;
    private Context mContext;
    
    private String nombrePersonaje;
    
    private boolean animacionFinalizada;
    private int contadorFrames;
    
    private ExternalStorageManager manager;
	private AudioPlayerManager player;
	
	private Handler handler;
	private Runnable task;
	
	private boolean threadActivo;
	private boolean finAnimacion;
	
	/* SECTION Constructora */
	
    public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.GameDetectors);
        
        mContext = context;
        
        animacionFinalizada = true;
        finAnimacion = false;
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m, OnGameListener gl, Level l)
	{        
        manager = m;
        nombrePersonaje = p.getNombre();
        listener = gl;
        
    	renderer = new GameOpenGLRenderer(getContext(), p, l);
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
        
        handler = new Handler();
        
        task = new Runnable() {
        	@Override
            public void run()
        	{        		 
        		//boolean primerosCiclos = contadorFrames < NUM_FRAMES_ANIMATION / 2;
        		
				finAnimacion = renderer.reproducirAnimacion();
				requestRender();
				
				if(finAnimacion)
				{
					renderer.seleccionarRun();
					animacionFinalizada = true;
				}

				switch(renderer.isJuegoFinalizado())
				{
					case Nada:
						handler.postDelayed(this, TIME_INTERVAL_ANIMATION);
					break;
					case FinJuegoVictoria:
						renderer.pararAnimacion();
	    				requestRender();
	                	
	                	listener.onGameFinished();
					break;
					case FinJuegoDerrota:
						renderer.pararAnimacion();
	    				requestRender();
	                	
	                	listener.onGameFailed();
					break;
				}
        	}
        };
        
        threadActivo = false;
        
        renderer.seleccionarRun();
	}
	
	/* SECTION M�todos abstractos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}
	
	/* SECTION M�todos de Selecci�n de Estado */

	public void seleccionarRun()
	{
		if(!threadActivo)
		{
			task.run();
			
			threadActivo = true;
		}
	}
	
	public void seleccionarJump() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarJump();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_jump));
			
			animacionFinalizada = false;	
			contadorFrames = 0;
		}
	}

	public void seleccionarCrouch()
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarCrouch();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_crouch));
			
			animacionFinalizada = false;
			contadorFrames = 0;
		}
	}

	public void seleccionarAttack() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarAttack();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_attack));
			
			animacionFinalizada = false;
			contadorFrames = 0;
		}
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
	public void saveData()
	{
		renderer.saveData();
	}
}
