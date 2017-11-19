package com.example.dc.colorflood;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LinearLayout colorsLayout;
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
        colorsLayout = (LinearLayout)findViewById(R.id.colors);
        myGame = (MyGame)findViewById(R.id.mygame);
        myGame.initLevel(lvlWidth, lvlHeight, nbColors);

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

        LinearLayout.LayoutParams param = params;




        for(int i = 0; i < nbColors; i++)
        {
            final Button bt = new Button(this);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGame.lvl.changeColor((int)bt.getTag(R.id.button_number));
                    myGame.update();
                }
            });
            bt.setTag(R.id.button_number, i);
            bt.setBackgroundColor(myGame.lvl.getCasesColors()[i]);
            bt.setLayoutParams(param);
            colorsLayout.addView(bt);
        }

        myGame.update();
    }
}
