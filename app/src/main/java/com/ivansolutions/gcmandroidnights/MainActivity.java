package com.ivansolutions.gcmandroidnights;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ivansolutions.gcmandroidnights.adapter.MessageAdapter;
import com.ivansolutions.gcmandroidnights.data.DataStorage;
import com.ivansolutions.gcmandroidnights.receivers.GcmIntenService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String SENDER_ID = "416686843422";
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    private boolean makingRequest = false;
    private ProgressBar pbRegistering = null;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        final RecyclerView rvPrincipal = (RecyclerView) findViewById(R.id.rv_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        boolean registered = DataStorage.getBooleanPreference(this, "registered");

        if (!registered) {
            showRegisterDialog();
        }

        adapter = new MessageAdapter();

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = rvPrincipal.getChildAdapterPosition(v);
                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                i.putExtra("text", AppContext.getList().get(pos).getContenido());
                startActivity(i);
            }
        });

        rvPrincipal.setHasFixedSize(true);
        rvPrincipal.setAdapter(adapter);
        rvPrincipal.addItemDecoration(new SimpleDividerItemDecoration(this));
        rvPrincipal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmIntenService.activityToRefresh = this;
        refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GcmIntenService.activityToRefresh = null;
    }

    public void refreshUI() {
        adapter.notifyDataSetChanged();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.str_device_not_registered_title));
        View dialogView = View.inflate(this, R.layout.layout_register, null);
        final EditText pwdField = (EditText) dialogView.findViewById(R.id.ed_name_device);
        pbRegistering = (ProgressBar) dialogView.findViewById(R.id.pb_registering);
        builder.setView(dialogView);

        builder.setNegativeButton(getString(R.string.str_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.str_aceptar), null);

        final AlertDialog alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (makingRequest) return;

                makingRequest = true;
                new RegIdSender(alert).execute(pwdField.getText().toString());

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

    private class RegIdSender extends AsyncTask<String, Void, String> {

        AlertDialog dialog;

        public RegIdSender(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbRegistering.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... str_Params) {
            String result = "";
            String nombre = str_Params[0];

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);

                URL url = new URL("http://192.168.0.5/PHPGcmAndroidNights/registerDevice.php");

                byte[] postDataBytes = ("gcm_id=" + regid + "" + "&nombre=" + nombre).getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    result = output;
                }

                conn.disconnect();
            } catch (IOException ignored) {
            }
            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            makingRequest = false;
            pbRegistering.setVisibility(View.INVISIBLE);

            if (s.equals("true")) {
                DataStorage.savePreference(MainActivity.this, "registered", true);
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Hubo un error en el registro", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.recyclerview_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
