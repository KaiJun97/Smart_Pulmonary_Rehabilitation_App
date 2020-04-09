package com.example.rehabilitation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rehabilitation.Game.MainGame;
import com.example.rehabilitation.R;

public class SelectGameActivity extends AppCompatActivity {
    private ImageButton btnBird, btnMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        this.btnBird=findViewById(R.id.btnBird);
        this.btnMusic= findViewById(R.id.btnMusic);

        this.btnBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this, MainGame.class);
                startActivity(intent);
            }
        });
    }
}
