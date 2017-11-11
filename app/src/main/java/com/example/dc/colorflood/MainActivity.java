package com.example.dc.colorflood;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LinearLayout colorsLayout;
    MyGame myGame;

    int[] colors;

    int nb_colors = 5;
    int w = 10, h = 10;
    int[][] cases;
    Random random;

    public MainActivity() {
        super();
        random = new Random();
        cases = new int[h][w];
        colors = new int[nb_colors];
        double part = 1.0 / (double)nb_colors;
        initLevel();
    }

    void initLevel()
    {
        double part = 1.0 / (double)nb_colors;
        //Log.d(getClass().getSimpleName(), "Part : " + part);
        for(int i = 0; i < nb_colors; i++)
        {
            colors[i] = Color.HSVToColor(255, new float[]{(float)((double)i * part * 360.0), 0.5f, 1.0f});
            //Log.e(getClass().getSimpleName(), "" + Color.red(colors[i]) + ", " + Color.green(colors[i]) + ", " + Color.blue(colors[i]) );
        }
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                cases[i][j] = random.nextInt(nb_colors);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorsLayout = (LinearLayout)findViewById(R.id.colors);
        myGame = (MyGame)findViewById(R.id.mygame);

        myGame.setLevel(cases, w, h, colors);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );

        for(int i = 0; i < 5; i++)
        {
            final Button bt = new Button(this);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGame.cases[0][0] = (int)bt.getTag(R.id.button_number);
                    myGame.update();
                }
            });
            bt.setTag(R.id.button_number, i);
            bt.setBackgroundColor(colors[i]);
            bt.setLayoutParams(param);
            colorsLayout.addView(bt);
        }
    }
}
