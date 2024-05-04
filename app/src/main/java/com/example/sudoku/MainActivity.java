package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout bt_home_ca_nhan = findViewById(R.id.bt_home_ca_nhan);
        bt_home_ca_nhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout bt_home_giai_tri = findViewById(R.id.bt_home_giai_tri);
        bt_home_giai_tri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Scr1Activity.class);
                startActivity(intent);
                finish();
            }
        });

        Button bt_home_tro_choi_moi = findViewById(R.id.bt_home_tro_choi_moi);
        bt_home_tro_choi_moi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // Lấy thời gian hiện tại
        Date currentDate = new Date();

        // Định dạng ngày tháng
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Tìm TextView trong layout
        TextView dateTextViewHN = findViewById(R.id.txt_home_tthn_today);

        // Cập nhật ngày tháng lên TextView
        dateTextViewHN.setText(formattedDate);

        LinearLayout bt_home_tiep_tuc = findViewById(R.id.bt_home_tiep_tuc);
        bt_home_tiep_tuc.setVisibility(View.INVISIBLE);

        TextView dateTextViewSK = findViewById(R.id.txt_home_su_kien_today);
        dateTextViewSK.setText(" 28 ngày");

    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        TextView txt_home_de = dialog.findViewById(R.id.txt_home_de);
        TextView txt_home_trung_binh = dialog.findViewById(R.id.txt_home_trung_binh);
        TextView txt_home_kho = dialog.findViewById(R.id.txt_home_kho);

        txt_home_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayActivityWithDifficulty("easy");
                dialog.dismiss();
            }
        });

        txt_home_trung_binh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayActivityWithDifficulty("medium");
                dialog.dismiss();
            }
        });

        txt_home_kho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayActivityWithDifficulty("hard");
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void startPlayActivityWithDifficulty(String difficulty) {
        Intent intent = new Intent(MainActivity.this, PlayActivity.class);
        intent.putExtra("DIFFICULTY", difficulty);
        startActivity(intent);
        finish();
    }
}