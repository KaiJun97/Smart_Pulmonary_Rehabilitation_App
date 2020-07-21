package com.example.rehabilitation.Activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rehabilitation.Data.BleGattService;
import com.example.rehabilitation.Data.RecordValue;
import com.example.rehabilitation.Flappy.MainGame;
import com.example.rehabilitation.Piano.PianoActivity;
import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SelectGameActivity extends AppCompatActivity {
    private ImageButton btnBird, btnMusic;
    public static int jump;
    public static BluetoothGatt bleGatt;
    public static BleGattService bleGattService;
    private static final UUID HEART_RATE_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String url_setUserRecord = MainActivity.ipBaseAddress+"/set_user_record.php";

    private int iteration=1;
    private String recIDStr;
    public static ArrayList<RecordValue> valuesArr= new ArrayList<>();
    private JSONArray array;
    private String[] records;
    public static String recId;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private Handler handler;
    private String recID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        this.btnBird=findViewById(R.id.btnBird);
        this.btnMusic= findViewById(R.id.btnMusic);
        this.bleGattService= PlayActivity.ble;
        recID= PlayActivity.recId;

        this.btnBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this, MainGame.class);
                getRecords(url_setUserRecord, "Bird");
                bleGatt = bleGattService.getBleGatt().getDevice().connectGatt(getApplicationContext(), false, bleGattCallback);
                startActivity(intent);
            }
        });
        this.btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this, PianoActivity.class);
                getRecords(url_setUserRecord, "Piano");
                bleGatt = bleGattService.getBleGatt().getDevice().connectGatt(getApplicationContext(), false, bleGattCallback);
                startActivity(intent);
            }
        });

    }
    private void getRecords(final String urlWebService, final String game) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("String", s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {

                    recId = storeRecords(s);
                    Log.e("message id ", recId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService+ ("?username=" + MainActivity.username)+("&uId="+MainActivity.uId)+("&game="+game));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private String storeRecords(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        records = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            // recordID.add(""+obj.getString("recId"));
            records[i] = obj.getString("recId");
        }
        Log.e("Record ID", records[records.length-1]);
        return records[records.length-1];
    }
    private final BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Connected");
                Log.d(DEBUG_TAG, "Connected");


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Not Connected");
                Log.d(DEBUG_TAG, "Disconnected");

            }
            gatt.discoverServices();
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(DEBUG_TAG, "onServicesDiscovered");

            List<BluetoothGattService> gattServices = gatt.getServices();
            for (BluetoothGattService gattService : gattServices) {
                Log.d(DEBUG_TAG, "Service UUID -> " + gattService.getUuid().toString());
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic :
                        gattCharacteristics) {
                    Log.d(DEBUG_TAG, "UUID -> " + gattCharacteristic.getUuid().toString());
                    if (gattCharacteristic.getUuid().equals(HEART_RATE_CHAR_UUID)) {
                        Log.d(DEBUG_TAG, "Found it");
                        Log.d(DEBUG_TAG, "Char property -> " + gattCharacteristic.getProperties());
                        //gatt.readCharacteristic(gattCharacteristic);
                        gatt.setCharacteristicNotification(gattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(
                                UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    }
                }
            }


            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            Log.d(DEBUG_TAG, "onCharacteristicRead");

            Log.d(DEBUG_TAG, "Value -> " + characteristic.getStringValue(0));

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        // Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(DEBUG_TAG, "onCharacteristicChanged");
            byte[] values = characteristic.getValue();
            //float f = buffer.wrap(values).getFloat();

            for (byte value : values) {
                Log.d(DEBUG_TAG, "value -> " + value);
                jump = value;
                String sDate = sdf.format(new Date());
                String s = new String(values, StandardCharsets.UTF_8);
//                Log.i("recID", recIDStr);

                RecordValue recValues= new RecordValue(String.valueOf(iteration),recId, String.valueOf(value), sDate);
                valuesArr.add(recValues);
                Log.i("Array size", String.valueOf(valuesArr.size()));
                iteration++;

                // Log.d(DEBUG_TAG, "float values -> " + f);

                //Log.d(DEBUG_TAG, "Value -> " + characteristic.getValue());
            }


        }
    };
}
