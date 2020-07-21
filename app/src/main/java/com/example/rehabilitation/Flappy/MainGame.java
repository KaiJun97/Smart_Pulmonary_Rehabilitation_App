package com.example.rehabilitation.Flappy;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rehabilitation.Activity.GameActivity;
import com.example.rehabilitation.Activity.MainActivity;
import com.example.rehabilitation.Activity.PlayActivity;
import com.example.rehabilitation.Data.BleGattService;
import com.example.rehabilitation.Data.RecordValue;
import com.example.rehabilitation.R;

import org.json.JSONArray;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;

public class MainGame extends AppCompatActivity {

    private Button btnBack;

    public static int jump() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        try {
            AppConstants.initialization(this.getApplicationContext());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame(View view){
        //Log.i("ImageButton","clicked");
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}
