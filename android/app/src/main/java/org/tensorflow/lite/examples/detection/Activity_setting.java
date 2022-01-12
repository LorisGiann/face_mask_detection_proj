package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_setting extends AppCompatActivity {
    private TextView title;
    private TextView withoutMaskCheckbox;
    private TextView withMaskCheckbox;
    //tracking
    private TextView modeTracking;
    private TextView lineTracking;
    private TextView objectTracking;

    private CheckBox checkBoxTrackingMode;
    private ImageView exitOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        CheckDetect ck=CheckDetect.getInstance();
        ((CheckBox) findViewById(R.id.withMaskCheckbox)).setChecked(ck.getCheckMask());
        ((CheckBox) findViewById(R.id.withoutMaskCheckbox)).setChecked(ck.getCheckNoMask());

        ((CheckBox) findViewById(R.id.modeTrackingCheckbox)).setChecked(ck.getCheckModeTracking());

        ((CheckBox) findViewById(R.id.visibilityLine)).setChecked(ck.getCheckLineTracking());
        ((CheckBox) findViewById(R.id.visibilityTracking)).setChecked(ck.getCheckObjectTracking());

        title=findViewById(R.id.textView);
        withMaskCheckbox=findViewById(R.id.withMaskCheckbox);
        withoutMaskCheckbox=findViewById(R.id.withoutMaskCheckbox);
        modeTracking=findViewById(R.id.modeTrackingCheckbox);
        lineTracking=findViewById(R.id.visibilityLine);
        objectTracking=findViewById(R.id.visibilityTracking);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/BebasNeue-Regular.ttf");
        title.setTypeface(type);
        withMaskCheckbox.setTypeface(type);
        withoutMaskCheckbox.setTypeface(type);
        modeTracking.setTypeface(type);
        lineTracking.setTypeface(type);
        objectTracking.setTypeface(type);

        exitOption = findViewById(R.id.option_exit_icon);
        exitOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ck.setCheckMask(((CheckBox) findViewById(R.id.withMaskCheckbox)).isChecked());
                ck.setCheckNoMask(((CheckBox) findViewById(R.id.withoutMaskCheckbox)).isChecked());
                ck.setCheckModeTracking(((CheckBox) findViewById(R.id.modeTrackingCheckbox)).isChecked());
                ck.setCheckLineTracking(((CheckBox) findViewById(R.id.visibilityLine)).isChecked());
                ck.setCheckObjectTracking(((CheckBox) findViewById(R.id.visibilityTracking)).isChecked());
                finish();
            }
        });

        checkBoxTrackingMode = (CheckBox)findViewById(R.id.modeTrackingCheckbox);
        if (checkBoxTrackingMode.isChecked()){
            lineTracking.setVisibility(View.VISIBLE);
            objectTracking.setVisibility(View.VISIBLE);
        }

        checkBoxTrackingMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lineTracking.setVisibility(View.VISIBLE);
                    objectTracking.setVisibility(View.VISIBLE);
                }else{
                    lineTracking.setVisibility(View.INVISIBLE);
                    objectTracking.setVisibility(View.INVISIBLE);
                }
            }
        });

        findViewById(R.id.fatherSetting).setOnTouchListener(new OnSwipeTouchListener(Activity_setting.this) {
            @Override
            public void onSwipeRight() {
                ck.setCheckMask(((CheckBox) findViewById(R.id.withMaskCheckbox)).isChecked());
                ck.setCheckNoMask(((CheckBox) findViewById(R.id.withoutMaskCheckbox)).isChecked());
                ck.setCheckModeTracking(((CheckBox) findViewById(R.id.modeTrackingCheckbox)).isChecked());
                ck.setCheckLineTracking(((CheckBox) findViewById(R.id.visibilityLine)).isChecked());
                ck.setCheckObjectTracking(((CheckBox) findViewById(R.id.visibilityTracking)).isChecked());
                finish();
            }
        });



    }

}