package com.character.select;

import com.creation.data.TTipoMovimiento;

import android.graphics.Bitmap;

public interface OnCharacterListener
{
	public void onCharacterSelected();
	public void onCharacterRepainted();
	public void onCharacterDeleted();
	public void onCharacterRenamed();
	public void onCharacterExported();
	
	public void onPostPublished(String text, Bitmap bitmap);
	public void onSetSwipeable(boolean swipeable);
	public void onPlaySound(TTipoMovimiento tipo);
}
