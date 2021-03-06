package com.lib.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class BufferManager
{
	/* Métodos de Construcción de Buffer de Pintura */
	
	public static FloatBuffer buildBufferTexture()
	{
		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		return buildBufferVertexList(texture);
	}

	// Construcción de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	public static FloatBuffer buildBufferVertexList(float[] vertices)
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(vertices);
		buffer.position(0);

		return buffer;
	}

	// Construcción de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	public static FloatBuffer buildBufferVertexList(VertexArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		return buildBufferVertexList(arrayVertices);
	}

	// Construcción de un buffer de pintura para puntos a partir de una lista de indice de vertices
	public static FloatBuffer buildBufferVertexIndexList(HullArray contorno, VertexArray vertices)
	{
		float[] arrayVertices = new float[2 * contorno.getNumVertices()];

		int j = 0;
		for (short i = 0; i < contorno.getNumVertices(); i++)
		{
			short a = contorno.getVertex(i);
			
			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			j = j + 2;
		}

		return buildBufferVertexList(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer buildBufferTriangleList(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[12 * triangulos.getNumTriangles()];

		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			arrayVertices[j + 2] = vertices.getXVertex(b);
			arrayVertices[j + 3] = vertices.getYVertex(b);

			arrayVertices[j + 4] = vertices.getXVertex(b);
			arrayVertices[j + 5] = vertices.getYVertex(b);

			arrayVertices[j + 6] = vertices.getXVertex(c);
			arrayVertices[j + 7] = vertices.getYVertex(c);

			arrayVertices[j + 8] = vertices.getXVertex(c);
			arrayVertices[j + 9] = vertices.getYVertex(c);

			arrayVertices[j + 10] = vertices.getXVertex(a);
			arrayVertices[j + 11] = vertices.getYVertex(a);
			
			j = j + 12;			
		}

		return buildBufferVertexList(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_TRIANGLES
	public static FloatBuffer buildBufferTriangleFillList(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[6 * triangulos.getNumTriangles()];

		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			arrayVertices[j + 2] = vertices.getXVertex(b);
			arrayVertices[j + 3] = vertices.getYVertex(b);

			arrayVertices[j + 4] = vertices.getXVertex(c);
			arrayVertices[j + 5] = vertices.getYVertex(c);

			j = j + 6;
		}

		return buildBufferVertexList(arrayVertices);
	}

	/* Metodos de Actualización de Buffers de Pintura */

	// Actualiza los valores de un buffer de pintura para puntos
	public static void updateBufferVertexList(FloatBuffer buffer, VertexArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		buffer.put(arrayVertices);
		buffer.position(0);
	}

	// Actualizar los valores de un buffer de pintura para triangulos.
	// Uso para GL_LINES
	public static void updateBufferTriangleList(FloatBuffer buffer, TriangleArray triangulos, VertexArray vertices)
	{
		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));

			buffer.put(j + 2, vertices.getXVertex(b));
			buffer.put(j + 3, vertices.getYVertex(b));

			buffer.put(j + 4, vertices.getXVertex(b));
			buffer.put(j + 5, vertices.getYVertex(b));

			buffer.put(j + 6, vertices.getXVertex(c));
			buffer.put(j + 7, vertices.getYVertex(c));

			buffer.put(j + 8, vertices.getXVertex(c));
			buffer.put(j + 9, vertices.getYVertex(c));

			buffer.put(j + 10, vertices.getXVertex(a));
			buffer.put(j + 11, vertices.getYVertex(a));

			j = j + 12;
		}
	}

	// Actualiza los valores de un buffer de pintura para triangulos
	// Uso para GL_TRIANGLES
	public static void updateBufferTriangleFillList(FloatBuffer buffer, TriangleArray triangulos, VertexArray vertices)
	{
		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));

			buffer.put(j + 2, vertices.getXVertex(b));
			buffer.put(j + 3, vertices.getYVertex(b));

			buffer.put(j + 4, vertices.getXVertex(c));
			buffer.put(j + 5, vertices.getYVertex(c));

			j = j + 6;
		}
	}

	// Actualiza los valores de un buffer de pintura para indice puntos
	public static void updateBufferVertexIndexList(FloatBuffer buffer, HullArray contorno, VertexArray vertices)
	{
		int j = 0;
		for (short i = 0; i < contorno.getNumVertices(); i++)
		{
			short a = contorno.getVertex(i);
			
			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));
			
			j = j + 2;
		}
	}
	
	/* Métodos de Transformación de Puntos */
	
	public static void dragVertices(float vx, float vy, VertexArray vertices)
	{
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, x + vx, y + vy);
		}
	}

	public static void scaleVertices(float fx, float fy, float cx, float cy, VertexArray vertices)
	{
		dragVertices(-cx, -cy, vertices);
		scaleVertices(fx, fy, vertices);
		dragVertices(cx, cy, vertices);
	}

	public static void scaleVertices(float fx, float fy, VertexArray vertices)
	{
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, x * fx, y * fy);
		}
	}

	public static void rotateVertices(float angRad, float cx, float cy, VertexArray vertices)
	{
		dragVertices(-cx, -cy, vertices);
		rotateVertices(angRad, vertices);
		dragVertices(cx, cy, vertices);
	}

	public static void rotateVertices(float angRad, VertexArray vertices)
	{
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, (float) (x * Math.cos(angRad) - y * Math.sin(angRad)), (float) (x * Math.sin(angRad) + y * Math.cos(angRad)));
		}
	}
}
