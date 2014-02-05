package com.create.paint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.dialog.ColorPicker;
import com.android.dialog.SizePicker;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.R;

public class PaintFragment extends Fragment
{
	private PaintFragmentListener mCallback;
	private Context mContext;
	
	private PaintGLSurfaceView canvas;
	private ColorPicker colorPicker;
	private SizePicker sizePicker;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
	private Esqueleto esqueletoActual;
	
	/* Constructora */
	
	public static final PaintFragment newInstance(Esqueleto e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(e);
		return fragment;
	}
	
	private void setParameters(Esqueleto e)
	{
		esqueletoActual = e;
	}
	
	public interface PaintFragmentListener
	{
        public void onPaintReadyButtonClicked(Textura t);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (PaintFragmentListener) activity;
		mContext = activity.getApplicationContext();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_paint_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		canvas.setParameters(esqueletoActual);
		
		botonPincel = (ImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		botonCubo = (ImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		botonColor = (ImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		botonSize = (ImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		botonEye = (ImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonMano = (ImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonNext = (ImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonPaint10);
		
		botonNext.setEnabled(false);
		botonPrev.setEnabled(false);
		botonDelete.setEnabled(false);
		
		botonPincel.setOnClickListener(new OnPincelClickListener());	
		botonCubo.setOnClickListener(new OnCuboClickListener());
		botonColor.setOnClickListener(new OnColorClickListener());
		botonSize.setOnClickListener(new OnSizeClickListener());
		botonEye.setOnClickListener(new OnEyeClickListener());
		botonMano.setOnClickListener(new OnManoClickListener());
		botonNext.setOnClickListener(new OnNextClickListener());
		botonPrev.setOnClickListener(new OnPrevClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonReady.setOnClickListener(new OnReadyClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				canvas.onTouch(view, event);
				actualizarBotones();
				
				return true;
			}
		});
		
		return rootView;
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
		canvas.onPause();
	}
	
	/* M�todos abstractos de OpenGLFragment */
	
	private void actualizarBotones()
	{
		if(canvas.bufferSiguienteVacio())
		{
			botonNext.setEnabled(false);
		}
		else
		{
			botonNext.setEnabled(true);
		}
		
		if(canvas.bufferAnteriorVacio())
		{
			botonPrev.setEnabled(false);
			botonDelete.setEnabled(false);
		}
		else
		{
			botonPrev.setEnabled(true);
			botonDelete.setEnabled(true);
		}
	}
	
	/* Listener de Botones */
	
	private class OnPincelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();
		}
    }
    
    private class OnCuboClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();
		}
    }
    
    private class OnColorClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			int color = canvas.getColorPaleta();
			canvas.seleccionarColor(color);
			
			if (colorPicker == null)
			{
				colorPicker = new ColorPicker(mContext, ColorPicker.VERTICAL, canvas, color);    	
			}
			colorPicker.show(v);
		}
    }
    
    private class OnSizeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if (sizePicker == null) 
			{
				sizePicker = new SizePicker(mContext, SizePicker.VERTICAL, canvas);    	
			}
			sizePicker.show(v);
		}
    }
    
    private class OnEyeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			// TODO
		}
	}
    
    private class OnManoClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();
		}
    }
    
    private class OnPrevClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.anteriorAccion();
			
			actualizarBotones();
		}
    }
    
    private class OnNextClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.siguienteAccion();
	
			actualizarBotones();
		}
    }
    
    private class OnDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
				
			actualizarBotones();
		}
    }

    private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.capturaPantalla();
			
			mCallback.onPaintReadyButtonClicked(canvas.getTextura());
		}
    }
}
