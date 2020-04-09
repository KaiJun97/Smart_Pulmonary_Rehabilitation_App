package com.example.rehabilitation.Activity;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity {
    private Button btnBack, btnSignUp;
    private EditText etusername, etpassword, etpasswordConfirm,etFirstName, etLastName;
    SQLiteDatabase db;
    private static final String url_Signup = MainActivity.ipBaseAddress+"/SignUpJ.php";
    private static final String TAG_SUCCESS = "success";
    private  HashMap<String, String> params;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.btnBack = (Button) findViewById(R.id.btnBack);
        this.btnSignUp = (Button) findViewById(R.id.btnSignUp);
        this.etusername = (EditText) findViewById(R.id.etUsername);
        this.etpassword = (EditText) findViewById(R.id.etPassword);
        this.etpasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        this.etFirstName=(EditText) findViewById(R.id.etFirstName);
        this.etLastName=(EditText) findViewById(R.id.etLastName);



        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                String passwordConfirm = etpasswordConfirm.getText().toString();
                pDialog = new ProgressDialog(SignUpActivity.this);
                pDialog.setMessage("Signing Up...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                if (username.isEmpty()) {
                    etusername.setError(getResources().getString(R.string.error_field_required));
                } else {
                    if (password.isEmpty() || passwordConfirm.isEmpty())
                        etpassword.setError(getResources().getString(R.string.error_field_required));
                    else {
                        if (password.equals(passwordConfirm)) {

                            params = new HashMap<String, String>();
                                params.put("username", username);
                                params.put("password", password);
                                params.put("firstName", etFirstName.getText().toString());
                                params.put("lastName", etLastName.getText().toString());

                            JSONObject dataJson = new JSONObject(params);
                            postData(url_Signup, dataJson, 1);

                        } else {
                            etpasswordConfirm.setError(getResources().getString(R.string.error_field_mismatch));
                        }
                    }
                }
            }
        });

    }


    public void postData(String url,final JSONObject json, final int option){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("RESPONSE", String.valueOf(response.getInt("success")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (option) {
                    case 1:
                        checkResponseSignUp(response);
                        break;

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });
        requestQueue.add(json_obj_req);
    }
    public void checkResponseSignUp (JSONObject response)
    {
        Log.i("----Response", response + " " + url_Signup);
        try {
            if (response.getInt(TAG_SUCCESS) == 1) {
                pDialog.dismiss();
                finish();

            } else {
                pDialog.dismiss();
                Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}





