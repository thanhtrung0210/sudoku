package com.example.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên và phiên bản cơ sở dữ liệu
    private static final String DATABASE_NAME = "game_data.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng lưu điểm cao
    private static final String TABLE_HIGH_SCORE = "high_scores";
    private static final String COLUMN_SCORE = "score";

    // Bảng lưu thành tích
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_COMPLETED_LEVELS = "completed_levels";
    public static final String COLUMN_PERFECT_WINS = "perfect_wins";
    public static final String COLUMN_BEST_WINSTREAK = "best_winstreak";
    public static final String COLUMN_BEST_TIME = "best_time";

    // Bảng lưu trữ winstreak cho mỗi độ khó
    private static final String TABLE_WINSTREAK = "winstreaks";
    private static final String COLUMN_DIFFICULTY_W = "difficulty_w";
    private static final String COLUMN_WINSTREAK = "winstreak";

    // Bảng lưu tổng thời gian chơi
    private static final String TABLE_TOTAL_TIME = "total_time";
    private static final String COLUMN_TIME = "time";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng điểm cao
        String createHighScoreTable = "CREATE TABLE " + TABLE_HIGH_SCORE + "("
                + COLUMN_SCORE + " INTEGER)";
        db.execSQL(createHighScoreTable);

        // Tạo bảng thành tích
        String createAchievementsTable = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
                + COLUMN_DIFFICULTY + " TEXT,"
                + COLUMN_COMPLETED_LEVELS + " INTEGER DEFAULT 0,"
                + COLUMN_PERFECT_WINS + " INTEGER DEFAULT 0,"
                + COLUMN_BEST_WINSTREAK + " INTEGER DEFAULT 0,"
                + COLUMN_BEST_TIME + " TEXT DEFAULT '--:--')";
        db.execSQL(createAchievementsTable);

        // Thêm dữ liệu mặc định của thành tích cho cả 3 độ khó
        addDefaultDataAchievements(db, "easy");
        addDefaultDataAchievements(db, "medium");
        addDefaultDataAchievements(db, "hard");

        // Tạo bảng winstreak
        String createWinstreakTable = "CREATE TABLE " + TABLE_WINSTREAK + "("
                + COLUMN_DIFFICULTY_W + " TEXT PRIMARY KEY,"
                + COLUMN_WINSTREAK + " INTEGER DEFAULT 0)";
        db.execSQL(createWinstreakTable);

        // Thêm dữ liệu mặc định cho bảng Winstreak
        addDefaultDataWinstreak(db, "easy");
        addDefaultDataWinstreak(db, "medium");
        addDefaultDataWinstreak(db, "hard");

        // Tạo bảng điểm cao
        String createTotalTimeTable = "CREATE TABLE " + TABLE_TOTAL_TIME + "("
                + COLUMN_TIME + " INTEGER DEFAULT 0)";
        db.execSQL(createTotalTimeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu nếu cần
    }

    // Lưu điểm cao
    public void saveHighScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);

        // Kiểm tra xem có dữ liệu nào trong bảng chưa
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HIGH_SCORE, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có dữ liệu, cập nhật nó
            db.update(TABLE_HIGH_SCORE, values, null, null);
            cursor.close();
        } else {
            // Nếu chưa có dữ liệu, thêm mới
            db.insert(TABLE_HIGH_SCORE, null, values);
        }

        db.close();
    }

    // Lấy điểm cao
    public int getHighScore() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_SCORE};

        Cursor cursor = db.query(
                TABLE_HIGH_SCORE,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int highScore = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_SCORE);
            if (columnIndex != -1) {
                highScore = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return highScore;
    }

    // Lưu thành tích
    public void saveAchievement(String difficulty, int completedLevels, int perfectWins,
                                int bestWinstreak, String bestTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_COMPLETED_LEVELS, completedLevels);
        values.put(COLUMN_PERFECT_WINS, perfectWins);
        values.put(COLUMN_BEST_WINSTREAK, bestWinstreak);
        values.put(COLUMN_BEST_TIME, bestTime);

        // Kiểm tra xem bản ghi đã tồn tại chưa
        boolean isRecordExists = checkIfRecordExists(db, TABLE_ACHIEVEMENTS, COLUMN_DIFFICULTY, difficulty);

        if (isRecordExists) {
            // Nếu bản ghi đã tồn tại, cập nhật nó
            db.update(TABLE_ACHIEVEMENTS, values, COLUMN_DIFFICULTY + "=?", new String[]{difficulty});
        } else {
            // Nếu bản ghi chưa tồn tại, thêm mới
            db.insert(TABLE_ACHIEVEMENTS, null, values);
        }

        db.close();
    }

    // Hàm kiểm tra xem một bản ghi có tồn tại trong bảng hay không
    private boolean checkIfRecordExists(SQLiteDatabase db, String tableName, String columnName, String value) {
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{value});

        boolean exists = (cursor.getCount() > 0);

        cursor.close();

        return exists;
    }


    // Lấy giá trị của một cột cụ thể từ bảng achievements
    public Object getAchievementValue(String columnName, String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {columnName};
        String selection = COLUMN_DIFFICULTY + " = ?";
        String[] selectionArgs = {difficulty};

        Cursor cursor = db.query(
                TABLE_ACHIEVEMENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Object value = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != -1) {
                if (COLUMN_BEST_TIME.equals(columnName)) {
                    // Nếu là cột BestTime, chuyển đổi chuỗi thành LocalTime
                    value = cursor.getString(columnIndex);
                } else {
                    // Ngược lại, lấy giá trị như bình thường
                    value = cursor.getInt(columnIndex);
                }
            }
            cursor.close();
        }

        return value;
    }

    // Thêm dữ liệu mặc định của thành tích cho một độ khó cụ thể
    private void addDefaultDataAchievements(SQLiteDatabase db, String difficulty) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_COMPLETED_LEVELS, 0);
        values.put(COLUMN_PERFECT_WINS, 0);
        values.put(COLUMN_BEST_WINSTREAK, 0);
        values.put(COLUMN_BEST_TIME, "--:--");
        db.insert(TABLE_ACHIEVEMENTS, null, values);
    }

    // Thêm dữ liệu mặc định của winstreak cho một độ khó cụ thể
    private void addDefaultDataWinstreak(SQLiteDatabase db, String difficulty) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIFFICULTY_W, difficulty);
        values.put(COLUMN_WINSTREAK, 0);
        db.insert(TABLE_WINSTREAK, null, values);
    }

    // Cập nhật winstreak
    public void updateWinstreak(String difficulty, int currentWinstreak) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DIFFICULTY_W, difficulty);
        values.put(COLUMN_WINSTREAK, currentWinstreak);

        // Chỉ cập nhật winstreak nếu khóa đã tồn tại
        db.insertWithOnConflict(TABLE_WINSTREAK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Lấy giá trị winstreak
    public int getWinstreak(String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_WINSTREAK};
        String selection = COLUMN_DIFFICULTY_W + "=?";
        String[] selectionArgs = {difficulty};

        Cursor cursor = db.query(
                TABLE_WINSTREAK,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int winstreak = 0;

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(COLUMN_WINSTREAK);
            if (columnIndex != -1 && cursor.moveToFirst()) {
                winstreak = cursor.getInt(columnIndex);
            }

            cursor.close();
        }

        return winstreak;
    }

    // Hàm cập nhật tổng thời gian chơi
    public void updateTotalTime(long timeInMillis) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Lấy giá trị hiện tại của tổng thời gian
        long currentTotalTime = getTotalTime();

        // Cộng thêm thời gian mới vào giá trị hiện tại
        long newTotalTime = currentTotalTime + timeInMillis;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, newTotalTime);

        // Kiểm tra xem có dữ liệu nào trong bảng chưa
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TOTAL_TIME, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có dữ liệu, cập nhật nó
            db.update(TABLE_TOTAL_TIME, values, null, null);
            cursor.close();
        } else {
            // Nếu chưa có dữ liệu, thêm mới
            db.insert(TABLE_TOTAL_TIME, null, values);
        }

        db.close();
    }



    // Hàm lấy tổng thời gian chơi
    public long getTotalTime() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_TIME};

        Cursor cursor = db.query(
                TABLE_TOTAL_TIME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        long totalTime = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_TIME);
            if (columnIndex != -1) {
                totalTime = cursor.getLong(columnIndex);
            }
            cursor.close();
        }

        return totalTime;
    }

    // Xóa tất cả dữ liệu
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Xóa tất cả dữ liệu
        db.delete(TABLE_HIGH_SCORE, null, null);
        db.delete(TABLE_WINSTREAK, null, null);
        db.delete(TABLE_ACHIEVEMENTS, null, null);

        // Thêm dữ liệu mặc định cho bảng thành tích sau khi xóa
        addDefaultDataAchievements(db, "easy");
        addDefaultDataAchievements(db, "medium");
        addDefaultDataAchievements(db, "hard");

        // Thêm dữ liệu mặc định cho bảng Winstreak sau khi xóa
        addDefaultDataWinstreak(db, "easy");
        addDefaultDataWinstreak(db, "medium");
        addDefaultDataWinstreak(db, "hard");

        db.close();
    }



}
