package com.example.dc.colorflood;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    ButtonBar colorsButtonsLayout;
    MyGame myGame;
    int lvlHeight = 10, lvlWidth = 10;
    int nbColors = 5;
    Context context;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.colorsButtonsLayout = findViewById(R.id.colors);
        this.myGame = findViewById(R.id.mygame);

        this.myGame.initLevel(this.lvlWidth, this.lvlHeight, this.nbColors);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics()
        );
        params.setMargins(px, px, px, px);

        this.colorsButtonsLayout.setParams(params);
        this.colorsButtonsLayout.setBtnCallback(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myGame.lvl.changeColor((int)v.getTag(R.id.button_number));
                myGame.update();
            }
        });

        this.colorsButtonsLayout.addButtons(this.myGame.lvl.getCasesColors());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.myGame.lvl.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.myGame.lvl.restoreState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
