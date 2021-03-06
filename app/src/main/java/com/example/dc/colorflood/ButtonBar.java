package com.example.dc.colorflood;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Layout pour afficher la barre de bouton du jeu
 */
public class ButtonBar extends LinearLayout {
    private View.OnClickListener btnCallback;
    private LinearLayout.LayoutParams params;

    public ButtonBar(Context context) {
        super(context);
    }

    public ButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBtnCallback(OnClickListener btnCallback) {
        this.btnCallback = btnCallback;
    }

    public void setParams(LayoutParams params) {
        this.params = params;
    }

    public void initButtons(int[] colors){
        this.removeAllViews();
        for(int i = colors.length-1; i >= 0; i--)
        {
            final Button bt = new Button(this.getContext());
            bt.setSoundEffectsEnabled(false);
            bt.setOnClickListener(this.btnCallback);
            bt.setTag(R.id.button_number, i);
            bt.setBackgroundColor(colors[i]);
            bt.setLayoutParams(this.params);
            this.addView(bt);
        }

    }

    public int getLength(){
        return this.getChildCount();
    }
}
