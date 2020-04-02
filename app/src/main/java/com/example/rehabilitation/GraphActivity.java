package com.example.rehabilitation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rehabilitation.Data.RecordValue;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {
    private GraphView graphView;
    SQLiteDatabase db;
    private ListView listView;
    private ProgressDialog pDialog;
    private String recID;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private static final String url_getAllValues = MainActivity.ipBaseAddress + "/get_user_record_values.php";
    private LineGraphSeries<DataPoint> series;

    private ArrayList<RecordValue> valuesArr;
    Date time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent intent=getIntent();

        this.listView = (ListView) findViewById(R.id.list);

        this.graphView = (GraphView) findViewById(R.id.graphView);
        this.recID=intent.getStringExtra("recID");

        ArrayList<String> listItem = new ArrayList<String>();
        final ArrayList<String> listID = new ArrayList<>();
        final ArrayList<String> listValue = new ArrayList<>();


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading record ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listValue);

        listView.setAdapter(adapter);
        getJSON(url_getAllValues);

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

                    URL url = new URL(urlWebService+ ("?recID=" + recID));
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
        Log.e("return value", json);
        JSONArray jsonArray = new JSONArray(json);
        if (jsonArray !=null) {


            String[] values = new String[jsonArray.length()];
            valuesArr = new ArrayList<RecordValue>();
            double date = 0;
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                //date= convertTime();
                RecordValue recValue = new RecordValue(String.valueOf(obj.getInt("indexVal")), String.valueOf(obj.getInt("recId")), String.valueOf(obj.getInt("value")), obj.getString("time"));
                valuesArr.add(recValue);

                values[i] ="Index: "+String.valueOf(obj.getInt("indexVal"))+"\n" + "Value: " + obj.getInt("value") + "\n" + "Time: " + obj.getString("time");
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
            plotGraph();
            listView.setAdapter(arrayAdapter);
        }
        pDialog.dismiss();

    }

    private void plotGraph() {

        graphView.getGridLabelRenderer().setHumanRounding(true);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
        series = new LineGraphSeries<>(new DataPoint[0]);
        new DataPoint(0, 0);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return sdf.format(new Date((long) value));
                else
                    return super.formatLabel(value, isValueX);
            }
        });

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true); // enables horizontal scrolling
        graphView.getViewport().setScrollableY(true); // enables vertical scrolling
        graphView.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graphView.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        try {
            if(valuesArr!=null) {
                graphView.getViewport().setMinX(convertTime(valuesArr.get(0).getsTime()));
                graphView.getViewport().setMaxX(convertTime(valuesArr.get(valuesArr.size() - 1).getsTime()));
//                Log.i("TIME", String.valueOf(convertTime(valuesArr.get(0).getsTime())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        for(int i = 0;i<valuesArr.size();i++){
            DateFormat df2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            //DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date cDate = null;
            try {
                 cDate= df2.parse(valuesArr.get(i).getsTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Date cdate = df2.parse(String.valueOf(valuesArr.get(i).getTime()));
            series.appendData(new DataPoint(cDate.getTime(), Double.valueOf(valuesArr.get(i).getValue())),true,valuesArr.size());

        }
        graphView.addSeries(series);
    }


    public double convertTime(String dateStr) throws ParseException {
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy");
        //DateFormat osLocalizedDateFormat = new SimpleDateFormat("MMMM EEEE");
        //System.out.println(osLocalizedDateFormat.format(new Date()))
        Log.i("convert time", String.valueOf(dateFormat.parse(dateStr)));
        SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm:ss");
        long cdate = dateFormat.parse(dateStr).getTime();
        //long cdate = df2.parse(dateStr).getTime();
        Log.e("Convert ", ""+cdate);
        return cdate;
    }
}
