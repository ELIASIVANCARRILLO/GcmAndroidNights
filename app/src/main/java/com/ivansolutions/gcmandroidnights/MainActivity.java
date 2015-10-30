package com.ivansolutions.gcmandroidnights;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    //AIzaSyCmNFLZC-3OQPMEvwp1635glTm1Ifr1-is  ->  server key (para php)

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    static final String TAG = "GCMDemo";
    String SENDER_ID = "416686843422";
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    private boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        if (!registered) {
            showRegisterDialog();
        }


    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.str_device_not_registered_title));
        View dialogView = View.inflate(this, R.layout.layout_register, null);
        final EditText pwdField = (EditText) dialogView.findViewById(R.id.ed_name_device);
        builder.setView(dialogView);

        builder.setNegativeButton(getString(R.string.str_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.str_aceptar), null);

        AlertDialog alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, DetailActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private class RegIdSender extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... str_Params) {
            String result = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);

                URL url = new URL("<Url_php para registrar el dispositivo>");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream wr = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write("regid=" + regid);
                wr.flush();

                connection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    result = output;
                }

                connection.disconnect();
            } catch (IOException ignored) {
            }
            return result;
        }
    }
}
