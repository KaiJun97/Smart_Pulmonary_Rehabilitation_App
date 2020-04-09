package com.example.rehabilitation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rehabilitation.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText,passwordEditText;
    private Button btnLogin,btnSignup;
    private ProgressBar loadingProgressBar;
    private static final int PERMISSION_INTERNET = 1;
    private static final int PERMISSION_ACCESS_NETWORK_STATE = 2;
    private ProgressDialog pDialog;
    private static final String url_login = MainActivity.ipBaseAddress+"/LoginJ.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        this.usernameEditText = findViewById(R.id.username);
        this.passwordEditText = findViewById(R.id.password);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnSignup= findViewById(R.id.btnSignup);
        this.loadingProgressBar = findViewById(R.id.loading);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw= passwordEditText.getText().toString();
                String uName= usernameEditText.getText().toString();
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Signing in");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                if(pw.isEmpty())
                {
                    pDialog.dismiss();
                    passwordEditText.setError( getResources().getString(R.string.error_field_required));

                }else

                if(uName.isEmpty())
                {
                    pDialog.dismiss();
                    usernameEditText.setError(getString(R.string.error_field_required));

                }else
                {
                    //JSONObject dataJson = new JSONObject();
                    HashMap<String, String> params = new HashMap<String, String>();
//                    try{

                        params.put("username", uName);
                        params.put("password", pw);
//                        dataJson.put("username", uName);
//                        dataJson.put("password", pw);


//                    }catch(JSONException e){
//
//                    }
                    JSONObject dataJson = new JSONObject(params);
                    Log.i("JSON DATA",dataJson.toString());
                    postData(url_login,dataJson,1 );

                }

            }
        });
    }

    public void postData(String url, JSONObject json, final int option){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                switch (option){
                    case 1:checkResponseLogin(response); break;

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", "Error");
                error.printStackTrace();
//                String alert_message;
//                alert_message = error.toString();
//                showAlertDialogue("Error", alert_message);
            }

        });
        requestQueue.add(json_obj_req);
    }


    public void checkResponseLogin(JSONObject response)
    {
        Log.i("----Response", response+" "+url_login);
        try {
            if(response.getInt(TAG_SUCCESS)==1){
                pDialog.dismiss();

                //finish();
                //new DataRequest().logInFunction(usernameEditText.getText().toString());
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("Username", usernameEditText.getText().toString());
                Toast.makeText(this, "Welcome "+usernameEditText.getText().toString(),Toast.LENGTH_SHORT).show();
                startActivity(i);



            }else{
                pDialog.dismiss();
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {


            e.printStackTrace();

        }

    }


}
