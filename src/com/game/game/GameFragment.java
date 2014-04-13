package com.game.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.storage.InternalStorageManager;
import com.android.view.OpenGLFragment;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.game.select.TTipoLevel;
import com.project.main.GamePreferences;
import com.project.main.R;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;

	private InternalStorageManager internalManager;

	private InstanciaNivel level;
	private Personaje personaje;

	private GameOpenGLSurfaceView canvas;
	private TextView textoPuntuacion;
	private ImageButton botonPlay;
	private ImageView[] imagenVidas;

	private boolean gamePaused;

	/* Constructora */

	public static final GameFragment newInstance(Personaje p, InternalStorageManager m, InstanciaNivel l)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(p, m, l);
		return fragment;
	}

	private void setParameters(Personaje p, InternalStorageManager m, InstanciaNivel l)
	{
		personaje = p;
		internalManager = m;
		level = l;
	}

	public interface GameFragmentListener
	{
		public void onGameFinished(TTipoLevel level, int score, int idImage, String nameLevel, boolean perfecto);

		public void onGameFailed(TTipoLevel level, int idImage);
	}

	/* M�todos Fragment */

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (GameFragmentListener) activity;

		gamePaused = true;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_layout, container, false);
		
		imagenVidas = new ImageView[GamePreferences.MAX_LIVES];

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewGame1);
		imageBackground.setBackgroundResource(level.getFondoNivel().getIdTexturaCielo());

		canvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(personaje, internalManager, this, level);
		
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
		onScoreChanged(score);
		
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
	public void onScoreChanged(int score)
	{
		textoPuntuacion.setText(getActivity().getString(R.string.text_game_score)+" "+score);
	}
	
	@Override
	public void onLivesChanged(int lives)
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
}
