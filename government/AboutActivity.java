package com.wisaterhunep.government;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "ABOUT";

    private TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.d(TAG, "onCreate: " + TAG);

        //redirecting textview to a link when the user clicks on the application name in the middle of the page
        appName = (TextView) findViewById(R.id.AppName);
        appName.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
