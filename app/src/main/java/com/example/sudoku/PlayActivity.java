package com.example.sudoku;

import static com.example.sudoku.DatabaseHelper.COLUMN_BEST_TIME;
import static com.example.sudoku.DatabaseHelper.COLUMN_BEST_WINSTREAK;
import static com.example.sudoku.DatabaseHelper.COLUMN_COMPLETED_LEVELS;
import static com.example.sudoku.DatabaseHelper.COLUMN_PERFECT_WINS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity{

    private int diemSo = 0, soLoi = 0;
    private long thoiGianTruoc = 0;
    private long thoiGianVaoGame = 0;
    private long thoiGianHienTai = 0;
    private long thoiGianChoi = 0;
    private long tongThoiGian = 0;
    private Handler handler;
    private TextView txt_play_thoi_gian;

    private String txt_selected_id;
    private int selectedNumber;

    private final int SIZE = 9;
    private final Random random = new Random();

    private String option;

    private int[] countOccurrences = new int[9];

    int [][] grid = new int[9][9];;

    private GridLayout sudoku;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        dbHelper = new DatabaseHelper(this);

        ImageView bt_play_back = findViewById(R.id.bt_play_back);
        bt_play_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("DIFFICULTY")) {
            option = intent.getStringExtra("DIFFICULTY");
        }

        //Lấy lưới Sudoku
        sudoku = findViewById(R.id.sudoku);

        // Lặp qua từng ô Sudoku và gán sự kiện onClick
        for (int i = 0; i < sudoku.getChildCount(); i++) {
            GridLayout gridLayout = (GridLayout) sudoku.getChildAt(i);

            for (int j = 0; j <gridLayout.getChildCount(); j++) {
                final TextView textView = (TextView) gridLayout.getChildAt(j);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Lấy ID của TextView được click
                        String textViewId = getResources().getResourceName(textView.getId());

                        clearHighlight();

                        // Trích xuất thông tin vị trí từ ID
                        String[] parts = textViewId.split("_");
                        if (parts.length == 4) {
                            int hang = Integer.parseInt(parts[2]);
                            int cot = Integer.parseInt(parts[3]);

                            // Highlight các ô ở cùng hàng, cùng cột, và cùng vùng 3x3
                            highlightHang(hang);
                            highlightCot(cot);
                            highlightVung3x3(hang, cot);
                        }

                        textView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1_highlight);

                        String value = textView.getText().toString();
                        if(!value.isEmpty()) {
                            highlightSameValueCells(value);
                            txt_selected_id = textViewId;
                        } else {
                            txt_selected_id = textViewId;
                        }
                    }
                });
            }
        }

        TextView txt_play_do_kho = findViewById(R.id.txt_play_do_kho);
        if ("easy".equals(option))
            txt_play_do_kho.setText("Dễ");
        else if ("medium".equals(option))
            txt_play_do_kho.setText("Trung bình");
        else
            txt_play_do_kho.setText("Khó");

        txt_play_thoi_gian = findViewById(R.id.txt_play_thoi_gian);

        //Bắt đầu đếm thời gian vào game
        thoiGianVaoGame = System.currentTimeMillis();

        //Tạo 1 handler để cập nhật thời gian liên tục
        handler = new Handler();
        //Chạy 1 Runnable để cập nhật thời gian
        handler.post(updateThoiGian);

        //Cập nhật thời gian theo định dạng "--:--"
        capNhatThoiGianChoi(txt_play_thoi_gian);

        //Sinh dữ liệu ban đầu
        sudokuGenerator();

        //Điền số vào ô trống
        setupCardViewClickListeners();

        setupPauseButton();

        setupDeleteButton();

        setupSuggestButton();
    }

    private void capNhatDiemSo() {

        long thoiGianHienTai = System.currentTimeMillis();

        long thoiGianGiuaHaiLanDien;

        int diemSoMacDinh;
        if ("easy".equals(option))
            diemSoMacDinh = 50;
        else if ("medium".equals(option))
            diemSoMacDinh = 60;
        else
            diemSoMacDinh = 70;

        if(thoiGianTruoc >= 0) {
            if(thoiGianTruoc == 0)
                thoiGianGiuaHaiLanDien = thoiGianHienTai - thoiGianVaoGame;
            else
                thoiGianGiuaHaiLanDien = thoiGianHienTai - thoiGianTruoc;

            int thoiGianCombo = (int) (thoiGianGiuaHaiLanDien / 1000); // Chuyển đổi thành giây

            if(thoiGianCombo < 5) {
                diemSo += diemSoMacDinh + 30;
            } else if (thoiGianCombo < 10) {
                diemSo += diemSoMacDinh + 25;
            } else if (thoiGianCombo < 15) {
                diemSo += diemSoMacDinh + 20;
            } else if (thoiGianCombo < 20) {
                diemSo += diemSoMacDinh + 15;
            } else if (thoiGianCombo < 25) {
                diemSo += diemSoMacDinh + 10;
            } else if (thoiGianCombo < 30) {
                diemSo += diemSoMacDinh + 5;
            } else {
                diemSo += diemSoMacDinh;
            }
        }

        // Lưu thời gian lần điền hiện tại để sử dụng cho lần điền tiếp theo
        thoiGianTruoc = thoiGianHienTai;

        TextView txt_play_diem_so = findViewById(R.id.txt_play_diem_so);
        txt_play_diem_so.setText("Điểm số: " + diemSo);

        // Kiểm tra xem trò chơi đã hoàn thành hay chưa
        if (isGameCompleted()) {
            // Nếu trò chơi đã hoàn thành, hiển thị thông báo và kết thúc trò chơi
            ketThucTroChoiThang();
        }
    }

    private void capNhatLoi() {
        soLoi++;

        TextView txt_play_loi = findViewById(R.id.txt_play_loi);
        txt_play_loi.setText("Lỗi: " + soLoi + "/3");

        if(soLoi >= 3) {
            ketThucTroChoiThua();
        }
    }

    private void ketThucTroChoiThua() {

        String thoiGianDaChoi = txt_play_thoi_gian.getText().toString();

        int hightScore = dbHelper.getHighScore();

        if(diemSo > hightScore) {
            hightScore = diemSo;
            dbHelper.saveHighScore(hightScore);
        }

        // Hiển thị Dialog thông báo kết thúc trò chơi với hai lựa chọn
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thất bại");
        builder.setMessage("Bạn đã thua do mắc quá nhiều lỗi!\n\n"
                + "Điểm số của bạn: " + diemSo + "\n"
                + "Điểm cao: " + hightScore + "\n"
                + "Thời gian đã chơi: " + thoiGianDaChoi);
        builder.setPositiveButton("Chơi lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });
        builder.setNegativeButton("Quay về màn hình chính", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToMainMenu();
            }
        });

        // Reset bộ đếm Winstreak
        dbHelper.updateWinstreak(option, 0);

        // Dừng việc cập nhật thời gian khi trò chơi kết thúc
        handler.removeCallbacks(updateThoiGian);

        // Cập nhật tổng thời gian đã chơi
        dbHelper.updateTotalTime(tongThoiGian);

        // Ngăn chặn việc đóng Dialog khi chạm ra ngoài
        builder.setCancelable(false);

        builder.show();
    }

    private void restartGame() {
        Intent intent = getIntent();
        finish(); // Kết thúc Activity hiện tại
        startActivity(intent); // Khởi động lại Activity
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(PlayActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void capNhatThoiGianChoi(TextView textView) {
        thoiGianHienTai = System.currentTimeMillis();
        tongThoiGian = thoiGianChoi + (System.currentTimeMillis() - thoiGianVaoGame);

        // Chuyển đổi thời gian chơi thành định dạng "--:--"
        int giay = (int) (tongThoiGian / 1000);
        int phut = giay / 60;
        giay %= 60;
        String thoiGian = String.format("%02d:%02d", phut, giay);

        // Cập nhật thời gian chơi trên giao diện
        textView.setText("" + thoiGian);
    }


    //Runnable để cập nhật thời gian liên tục
    private Runnable updateThoiGian = new Runnable() {
        @Override
        public void run() {
            capNhatThoiGianChoi(txt_play_thoi_gian);
            handler.postDelayed(this, 1000); //Cập nhật mỗi 1 giây
        }
    };

    private void clearHighlight() {
        for (int i = 0; i < sudoku.getChildCount(); i++) {
            GridLayout gridLayout = (GridLayout) sudoku.getChildAt(i);

            for (int j = 0; j < gridLayout.getChildCount(); j++) {
                TextView textView = (TextView) gridLayout.getChildAt(j);
                textView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1);
            }
        }
    }

    private void highlightHang(int hang) {
        for (int i = 0; i < 9; i++) {
            String textViewId = "txt_play_" + hang + "_" + i;
            int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
            TextView rowTextView = findViewById(textViewResourceId);
            rowTextView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1_highlight_gray); // Đặt background màu highlight
        }
    }

    private void highlightCot(int cot) {
        for (int i = 0; i < 9; i++) {
            String textViewId = "txt_play_" + i + "_" + cot;
            int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
            TextView colTextView = findViewById(textViewResourceId);
            colTextView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1_highlight_gray); // Đặt background màu highlight
        }
    }

    private void highlightVung3x3(int hang, int cot) {
        int vung3x3HangBatDau = (hang / 3) * 3;
        int vung3x3CotBatDau = (cot / 3) * 3;

        for (int i = vung3x3HangBatDau; i < vung3x3HangBatDau + 3; i++) {
            for (int j = vung3x3CotBatDau; j < vung3x3CotBatDau + 3; j++) {
                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView vung3x3TextView = findViewById(textViewResourceId);
                vung3x3TextView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1_highlight_gray); // Đặt background màu highlight
            }
        }
    }

    private void highlightSameValueCells(String value) {
        for (int i = 0; i < sudoku.getChildCount(); i++) {
            GridLayout gridLayout = (GridLayout) sudoku.getChildAt(i);

            for (int j = 0; j < gridLayout.getChildCount(); j++) {
                TextView textView = (TextView) gridLayout.getChildAt(j);
                if (textView.getText().toString().equals(value)) {
                    textView.setBackgroundResource(R.drawable.ds_khung_sudoku_1x1_highlight);
                }
            }
        }
    }

    private void sudokuGenerator() {

        final int easyMin = 36;
        final int easyMax = 49;

        final int mediumMin = 32;
        final int mediumMax = 35;

        final int hardMin = 22;
        final int hardMax = 27;

        //Tạo một lưới Sudoku hoàn chỉnh
        solveSudoku();

        int noOfCellsToBeGenerated = 0; //Số ô cho sẵn dữ liệu

        //Set số ô cho sẵn
        if ("easy".equals(option)) {
            noOfCellsToBeGenerated = random.nextInt((easyMax - easyMin) + 1) + easyMin;
        } else if ("medium".equals(option)) {
            noOfCellsToBeGenerated = random.nextInt((mediumMax - mediumMin) + 1) + mediumMin;
        } else {
            noOfCellsToBeGenerated = random.nextInt((hardMax - hardMin) + 1) + hardMin;
        }

        //Hiển thị ngẫu nhiên 1 số ô ra giao diện
        for( int i = 0; i < noOfCellsToBeGenerated; i++) {

            String textViewId;
            int textViewResourceId;
            TextView textView;

            int row, col;

            do {
                row = random.nextInt(SIZE);
                col = random.nextInt(SIZE);

                textViewId = "txt_play_" + row + "_" + col;
                textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                textView = findViewById(textViewResourceId);
            } while (!"0".equals(textView.getText().toString()));

            textView.setText(""+grid[row][col]);
            textView.setTag(true);
        }

        //Hiển thị các ô trống
        for(int i=0; i<SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);
                if ("0".equals(textView.getText().toString()))
                    textView.setText("");
            }
        }

        capNhatDemSoConThieu();

    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Kiểm tra xem số num có hợp lệ ở hàng, cột và ô 3x3 hay không
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num || board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean solveSudoku() {
        // Lặp qua từng ô Sudoku
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Nếu ô chưa được điền
                if (grid[i][j] == 0) {
                    // Duyệt ngẫu nhiên các số từ 1 đến 9
                    for (int num = 1; num <= SIZE; num++) {
                        //Gán 1 giá trị ngẫu nhiên
                        int randomNum = random.nextInt(SIZE) + 1;
                        if (isValid(grid, i, j, randomNum)) {
                            //Nếu không vi phạm quy tắc thì điền giá trị và tiếp tục giải
                            grid[i][j] = randomNum;

//                            printSudokuGrid();

                            if (solveSudoku()) {
                                return true; // Nếu giải thành công, trả về true
                            }
                            // Nếu không giải được với giá trị hiện tại, quay lui và thử giá trị khác
                            grid[i][j] = 0;
                        }
                    }
                    return false; // Nếu không có giá trị nào thích hợp, quay lui
                }
            }
        }
        return true; // Nếu đã điền đầy đủ các ô, trả về true
    }

