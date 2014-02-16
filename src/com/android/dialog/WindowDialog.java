package com.android.dialog;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

public abstract class WindowDialog implements OnTouchListener
{
	private Context mContext;
	private View rootView;
	private PopupWindow popupWindow;
	private LayoutInflater layoutInflater;
	
	public WindowDialog(Context context, int id)
	{
		mContext = context;
		
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		rootView = layoutInflater.inflate(id, null);
		popupWindow = new PopupWindow(rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchInterceptor(this);
		popupWindow.setFocusable(true);
	}
	
	protected View findViewById(int id)
	{
		if(rootView != null)
		{
			return rootView.findViewById(id);
		}
		
		else return null;
	}
	
	protected ViewTreeObserver getViewTreeObserver()
	{
		if(rootView != null)
		{
			return rootView.getViewTreeObserver();
		}
		
		else return null;
	}
	
	protected void removeGlobalLayoutListener(OnGlobalLayoutListener listener)
	{
		rootView.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	}
	
	public void show(View view)
	{
		rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		int posX = view.getWidth()/4;
		int posY = rootView.getMeasuredHeight() + view.getHeight();
		
		popupWindow.showAsDropDown(view, posX, -posY);
	}
	
	protected void dismiss()
	{
		popupWindow.dismiss();
	}
	
	protected void showKeyBoard(Activity activity, EditText editText)
	{
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	protected void dismissKeyBoard(Activity activity, EditText editText)
	{
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int action = event.getAction();
		
		if(action == MotionEvent.ACTION_OUTSIDE)
		{
			onTouchOutsidePopUp(v, event);
			return true;
		}
		
		return false;
	}
	
	protected abstract void onTouchOutsidePopUp(View v, MotionEvent event);
}