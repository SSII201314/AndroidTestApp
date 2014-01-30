package com.example.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Esqueleto implements Serializable
{
	private static final long serialVersionUID = 666L;
	
	private String nombre;
	private ShortArray contorno;
	private FloatArray vertices;
	private ShortArray triangulos;
	private TexturaBMP textura;
	private FloatArray coordTextura;
	
	public Esqueleto(ShortArray contorno, FloatArray vertices, ShortArray triangulos)
	{
		this.nombre = "";
		this.contorno = contorno;
		this.vertices = vertices;
		this.triangulos = triangulos;
	}
	
	public void setTexture(TexturaBMP textura, FloatArray coordTextura)
	{
		this.textura = textura;
		this.coordTextura = coordTextura;
	}
	
	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}
	
	public String getNombre()
	{
		return nombre;
	}

	public ShortArray getContorno()
	{
		return contorno;
	}

	public FloatArray getVertices()
	{
		return vertices;
	}

	public ShortArray getTriangulos()
	{
		return triangulos;
	}

	public TexturaBMP getTextura()
	{
		return textura;
	}

	public FloatArray getCoordTextura()
	{
		return coordTextura;
	}
}
