package com.fishinwater.android_version_update;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.fishinwater.android_version_update.version.HttpURLConnectionActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView mHttpUriClient, mOkHttp, mBmob, mDownLoadManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iniViews();
    }

    private void iniViews(){
        fab = findViewById(R.id.fab);
        mHttpUriClient = findViewById(R.id.http_client);
        mOkHttp = findViewById(R.id.ok_http);
        mBmob = findViewById(R.id.bmob);
        mDownLoadManager = findViewById(R.id.download_manager);

        fab.setOnClickListener(this);
        mHttpUriClient.setOnClickListener(this);
        mOkHttp.setOnClickListener(this);
        mBmob.setOnClickListener(this);
        mDownLoadManager.setOnClickListener(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.http_client:
                HttpURLConnectionActivity.anctionStart(MainActivity.this);
                break;

            case R.id.ok_http:

                break;

            case R.id.bmob:

                break;

            case R.id.download_manager:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;

            case R.id.fab:

                break;

            default:

                break;
        }
    }
}
