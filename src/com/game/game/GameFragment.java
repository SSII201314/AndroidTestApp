package com.game.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.view.OpenGLFragment;
import com.creation.data.TTipoMovimiento;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.game.select.TTipoLevel;
import com.project.main.R;
import com.project.model.GamePreferences;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;

	private InstanciaNivel level;
	private Personaje personaje;

	private GameOpenGLSurfaceView canvas;
	private TextView textoPuntuacion;
	private ImageButton botonPlay;
	private ImageView[] imagenVidas;

	private boolean gamePaused;

	/* Constructora */

	public static final GameFragment newInstance(GameFragmentListener c, Personaje p, InstanciaNivel l)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(c, p, l);
		return fragment;
	}

	private void setParameters(GameFragmentListener c, Personaje p, InstanciaNivel l)
	{
		mCallback = c;
		personaje = p;
		level = l;
		gamePaused = true;
	}

	public interface GameFragmentListener
	{
		public void onGameFinished(final TTipoLevel nivel, final int score, final int idImage, final String nameLevel, final boolean perfecto);
		public void onGameFailed(final TTipoLevel level, final int idImage);
		public void onGamePlaySound(final TTipoMovimiento tipo);
	}

	/* M�todos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_layout, container, false);
		
		imagenVidas = new ImageView[GamePreferences.MAX_LIVES];

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewGame1);
		imageBackground.setBackgroundResource(level.getFondoNivel().getIdTexturaCielo());

		canvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(this, personaje, level);
		
		textoPuntuacion = (TextView) rootView.findViewById(R.id.textViewGame1);
		
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonGame1);
		botonPlay.setOnClickListener(new onPlayGameClickListener());
		
		for(int i = 0; i < GamePreferences.MAX_LIVES; i++)
		{
			int id = getActivity().getResources().getIdentifier(GamePreferences.RESOURCE_IMAGE_HEART + (i + 1), "id", getActivity().getPackageName());
			
			imagenVidas[i] = (ImageView) rootView.findViewById(id);
		}

		setCanvasListener(canvas);

		reiniciarInterfaz();
		actualizarInterfaz();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
		textoPuntuacion = null;
		botonPlay = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.saveData();
		canvas.onPause();
	}

	/* M�todos abstractos de OpenGLFragment */

	@Override
	protected void reiniciarInterfaz()
	{
		botonPlay.setBackgroundResource(R.drawable.icon_game_pause);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (gamePaused)
		{
			botonPlay.setBackgroundResource(R.drawable.icon_game_play);
		}
	}

	/* M�todos Listener onClick */

	private class onPlayGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			gamePaused = !gamePaused;

			if (gamePaused)
			{
				canvas.seleccionarPause();

				sendToastMessage(R.string.text_game_paused);
			}
			else
			{
				canvas.seleccionarResume();
			}

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	/* M�todos de OnGameListener */

	@Override
	public void onGameFinished(int score, int lives)
	{
		onGameScoreChanged(score);
		
		if(lives == GamePreferences.MAX_LIVES)
		{
			mCallback.onGameFinished(level.getTipoNivel(), score, level.getFondoNivel().getIdTextureLevelPerfected(), level.getNombreNivel(), true);

		}
		else
		{
			mCallback.onGameFinished(level.getTipoNivel(), score, level.getFondoNivel().getIdTextureLevelCompleted(), level.getNombreNivel(), false);
		}
	}

	@Override
	public void onGameFailed()
	{
		mCallback.onGameFailed(level.getTipoNivel(), level.getFondoNivel().getIdTextureGameOver());
	}
	
	@Override
	public void onGameScoreChanged(int score)
	{
		textoPuntuacion.setText(getActivity().getString(R.string.text_game_score)+" "+score);
	}
	
	@Override
	public void onGameLivesChanged(int lives)
	{
		for(int i = 0; i < GamePreferences.MAX_LIVES; i++)
		{
			imagenVidas[i].setBackgroundResource(R.drawable.lives_heart_broken);
		}
		
		for(int i = 0; i < lives; i++)
		{
			imagenVidas[i].setBackgroundResource(R.drawable.lives_heart);
		}
	}

	@Override
	public void onGamePlaySound(TTipoMovimiento tipo)
	{
		mCallback.onGamePlaySound(tipo);
	}
}
