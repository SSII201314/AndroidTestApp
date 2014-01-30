package com.view.select;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.data.Esqueleto;

public class SelectGLSurfaceView extends GLSurfaceView
{
	// Renderer
    private final SelectOpenGLRenderer renderer;
    
    public SelectGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);

        // Asignar Renderer al GLSurfaceView
        renderer = new SelectOpenGLRenderer(context);
        setRenderer(renderer);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		renderer.setEsqueleto(esqueleto);
	}
}
