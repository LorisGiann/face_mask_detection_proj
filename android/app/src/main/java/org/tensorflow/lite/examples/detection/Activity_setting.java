package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_setting extends AppCompatActivity {
    private TextView title;
    private TextView withoutMaskCheckbox;
    private TextView withMaskCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        CheckDetect ck=CheckDetect.getInstance();
        ((CheckBox) findViewById(R.id.withMaskCheckbox)).setChecked(ck.getCheckMask());
        ((CheckBox) findViewById(R.id.withoutMaskCheckbox)).setChecked(ck.getCheckNoMask());


        title=findViewById(R.id.textView);
        withMaskCheckbox=findViewById(R.id.withMaskCheckbox);
        withoutMaskCheckbox=findViewById(R.id.withoutMaskCheckbox);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/BebasNeue-Regular.ttf");
        title.setTypeface(type);
        withMaskCheckbox.setTypeface(type);
        withoutMaskCheckbox.setTypeface(type);

        findViewById(R.id.fatherSetting).setOnTouchListener(new OnSwipeTouchListener(Activity_setting.this) {
            @Override
            public void onSwipeRight() {
                ck.setCheckMask(((CheckBox) findViewById(R.id.withMaskCheckbox)).isChecked());
                ck.setCheckNoMask(((CheckBox) findViewById(R.id.withoutMaskCheckbox)).isChecked());
                finish();
            }
        });



    }

}