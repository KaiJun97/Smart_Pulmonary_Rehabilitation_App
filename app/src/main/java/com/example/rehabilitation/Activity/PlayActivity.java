package com.example.rehabilitation.Activity;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rehabilitation.Data.BleGattService;
import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT =1 ;
    private static final String DEBUG_TAG = "DEBUG_TAG";
    //public static BluetoothGatt bleGatt;
    private Button btnPlay;
    private Button btnConnect, btnBack;
    private ListView btList;
    SQLiteDatabase db;

    private Handler handler;
    private ByteBuffer buffer;

    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter<String> testAdapter;
    private ArrayAdapter<String> deviceAdapter;

    private BluetoothDevice bleDevice;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private ArrayList<ScanResult> results = new ArrayList<>();
    private ScanSettings settings;

    private String[] records;

    private ScanResult device;
    public static BleGattService ble;

    private Intent intent;

    public static String recId;

    private ListView bluetoothList;

    private boolean completed = false;

    static final UUID HR_SERVICE_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String url_setUserRecord = MainActivity.ipBaseAddress+"/set_user_record.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        deviceAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        testAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1);

        this.handler= new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        this.btnConnect = (Button) findViewById(R.id.connect);
        this.btnBack=(Button) findViewById(R.id.btnBack);
        this.btnPlay=(Button) findViewById(R.id.btnPlay);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bleGatt!=null) {

                    Intent intent = new Intent(PlayActivity.this, SelectGameActivity.class);


                    getRecords(url_setUserRecord);


//                    intent.putExtra("ID", recId);
                    startActivity(intent);

                }
                else
                    Toast.makeText(getApplicationContext(),"Connect to a BLE device", Toast.LENGTH_LONG).show();
            }
        });



        this.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
                final Dialog d = new Dialog(PlayActivity.this); //open up dialog box with listview
                d.setContentView(R.layout.bluetooth_device);
                d.setTitle("Devices");
                d.show();

                //stopScan();

                Button scanBtn = d.findViewById(R.id.scanBluetooth);
                bluetoothList = d.findViewById(R.id.bluetoothDeviceList);
                bluetoothList.setAdapter(deviceAdapter);
                bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        device = results.get(i);
                        Toast.makeText(getApplicationContext(), device.getDevice().getName(), Toast.LENGTH_LONG).show();

                        ble = new BleGattService();
                        bleGatt=device.getDevice().connectGatt(getApplicationContext(), false, bleGattCallback);
                        ble.setBleGatt(bleGatt);
                        //finish();
                        d.cancel();
                    }
                });

                scanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //clear all list and adapters before scanning again

                        deviceList.clear();
                        deviceAdapter.clear();
                        results.clear();
                        startScan();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopScan();
                            }
                        },3000);
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopScan();
                    }
                },3000);
            }
        });
    }


    private void getRecords(final String urlWebService) {

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

                    recId= storeRecords(s);
                    Log.e("message id ", recId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService+ ("?username=" + MainActivity.username));
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

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    private void checkBluetooth()
    {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void stopScan(){
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        bleScanner.stopScan(scanCallback);
    }
    private void startScan() {
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner != null) { //setting up of scanner
            final ScanFilter scanFilter =new ScanFilter.Builder().build();
            settings =new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bleScanner.startScan(Arrays.asList(scanFilter), settings, scanCallback);
            //stopScan();
        }
        else
            checkBluetooth();
    }

    private ScanCallback scanCallback = new ScanCallback() { //scan and return device results
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            System.out.println("@@@@@@@@@ "+callbackType + result);
            if (bleScanner != null && !deviceList.contains(result.getDevice().getName())) {
                deviceList.add(result.getDevice().getName());
                String device = result.getDevice().getName() + "\n" + result.getDevice().getAddress();
                deviceAdapter.add(device); //Store device name and address
                results.add(result); //records found devices as ScanResult
            }

        }
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("TAG","onScanFailed");
        }

    };

    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState== BluetoothProfile.STATE_CONNECTED){
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Connected");
                Log.d(DEBUG_TAG, "Connected");


            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
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
            for (BluetoothGattService gattService : gattServices)
            {
                Log.d(DEBUG_TAG, "Service UUID -> " + gattService.getUuid().toString());
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic :
                        gattCharacteristics) {
                    Log.d(DEBUG_TAG, "UUID -> " + gattCharacteristic.getUuid().toString());
                    if (gattCharacteristic.getUuid().equals(HEART_RATE_CHAR_UUID))
                    {
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

    };
}
