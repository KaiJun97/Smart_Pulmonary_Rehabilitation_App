package com.example.rehabilitation.Flappy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.rehabilitation.Activity.MainActivity;
import com.example.rehabilitation.Activity.SelectGameActivity;
import com.example.rehabilitation.Data.RecordValue;
import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GameOver extends AppCompatActivity {

    TextView tvScore, tvPersonalBest;
    private JSONArray array;
    private static final String url_saveRecordedData = MainActivity.ipBaseAddress + "/test_save_data.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        int score = getIntent().getExtras().getInt("score");
        SharedPreferences pref = getSharedPreferences("MyPref", 0);
        int scoreSP = pref.getInt("scoreSP", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (score > scoreSP) {
            scoreSP = score;
            editor.putInt("scoreSP", scoreSP);
            editor.commit();
        }
        tvScore = findViewById(R.id.tvScore);
        tvPersonalBest = findViewById(R.id.tvPersonalBest);
        tvScore.setText("" + score);
        tvPersonalBest.setText("" + scoreSP);
    }

    /*public void restart(View view){
        Intent intent = new Intent(GameOver.this, MainGame.class);
        startActivity(intent);
        finish();
    }*/

    public void exit(View view) {
        Intent intent = new Intent(GameOver.this, MainGame.class);


    }
}
