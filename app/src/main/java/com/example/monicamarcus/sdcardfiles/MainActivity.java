package com.example.monicamarcus.sdcardfiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends Activity {
    TextView textView;
    Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        scanButton = (Button) findViewById(R.id.button);
        scanButton.setOnClickListener(l);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            File sdCardDir = new File(Environment.getExternalStorageDirectory().getPath());
            if (sdCardDir.isDirectory() && sdCardDir.listFiles() != null) {
                textView.setText(sdCardDir.getName());
                Intent intent = new Intent(getApplicationContext(), ListDirActivity.class);
                intent.putExtra("dirname", sdCardDir.toString());
                startActivity(intent);
            }
        }
    };
}
