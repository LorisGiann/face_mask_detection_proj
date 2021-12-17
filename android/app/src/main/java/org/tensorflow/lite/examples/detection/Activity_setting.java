package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        CheckDetect ck=CheckDetect.getInstance();
        ((CheckBox) findViewById(R.id.IncorrectMaskCheckbox)).setChecked(ck.getCheckMaskIncorretly());
        ((CheckBox) findViewById(R.id.withMaskCheckbox)).setChecked(ck.getCheckMask());
        ((CheckBox) findViewById(R.id.withoutMaskCheckbox)).setChecked(ck.getCheckNoMask());


        findViewById(R.id.fatherSetting).setOnTouchListener(new OnSwipeTouchListener(Activity_setting.this) {
            @Override
            public void onSwipeRight() {
                ck.setCheckMask(((CheckBox) findViewById(R.id.withMaskCheckbox)).isChecked());
                ck.setCheckNoMask(((CheckBox) findViewById(R.id.withoutMaskCheckbox)).isChecked());
                ck.setCheckMaskIncorretly(((CheckBox) findViewById(R.id.IncorrectMaskCheckbox)).isChecked());
                ck.setCheckCont(true);
                finish();
            }
        });



    }

}