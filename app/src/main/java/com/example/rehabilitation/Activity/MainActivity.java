 package com.example.rehabilitation.Activity;

 import android.content.Intent;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.TextView;

 import androidx.appcompat.app.AppCompatActivity;

 import com.example.rehabilitation.Data.User;
 import com.example.rehabilitation.R;

 public class MainActivity extends AppCompatActivity {
    //public static String ipBaseAddress = "http://192.168.1.11/asp";
    public static String ipBaseAddress = "http://mprehab.atspace.cc";
     private TextView txtName;
     private Button btnPlay, btnShowData, btnExportData;
    public static String username;
    public static int uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtName= (TextView) findViewById(R.id.txtName);
        this.btnPlay=(Button)findViewById(R.id.btnPlay);
        this.btnShowData=(Button) findViewById(R.id.btnShowData);
        this.btnExportData=(Button) findViewById(R.id.btnExport);
        Intent intent= getIntent();
        this.username= intent.getStringExtra("Username");
        this.uId=intent.getIntExtra("uId",0);
        final User user= new User(this.username,this.uId);
        this.txtName.post(new Runnable() {
            public void run() {
                txtName.setText(user.getUsername());
            }
        });

        this.btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent recordActivity = new Intent(MainActivity.this, AllRecordsActivity.class);
                Intent recordActivity = new Intent(MainActivity.this, AllRecordsActivity.class);
                //recordActivity.putExtra("username",intent.getStringExtra("username"));
                startActivity(recordActivity);

            }
        });

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent play = new Intent(MainActivity.this, PlayActivity.class);
                //play.putExtra("username",intent.getStringExtra("username"));
                startActivity(play);

            }
        });

        this.btnExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordActivity = new Intent(MainActivity.this, ExportDataActivity.class);
                //recordActivity.putExtra("username",intent.getStringExtra("username"));
                startActivity(recordActivity);
            }
        });
    }

}
