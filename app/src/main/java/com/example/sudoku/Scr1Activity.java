package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Scr1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr1);

        Button bt_thi1_bat_dau = findViewById(R.id.bt_thi1_bat_dau);
        TextView txt_thi1_ho_ten = findViewById(R.id.txt_thi1_ho_ten);

        String name;

        name = txt_thi1_ho_ten.getText().toString();

        txt_thi1_ho_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        bt_thi1_bat_dau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scr1Activity.this, Scr2Activity.class);
                intent.putExtra("NAME", name);
                startActivity(intent);
                finish();
            }
        });
    }
}