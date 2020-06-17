package com.example.rehabilitation.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.rehabilitation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class ExportDataActivity extends AppCompatActivity {
    private ListView listView;
    Button btnBack;
    SQLiteDatabase db;
    int recordPosition;
    String recID;
    Date time;
    private String username;
    private ProgressDialog pDialog;
    private static final String url_getAllRecords = MainActivity.ipBaseAddress+"/get_user_records.php";
    private static final String url_getAllValues = MainActivity.ipBaseAddress + "/get_user_record_values.php";
    private ArrayList<String> listID= new ArrayList<>();
    private int uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        this.listView = (ListView) findViewById(R.id.exportList);
        this.btnBack = (Button) findViewById(R.id.btnBack);

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.username= MainActivity.username;
        this.uId=MainActivity.uId;

        ArrayList<String> listItem= new ArrayList<String>();
        final ArrayList<String> listUsername = new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,listUsername);


        //recordsList = new ArrayList<HashMap<String, String>>();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading records ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

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
                String recID = String.valueOf(listID.get(recordPosition));
                getValuesJson(url_getAllValues, recID);
            }

        });

        getJSON(url_getAllRecords);



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
            records[i] = "User ID:"+obj.getInt("uId")+"\n"+"Username: "+obj.getString("name")+"\n"+"Record ID: "+obj.getString("recId")+"\n"+"Date: "+obj.getString("date");
            listID.add(""+obj.getString("recId"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, records);
        pDialog.dismiss();
        listView.setAdapter(arrayAdapter);
    }



    private void getValuesJson(final String urlWebService, final String recordId) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String u) {
                super.onPostExecute(u);
                try {
                    export(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService+ ("?recID=" + recordId));
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
    private void export(String json) throws JSONException {
        StringBuilder data = new StringBuilder();
        data.append("index,record ID,Value,Time");
        JSONArray jsonArray = new JSONArray(json);
        String[] records = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            data.append("\n"+obj.getInt("recDataId")+","+obj.getInt("recId")+","+obj.getInt("indexVal")+","+
                    obj.getInt("value")+","+obj.getString("time"));

        }
        try{
            FileOutputStream out= openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();
            Context context= getApplicationContext();
            File fileLocation= new File(getFilesDir(), "data.csv");
            Uri path= FileProvider.getUriForFile(context, "com.example.rehabilitation.fileprovider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
//            Toast.makeText(, , )

        }catch(Exception e)
        {
            e.printStackTrace();
        }


    }


}
