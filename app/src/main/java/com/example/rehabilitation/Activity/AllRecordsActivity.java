package com.example.rehabilitation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rehabilitation.Data.JSONParser;
import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AllRecordsActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnBack;
    private String username;
    private ProgressDialog pDialog;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "username";
    private static final String TAG_PID = "recId";
    private ArrayList<String> recordID = new ArrayList<>();
    private static final String url_getAllRecords = MainActivity.ipBaseAddress+"/get_user_records.php";
    private int uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);
        JSONParser jParser = new JSONParser();
        final ArrayList<HashMap<String, String>> recordsList;
        this.username= MainActivity.username;
        this.uId=MainActivity.uId;

        ArrayList<String> listItem= new ArrayList<String>();

        final ArrayList<String> listUsername = new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,listUsername);


        recordsList = new ArrayList<HashMap<String, String>>();
        pDialog = new ProgressDialog(this);
        pDialog = new ProgressDialog(AllRecordsActivity.this,R.style.AppCompatAlertDialogStyle);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setMessage("Loading records ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        this.listView=(ListView) findViewById(R.id.list);
        this.btnBack=(Button) findViewById(R.id.btnBack);
        listView.setAdapter(adapter);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int recordPosition = i;
                String recID = String.valueOf(recordID.get(recordPosition));
                Intent intent= new Intent(AllRecordsActivity.this, GraphActivity.class);
                intent.putExtra("recID",recID);

                startActivity(intent);

            }

        });

        getJSON(url_getAllRecords);

        HashMap<String,String> params = new HashMap();
        params.put("username", username);

        JSONArray dataJson = new JSONArray();
        Log.i("dataJson", dataJson.toString());
        //postData(url_getAllRecords, dataJson, 1);


    }
    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService+ ("?username=" + username)+("&uId="+uId));
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

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] records = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            recordID.add(""+obj.getString("recId"));
            records[i] = "User ID:"+obj.getInt("uId")+"\n"+"Username: "+obj.getString("name")+"\n"+"Record ID: "+obj.getString("recId")+"\n"+"Date: "+obj.getString("date")+"\n"+"Game: "+obj.getString("game");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, records);
        pDialog.dismiss();
        listView.setAdapter(arrayAdapter);
    }


}
