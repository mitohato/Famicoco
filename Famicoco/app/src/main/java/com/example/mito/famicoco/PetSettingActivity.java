package com.example.mito.famicoco;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PetSettingActivity extends AppCompatActivity {

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

    }
}
