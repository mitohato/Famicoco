package com.example.mito.famicoco;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TimePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PetSettingActivity extends AppCompatActivity {
    private int minutue;

    @BindView(R.id.button3)
    Button button;

    @OnClick(R.id.button3)
    void Click() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_setting);
        ButterKnife.bind(this);

        setTitle("ペットここ設定");

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                minutue = i1;
            }
        }, 0, 0, false);
    }
}
