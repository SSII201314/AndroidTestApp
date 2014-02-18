package com.create.deform;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.view.SwipeableViewPager;
import com.lib.utils.FloatArray;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Textura;
import com.project.main.R;

public class AnimationFragment extends Fragment
{
	private AnimationFragmentListener mCallback;
	
	private ImageButton botonReady;
	private SwipeableViewPager viewPager;
	
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	
	private Movimientos movimientos;
	
	/* Constructora */
	
	public static final AnimationFragment newInstance(Esqueleto e, Textura t)
	{
		AnimationFragment fragment = new AnimationFragment();
		fragment.setParameters(e, t);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t)
	{
		esqueletoActual = e;
		texturaActual = t;
	}
	
	public interface AnimationFragmentListener
	{
        public void onAnimationReadyButtonClicked(Movimientos m);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (AnimationFragmentListener) activity;
		movimientos = new Movimientos();
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
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_animation_layout, container, false);
		
		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonAnimation1);		
		botonReady.setOnClickListener(new OnReadyClickListener());	

		viewPager = (SwipeableViewPager) rootView.findViewById(R.id.pagerViewAnimation1);
		viewPager.setAdapter(getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		viewPager.setSwipeable(false);
		
		viewPager.addView(DeformFragment.newInstance(esqueletoActual, texturaActual), getString(R.string.title_animation_section_run));
		viewPager.addView(DeformFragment.newInstance(esqueletoActual, texturaActual), getString(R.string.title_animation_section_jump));
		viewPager.addView(DeformFragment.newInstance(esqueletoActual, texturaActual), getString(R.string.title_animation_section_down));
		viewPager.addView(DeformFragment.newInstance(esqueletoActual, texturaActual), getString(R.string.title_animation_section_attack));

        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonReady = null;
	}
	
	/* Listeners de Botones */
	
    private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			int i = 0;
			Iterator<DeformFragment> it = viewPager.iterator();
			while(it.hasNext())
			{
				List<FloatArray> movimiento = it.next().getMovimientos();
				
				if(movimiento != null)
					movimientos.set(movimiento, i);
				

				if(movimientos.get(i) == null)
					Log.d("TEST", "null "+i);
				else
					Log.d("TEST", "NO null "+i);
				
				i++;	
			}
			mCallback.onAnimationReadyButtonClicked(movimientos);
		}
    }
}
