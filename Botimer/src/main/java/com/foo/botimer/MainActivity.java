package com.foo.botimer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.content.Intent;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        this.Init();
    }

    private void Init()
    {
        this.InitShowConverserButton();
    }

    private void InitShowConverserButton()
    {
        final Button Button = (Button) findViewById(R.id.Button_showConverser);
        Button.setOnClickListener(OnClick_showConverserButton);
    }


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    private View.OnClickListener OnClick_showConverserButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShowConverser();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////


    private void ShowConverser()
    {
        Intent Intent = new Intent(this, ConverserActivity.class);
        startActivity(Intent);
    }



}
