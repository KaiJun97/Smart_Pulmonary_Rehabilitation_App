package com.example.rehabilitation.Activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.rehabilitation.Data.BleGattService;
import com.example.rehabilitation.Data.RecordValue;
import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TestActivity extends AppCompatActivity {
    private BluetoothGatt bleGatt;
    private BleGattService bleGattService;
    private static final UUID HEART_RATE_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String url_saveRecordedData = MainActivity.ipBaseAddress+"/test_save_data.php";
    private int iteration=1;
    private String recIDStr;
    private ArrayList<RecordValue> valuesArr= new ArrayList<>();
    private Button btnBack;
    private JSONArray array;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.bleGattService= PlayActivity.ble;
//        Intent intent= getIntent();
        //recIDStr= PlayActivity.recId;
        this.btnBack= findViewById(R.id.btnBack);
        this.handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleGatt = bleGattService.getBleGatt().getDevice().connectGatt(getApplicationContext(), false, bleGattCallback);
            }
        },2000);

        
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleGatt.disconnect();
                bleGattService.getBleGatt().close();
                /*
                * convert array into json format
                * post json \
                * sql statement to insert data
                *
                * */
                array = new JSONArray();
                for(int i =0;i<valuesArr.size();i++){
                    JSONObject obj= new JSONObject();
                    RecordValue val = valuesArr.get(i);
//                    Log.e("check record id",val.getRecID());
                    HashMap<String, String> params = new HashMap<String, String>();
                    try {
                        obj.put("indexVal", val.getRecDataID());
                        obj.put("recId",val.getRecID());
                        obj.put("value", val.getValue());
                        obj.put("time", val.getsTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
                }
//                JSONObject valueObj = new JSONObject();
//                try {
//                    valueObj.put("values", array);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Log.e("String", array.toString());
                //saveData(url_saveRecordedData);
                RequestQueue queue = Volley.newRequestQueue(TestActivity.this);
                JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.POST, url_saveRecordedData, array,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray jsonArray) {
                                Log.i("----Response", jsonArray + " " + url_saveRecordedData);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                    Log.i("Error", "Error");
                                    volleyError.printStackTrace();
                            }
                        });

                queue.add(jobReq);

                finish();
            }
        });
    }
//    private void saveData(final String urlWebService) {
//
//        class GetJSON extends AsyncTask<Void, Void, String> {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
////                Log.e("String", s);
//                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
////                try {
////
////                    recId= storeRecords(s);
////                    Log.e("message id ", recId);
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//
//                    URL url = new URL(urlWebService+ ("?json=" + array));
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    StringBuilder sb = new StringBuilder();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                    String json;
//                    while ((json = bufferedReader.readLine()) != null) {
//                        sb.append(json + "\n");
//                    }
//                    return sb.toString().trim();
//                } catch (Exception e) {
//                    return null;
//                }
//            }
//        }
//        GetJSON getJSON = new GetJSON();
//        getJSON.execute();
//    }

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

                String sDate = sdf.format(new Date());
                String s = new String(values, StandardCharsets.UTF_8);
//                Log.i("recID", recIDStr);

//                RecordValue recValues= new RecordValue(String.valueOf(iteration), PlayActivity.recId, String.valueOf(value), sDate);
//                valuesArr.add(recValues);
                Log.i("Array size", String.valueOf(valuesArr.size()));
                iteration++;

                // Log.d(DEBUG_TAG, "float values -> " + f);

                //Log.d(DEBUG_TAG, "Value -> " + characteristic.getValue());
            }


        }
    };
}