//    private void printSudokuGrid() {
//        System.out.println("Sudoku Grid:");
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                System.out.print(grid[i][j] + " ");
//            }
//            System.out.println();
//        }
//    }

    private void capNhatDemSoConThieu() {

        // Đặt lại số lần xuất hiện của mỗi số về 0
        Arrays.fill(countOccurrences, 0);

        int num = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);

                // Nếu rỗng, gán bằng 0, nếu có giá trị, gán giá trị tương ứng
                if(!textView.getText().toString().isEmpty())
                    num = Integer.parseInt(textView.getText().toString());
                else
                    num = 0;

                //Nếu có giá trị, đếm thêm tại vị trí tương ứng của số đó trong mảng
                if(num != 0) {
                    countOccurrences[num - 1]++;
                }
            }
        }

        //Cập nhật lên giao diện
        for (int i = 1; i <= 9; i++) {
            int textViewId = getResources().getIdentifier("txt_play_" + i, "id", getPackageName());
            TextView textView = findViewById(textViewId);
            int soConThieu = SIZE - countOccurrences[i - 1];
            textView.setText(String.valueOf(soConThieu));

            String cardViewId = "bt_play_" + i;
            int cardViewResourceId = getResources().getIdentifier(cardViewId, "id", getPackageName());
            CardView cardView = findViewById(cardViewResourceId);

            if (soConThieu == 0) {
                cardView.setVisibility(View.INVISIBLE);
            } else {
                cardView.setVisibility(View.VISIBLE);
            }
        }
    }

    // Gắn OnClickListener cho mỗi CardView
    private void setupCardViewClickListeners() {
        for (int i = 1; i <= 9; i++) {
            String btnId = "bt_play_" + i;
            int btnResourceId = getResources().getIdentifier(btnId, "id", getPackageName());
            CardView numberCardView = findViewById(btnResourceId);

            // Lưu giá trị số tương ứng với CardView được nhấn
            final int selectedValue = i;
            numberCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gán giá trị số đã chọn
                    selectedNumber = selectedValue;

                    if (txt_selected_id != null && !txt_selected_id.isEmpty()) {
                        // Truy xuất TextView thông qua ID
                        TextView selectedTextView = findViewById(getResources().getIdentifier(txt_selected_id, "id", getPackageName()));

                        // Kiểm tra xem có được phép điền không
                        if (selectedTextView.getTag() == null || selectedTextView.getTag() == "false") {
                            // Điền số vào ô trống
                            selectedTextView.setText(String.valueOf(selectedNumber));

                            if (isValidMove(selectedTextView)) { //Nếu đúng
                                selectedTextView.setTextColor(getResources().getColor(R.color.sudoku));
                                if(selectedTextView.getTag() == null)
                                    capNhatDiemSo();
                                selectedTextView.setTag("true");
                            } else { //Nếu sai
                                selectedTextView.setTextColor(getResources().getColor(R.color.red));
                                capNhatLoi();
                            }
                            clearHighlight();
                            highlightSameValueCells(""+selectedNumber);
                            capNhatDemSoConThieu();
                        }
                    }
                }
            });
        }
    }

    //Kiểm tra giá trị vừa điền
    private boolean isValidMove(TextView textView) {
        int row, col, value;
        String textViewId = getResources().getResourceName(textView.getId());
        String[] parts = textViewId.split("_");
        if (parts.length == 4) {
            row = Integer.parseInt(parts[2]);
            col = Integer.parseInt(parts[3]);
            value = Integer.parseInt(textView.getText().toString());

            // Kiểm tra xem giá trị đã điền có đúng không
            return value == grid[row][col];
        }
        return false;
    }

    private boolean isGameCompleted() {
        // Kiểm tra xem tất cả các ô có giá trị hay không
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);
                if (textView.getText().toString().isEmpty()) {
                    return false; // Nếu có ô trống, trò chơi chưa hoàn thành
                }
            }
        }
        return true; // Nếu không có ô trống, trò chơi đã hoàn thành
    }

    private void ketThucTroChoiThang() {

        String thoiGianDaChoi = txt_play_thoi_gian.getText().toString();

        int hightScore = dbHelper.getHighScore();

        if(diemSo > hightScore) {
            hightScore = diemSo;
            dbHelper.saveHighScore(hightScore);
        }

        // Hiển thị Dialog thông báo kết thúc trò chơi với hai lựa chọn
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chiến thắng");
        builder.setMessage("Chúc mừng bạn đã hoàn thành màn chơi!\n\n"
                + "Điểm số của bạn: " + diemSo + "\n"
                + "Điểm cao: " + hightScore + "\n"
                + "Thời gian đã chơi: " + thoiGianDaChoi);
        builder.setPositiveButton("Chơi lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });
        builder.setNegativeButton("Quay về màn hình chính", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToMainMenu();
            }
        });

        //Cập nhật dữ liệu khi thắng
        updateAchievementsOnWin(option);

        // Dừng việc cập nhật thời gian khi trò chơi kết thúc
        handler.removeCallbacks(updateThoiGian);

        // Ngăn chặn việc đóng Dialog khi chạm ra ngoài
        builder.setCancelable(false);

        builder.show();
    }

    // Hàm để cập nhật thành tích khi thắng
    public void updateAchievementsOnWin(String difficulty) {
        // Lấy dữ liệu từ Database
        Object completedLevelsObj = dbHelper.getAchievementValue(COLUMN_COMPLETED_LEVELS, difficulty);
        Object perfectWinObj = dbHelper.getAchievementValue(COLUMN_PERFECT_WINS, difficulty);
        Object bestWinstreakObj = dbHelper.getAchievementValue(COLUMN_BEST_WINSTREAK, difficulty);
        Object bestTimeObj = dbHelper.getAchievementValue(COLUMN_BEST_TIME, difficulty);

        // Lấy giá trị hiện tại của winstreak từ Database
        int currentWinstreak = dbHelper.getWinstreak(option);

        // Chuyển đổi giá trị sang kiểu phù hợp
        int completedLevels = completedLevelsObj instanceof Integer ? (int) completedLevelsObj : 0;
        int perfectWin = perfectWinObj instanceof Integer ? (int) perfectWinObj : 0;
        int bestWinstreak = bestWinstreakObj instanceof Integer ? (int) bestWinstreakObj : 0;
        String bestTime = bestTimeObj instanceof String ? (String) bestTimeObj : "--:--";

        // Tăng số lượng màn đã hoàn thành
        completedLevels++;

        // Tăng số lượng màn hoàn hảo nếu không có lỗi
        if (soLoi == 0) {
            perfectWin++;
        }

        // Tăng Winstreak hiện tại
        currentWinstreak++;

        // Cập nhật bestWinstreak nếu như đạt điều kiện
        if(currentWinstreak > bestWinstreak) {
            bestWinstreak = currentWinstreak;
        }

        // Kiểm tra và cập nhật best time
        if ("--:--".equals(bestTime) || tongThoiGian < chuyenDoiThoiGian(bestTime)) {
            // Cập nhật bestTime với thời gian mới
            int giay = (int) (tongThoiGian / 1000);
            int phut = giay / 60;
            giay %= 60;
            bestTime = String.format("%02d:%02d", phut, giay);
        }

        // Cập nhật vào cơ sở dữ liệu
        dbHelper.saveAchievement(difficulty, completedLevels, perfectWin, bestWinstreak, bestTime);

        // Cập nhật tổng thời gian đã chơi
        dbHelper.updateTotalTime(tongThoiGian);
    }

    // Hàm chuyển đổi chuỗi thời gian thành milliseconds
    private long chuyenDoiThoiGian(String timeString) {
        try {
            String[] parts = timeString.split(":");
            int phut = Integer.parseInt(parts[0]);
            int giay = Integer.parseInt(parts[1]);

            // Chuyển đổi thời gian thành miligiây
            return (phut * 60 + giay) * 1000L;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showPauseDialog() {

        int hightScore = dbHelper.getHighScore();

        // Lấy thông tin về màn chơi (thời gian, điểm số, độ khó)
        String thoiGianDaChoi = txt_play_thoi_gian.getText().toString();

        String doKho;
        if ("easy".equals(option))
            doKho = "Dễ";
        else if ("medium".equals(option))
            doKho = "Trung bình";
        else
            doKho = "Khó";

        // Hiển thị Dialog tạm dừng
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạm Dừng");
        builder.setMessage("\n" +
                "Thời gian: " + thoiGianDaChoi + "\n" +
                "Điểm số: " + diemSo + "\n" +
                "Điểm cao: " + hightScore + "\n" +
                "Độ khó: " + doKho);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng Dialog và tiếp tục màn chơi
                resumeGame();
            }
        });
        builder.setNegativeButton("Chơi lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng Dialog và khởi động lại màn chơi
                restartGame();
            }
        });

        // Ngăn chặn việc đóng Dialog khi chạm ra ngoài
        builder.setCancelable(false);

        // Hiển thị Dialog
        builder.show();
    }

    private void resumeGame() {
        //Set lại thời gian bắt đầu
        thoiGianVaoGame = System.currentTimeMillis();

        // Hiển thị số khi tiếp tục
        showNumbers();

        // Gọi phương thức tiếp tục cập nhật thời gian
        handler.post(updateThoiGian);
    }

    private void setupPauseButton() {
        ImageView pauseButton = findViewById(R.id.bt_play_pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lưu thời gian chơi hiện tại
                thoiGianChoi += (System.currentTimeMillis() - thoiGianVaoGame);

                // Tạm dừng cập nhật thời gian
                handler.removeCallbacks(updateThoiGian);

                // Ẩn dữ liệu
                hideNumbers();

                // Hiển thị Dialog tạm dừng
                showPauseDialog();
            }
        });
    }

    private void hideNumbers() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showNumbers() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String textViewId = "txt_play_" + i + "_" + j;
                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupDeleteButton () {
        int btnResourceId = getResources().getIdentifier("bt_play_delete", "id", getPackageName());
        LinearLayout btnDelete = findViewById(btnResourceId);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_selected_id != null && !txt_selected_id.isEmpty()) {
                    // Truy xuất TextView thông qua ID
                    TextView selectedTextView = findViewById(getResources().getIdentifier(txt_selected_id, "id", getPackageName()));

                    // Kiểm tra xem có được phép xóa không
                    if (selectedTextView.getTag() == null || selectedTextView.getTag() == "false") {
                        selectedTextView.setText("");
                        selectedTextView.setTag("false");
                        clearHighlight();
                        capNhatDemSoConThieu();
                    }
                }
            }
        });
    }

    private void setupSuggestButton () {
        int btnResourceId = getResources().getIdentifier("bt_play_suggest", "id", getPackageName());
        LinearLayout btnSuggest = findViewById(btnResourceId);
        btnSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_selected_id != null && !txt_selected_id.isEmpty()) {
                    // Truy xuất TextView thông qua ID
                    TextView selectedTextView = findViewById(getResources().getIdentifier(txt_selected_id, "id", getPackageName()));

                    // Kiểm tra xem có giá trị hay không và chưa điền dữ liệu
                    if (selectedTextView.getText().toString().isEmpty() && (selectedTextView.getTag() == null || selectedTextView.getTag() == "false")) {
                        // Lấy vị trí của ô trên lưới Sudoku
                        String[] parts = txt_selected_id.split("_");
                        if (parts.length == 4) {
                            int row = Integer.parseInt(parts[2]);
                            int col = Integer.parseInt(parts[3]);

                            // Hiển thị đáp án trong ô và đặt tag để biết ô đã được gợi ý
                            selectedTextView.setText(String.valueOf(grid[row][col]));
                            selectedTextView.setTag("hint");
                            selectedTextView.setTextColor(getResources().getColor(R.color.sudoku));

                            clearHighlight();
                            capNhatDemSoConThieu();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Dừng việc cập nhật thời gian khi Activity kết thúc
        handler.removeCallbacks(updateThoiGian);

        // Đóng cơ sở dữ liệu khi Activity kết thúc
        dbHelper.close();
    }

}
