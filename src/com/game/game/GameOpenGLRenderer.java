package com.game.game;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.view.BackgroundDataSaved;
import com.android.view.OpenGLRenderer;
import com.game.data.Background;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.project.main.R;
import com.project.model.GamePreferences;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Background
	private Background background;

	// Protagonista
	private Personaje personaje;

	// Enemigos
	private List<Entidad> tipoEnemigos;
	private List<InstanciaEntidad> listaEnemigos;
	
	// Puntuancion
	private int puntuacion;
	private boolean puntuacionModificada;
	
	// Texturas
	private boolean texturasCargadas;

	/* Constructura */

	public GameOpenGLRenderer(Context context, int color, Personaje p, InstanciaNivel l)
	{
		super(context, color);

		personaje = p;
		background = l.getFondoNivel();

		tipoEnemigos = l.getTipoEnemigos();
		listaEnemigos = l.getListaEnemigos();
		
		personaje.reiniciarVidas();
		personaje.activarBurbuja();
		
		puntuacion = 0;
		puntuacionModificada = false;
		
		texturasCargadas = false;
		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_level_title), mContext.getString(R.string.text_processing_level_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while(!texturasCargadas);
				alert.dismiss();
			}
		});
		
		thread.start();
	}

	/* M�todos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// BackGround
		seleccionarTexturaFondo(background.getIdTexturaFondos());

		// Protagonista
		personaje.cargarTextura(gl, this, mContext);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().cargarTextura(gl, this, mContext);
		}
		
		texturasCargadas = true;
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		gl.glPushMatrix();

			gl.glTranslatef(GamePreferences.DISTANCE_GAME_RIGHT(), GamePreferences.DISTANCE_GAME_BOTTOM(), 0.0f);
		
			// Protagonista
			personaje.dibujar(gl, this);
	
			// Cola Enemigos
			Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
			while (it.hasNext())
			{
				InstanciaEntidad instancia = it.next();
				Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
				instancia.dibujar(gl, this, entidad);
			}

		gl.glPopMatrix();
	}

	/* M�todos de Modificaci�n de Estado */

	public void seleccionarRun()
	{
		personaje.mover();
	}

	public void seleccionarJump()
	{
		personaje.saltar();
	}

	public void seleccionarCrouch()
	{
		personaje.agachar();
	}

	public void seleccionarAttack()
	{
		personaje.atacar();
	}
	
	public void pararAnimacion()
	{
		personaje.reposo();
	}
	
	public boolean reproducirAnimacion()
	{
		// Background
		desplazarTexturaFondo();

		// Lista Enemigos
		Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
		while (it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			
			if(!instancia.isDerrotado())
			{
				instancia.avanzar(this, entidad);
				
				if(instancia.getPosicionX() < -entidad.getWidth())
				{
					switch(entidad.getTipo())
					{
						case Enemigo:
							puntuacion += GamePreferences.SCORE_ACTION_WRONG;
							puntuacionModificada = true;
						break;
						case Obstaculo:
							puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
							puntuacionModificada = true;
						break;
						case Misil:
							puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
							puntuacionModificada = true;
						default:
						break;
					}
					
					instancia.setDerrotado();
				}
			}
		}

		// Enemigos		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OPPONENTS; i++)
		{
			tipoEnemigos.get(i).animar();
		}
		
		// Personaje
		return personaje.animar();
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public TEstadoGame isJuegoFinalizado()
	{
		if (fondoFinalFijado)
		{
			puntuacion += GamePreferences.SCORE_LEVEL_COMPLETED;
		
			return TEstadoGame.FinJuegoVictoria;
		}

		Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
		while (it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());

			if (!instancia.isDerrotado())
			{
				switch (personaje.colision(entidad, instancia))
				{
					case EnemigoDerrotado:
						instancia.setDerrotado();
						
						puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
						return TEstadoGame.CambioPuntuacion;
					case Colision:
						instancia.setDerrotado();
						personaje.quitarVida();
						
						puntuacion += GamePreferences.SCORE_LOSE_LIFE;

						if (!personaje.isAlive())
						{						
							return TEstadoGame.FinJuegoDerrota;
						}
						
						return TEstadoGame.VidaPerdida;
					default:
					break;
				}
			}
		}
		
		if(puntuacionModificada)
		{
			puntuacionModificada = false;
			
			return TEstadoGame.CambioPuntuacion;
		}

		return TEstadoGame.Nada;
	}
	
	public int getPuntuacion()
	{
		return puntuacion;
	}
	
	public int getVidas()
	{
		return personaje.getVidas();
	}

	/* M�todos de Guardado de Informaci�n */

	public BackgroundDataSaved saveData()
	{
		// Personaje
		personaje.descargarTextura(this);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().descargarTextura(this);
		}
		
		return backgroundSaveData();
	}
	
	public void restoreData(BackgroundDataSaved data)
	{
		backgroundRestoreData(data);
	}
}
