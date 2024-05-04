package com.example.sudoku;

import static com.example.sudoku.DatabaseHelper.COLUMN_BEST_TIME;
import static com.example.sudoku.DatabaseHelper.COLUMN_BEST_WINSTREAK;
import static com.example.sudoku.DatabaseHelper.COLUMN_COMPLETED_LEVELS;
import static com.example.sudoku.DatabaseHelper.COLUMN_PERFECT_WINS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Scr2Activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr2);

        dbHelper = new DatabaseHelper(this);

        String name = "";

        Intent intent = getIntent();
        if (intent.hasExtra("NAME")) {
            name = intent.getStringExtra("NAME");
        }

        hideAchievement();

        TextView txt_thi2_xin_chao = findViewById(R.id.txt_thi2_xin_chao);
        txt_thi2_xin_chao.setText("Xin chào " + name);

        updateHighScore();

        updateSumTime();

        TextView bt_thi2_binh_thuong = findViewById(R.id.bt_thi2_binh_thuong);
        bt_thi2_binh_thuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_thi2_binh_thuong.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_thi2_binh_thuong.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("medium");
            }
        });

        TextView bt_thi2_de = findViewById(R.id.bt_thi2_de);
        bt_thi2_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_thi2_de.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_thi2_de.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("easy");
            }
        });

        TextView bt_thi2_kho = findViewById(R.id.bt_thi2_kho);
        bt_thi2_kho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_thi2_kho.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_thi2_kho.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("hard");
            }
        });

    }

    private void updateHighScore() {

        int hightScore = dbHelper.getHighScore();

        TextView txt_thi2_diem_cao = findViewById(R.id.txt_thi2_diem_cao);
        txt_thi2_diem_cao.setText(" " + hightScore);
    }

    private void updateProfile(String difficulty) {
        // Lấy dữ liệu từ Database
        Object completedLevelsObj = dbHelper.getAchievementValue(COLUMN_COMPLETED_LEVELS, difficulty);
        Object perfectWinObj = dbHelper.getAchievementValue(COLUMN_PERFECT_WINS, difficulty);
        Object bestWinstreakObj = dbHelper.getAchievementValue(COLUMN_BEST_WINSTREAK, difficulty);
        Object bestTimeObj = dbHelper.getAchievementValue(COLUMN_BEST_TIME, difficulty);

        // Chuyển đổi giá trị sang kiểu phù hợp
        int completedLevels = completedLevelsObj instanceof Integer ? (int) completedLevelsObj : 0;
        int perfectWin = perfectWinObj instanceof Integer ? (int) perfectWinObj : 0;
        int bestWinstreak = bestWinstreakObj instanceof Integer ? (int) bestWinstreakObj : 0;
        String bestTime = bestTimeObj instanceof String ? (String) bestTimeObj : "--:--";

        // Hiển thị thông tin hồ sơ
        TextView txt_thi2_da_hoan_thanh = findViewById(R.id.txt_thi2_da_hoan_thanh);
        txt_thi2_da_hoan_thanh.setText("" + completedLevels);

        TextView txt_thi2_thang_hoan_hao = findViewById(R.id.txt_thi2_thang_hoan_hao);
        txt_thi2_thang_hoan_hao.setText("" + perfectWin);

        TextView txt_thi2_win_streak = findViewById(R.id.txt_thi2_win_streak);
        txt_thi2_win_streak.setText("" + bestWinstreak);

        TextView txt_thi2_thoi_gian_tot_nhat = findViewById(R.id.txt_thi2_thoi_gian_tot_nhat);
        txt_thi2_thoi_gian_tot_nhat.setText("" + bestTime);
    }

    private void deleteSelect() {
        TextView bt_thi2_de = findViewById(R.id.bt_thi2_de);
        bt_thi2_de.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_thi2_de.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));

        TextView bt_thi2_binh_thuong = findViewById(R.id.bt_thi2_binh_thuong);
        bt_thi2_binh_thuong.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_thi2_binh_thuong.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));

        TextView bt_thi2_kho = findViewById(R.id.bt_thi2_kho);
        bt_thi2_kho.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_thi2_kho.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
    }

    private void hideAchievement() {

        TextView txt_thi2_da_hoan_thanh = findViewById(R.id.txt_thi2_da_hoan_thanh);
        txt_thi2_da_hoan_thanh.setVisibility(View.INVISIBLE);

        TextView txt_thi2_thang_hoan_hao = findViewById(R.id.txt_thi2_thang_hoan_hao);
        txt_thi2_thang_hoan_hao.setVisibility(View.INVISIBLE);

        TextView txt_thi2_win_streak = findViewById(R.id.txt_thi2_win_streak);
        txt_thi2_win_streak.setVisibility(View.INVISIBLE);

        TextView txt_thi2_thoi_gian_da_choi = findViewById(R.id.txt_thi2_thoi_gian_da_choi);
        txt_thi2_thoi_gian_da_choi.setVisibility(View.INVISIBLE);
    }

    private void showAchievement() {

        TextView txt_thi2_da_hoan_thanh = findViewById(R.id.txt_thi2_da_hoan_thanh);
        txt_thi2_da_hoan_thanh.setVisibility(View.VISIBLE);

        TextView txt_thi2_thang_hoan_hao = findViewById(R.id.txt_thi2_thang_hoan_hao);
        txt_thi2_thang_hoan_hao.setVisibility(View.VISIBLE);

        TextView txt_thi2_win_streak = findViewById(R.id.txt_thi2_win_streak);
        txt_thi2_win_streak.setVisibility(View.VISIBLE);

        TextView txt_thi2_thoi_gian_tot_nhat = findViewById(R.id.txt_thi2_thoi_gian_tot_nhat);
        txt_thi2_thoi_gian_tot_nhat.setVisibility(View.VISIBLE);
    }

    private void updateSumTime() {
        long totalTimeMillis = dbHelper.getTotalTime();

        // Chuyển đổi tổng thời gian từ mili giây sang giờ và phút
        long totalMinutes = totalTimeMillis / (60 * 1000);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        // Hiển thị giờ và phút lên TextView
        String formattedTime = hours + " giờ " + minutes + " phút";

        TextView txt_thi2_thoi_gian_da_choi = findViewById(R.id.txt_thi2_thoi_gian_da_choi);
        txt_thi2_thoi_gian_da_choi.setText(formattedTime);
    }
}