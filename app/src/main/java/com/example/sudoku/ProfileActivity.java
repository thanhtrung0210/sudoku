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
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity{

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        LinearLayout bt_pf_home = findViewById(R.id.bt_pf_home);
        bt_pf_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout bt_pf_giai_tri = findViewById(R.id.bt_pf_giai_tri);
        bt_pf_giai_tri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, Scr1Activity.class);
                startActivity(intent);
                finish();
            }
        });

        hideAchievement();

        TextView bt_pf_binh_thuong = findViewById(R.id.bt_pf_binh_thuong);
        bt_pf_binh_thuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_pf_binh_thuong.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_pf_binh_thuong.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("medium");
            }
        });

        TextView bt_pf_de = findViewById(R.id.bt_pf_de);
        bt_pf_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_pf_de.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_pf_de.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("easy");
            }
        });

        TextView bt_pf_kho = findViewById(R.id.bt_pf_kho);
        bt_pf_kho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                bt_pf_kho.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.sudoku));
                bt_pf_kho.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                showAchievement();
                updateProfile("hard");
            }
        });

        TextView bt_pf_delete = findViewById(R.id.bt_pf_delete);
        bt_pf_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAll();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateHighScore();

        updateSumTime();
    }

    private void updateHighScore() {

        int hightScore = dbHelper.getHighScore();

        TextView txt_pf_highscore = findViewById(R.id.txt_pf_highscore);
        txt_pf_highscore.setText(" " + hightScore);
    }

    private void updateSumTime() {
        long totalTimeMillis = dbHelper.getTotalTime();

        // Chuyển đổi tổng thời gian từ mili giây sang giờ và phút
        long totalMinutes = totalTimeMillis / (60 * 1000);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        // Hiển thị giờ và phút lên TextView
        String formattedTime = "Thời gian đã chơi: " + hours + " giờ " + minutes + " phút";

        TextView txt_pf_sum_time = findViewById(R.id.txt_pf_sum_time);
        txt_pf_sum_time.setText(formattedTime);
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
        TextView txt_pf_da_hoan_thanh = findViewById(R.id.txt_pf_da_hoan_thanh);
        txt_pf_da_hoan_thanh.setText("" + completedLevels);

        TextView txt_pf_thang_hoan_hao = findViewById(R.id.txt_pf_thang_hoan_hao);
        txt_pf_thang_hoan_hao.setText("" + perfectWin);

        TextView txt_pf_win_streak = findViewById(R.id.txt_pf_win_streak);
        txt_pf_win_streak.setText("" + bestWinstreak);

        TextView txt_pf_thoi_gian_tot_nhat = findViewById(R.id.txt_pf_thoi_gian_tot_nhat);
        txt_pf_thoi_gian_tot_nhat.setText("" + bestTime);

    }

    private void hideAchievement() {

        TextView txt_pf_da_hoan_thanh = findViewById(R.id.txt_pf_da_hoan_thanh);
        txt_pf_da_hoan_thanh.setVisibility(View.INVISIBLE);

        TextView txt_pf_thang_hoan_hao = findViewById(R.id.txt_pf_thang_hoan_hao);
        txt_pf_thang_hoan_hao.setVisibility(View.INVISIBLE);

        TextView txt_pf_win_streak = findViewById(R.id.txt_pf_win_streak);
        txt_pf_win_streak.setVisibility(View.INVISIBLE);

        TextView txt_pf_thoi_gian_tot_nhat = findViewById(R.id.txt_pf_thoi_gian_tot_nhat);
        txt_pf_thoi_gian_tot_nhat.setVisibility(View.INVISIBLE);
    }

    private void showAchievement() {

        TextView txt_pf_da_hoan_thanh = findViewById(R.id.txt_pf_da_hoan_thanh);
        txt_pf_da_hoan_thanh.setVisibility(View.VISIBLE);

        TextView txt_pf_thang_hoan_hao = findViewById(R.id.txt_pf_thang_hoan_hao);
        txt_pf_thang_hoan_hao.setVisibility(View.VISIBLE);

        TextView txt_pf_win_streak = findViewById(R.id.txt_pf_win_streak);
        txt_pf_win_streak.setVisibility(View.VISIBLE);

        TextView txt_pf_thoi_gian_tot_nhat = findViewById(R.id.txt_pf_thoi_gian_tot_nhat);
        txt_pf_thoi_gian_tot_nhat.setVisibility(View.VISIBLE);
    }

    private void deleteSelect() {
        TextView bt_pf_de = findViewById(R.id.bt_pf_de);
        bt_pf_de.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_pf_de.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));

        TextView bt_pf_binh_thuong = findViewById(R.id.bt_pf_binh_thuong);
        bt_pf_binh_thuong.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_pf_binh_thuong.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));

        TextView bt_pf_kho = findViewById(R.id.bt_pf_kho);
        bt_pf_kho.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
        bt_pf_kho.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
    }

}
