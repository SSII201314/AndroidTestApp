package com.android.dialog;

import android.content.Context;
import android.widget.TextView;

import com.project.main.R;

public class TextDialog extends WindowDialog
{
	private TextView text;
	
	public TextDialog(Context context, int layout)
	{
		super(context, layout, false);
		text = (TextView) findViewById(R.id.textViewDialog1);
	}

	/* M�todos P�blicos */
	
	public void setText(int message)
	{
		text.setText(message);
	}
}
