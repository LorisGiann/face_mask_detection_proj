package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Activity_setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.fatherSetting).setOnTouchListener(new OnSwipeTouchListener(Activity_setting.this) {
            @Override
            public void onSwipeRight() {
                finish();
            }
        });
    }

}