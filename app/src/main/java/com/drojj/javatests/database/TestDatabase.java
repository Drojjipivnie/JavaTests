package com.drojj.javatests.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.drojj.javatests.BuildConfig;
import com.drojj.javatests.model.Category;
import com.drojj.javatests.model.InterviewQuestion;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.question.Answer;
import com.drojj.javatests.model.question.Question;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestDatabase extends SQLiteOpenHelper {

    public static final int VERSION = BuildConfig.VERSION_CODE;

    private static final String DATABASE_NAME = "TestsDataBase.db";

    private static TestDatabase mInstance = null;

    private SQLiteDatabase mDataBase;

    private Context mCtx;

    public interface DataBaseInitCallback {
        void onSuccess();

        void onError(IOException e);
    }

    public static TestDatabase getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new TestDatabase(ctx);
        }
        return mInstance;
    }

    private TestDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mCtx = context;
    }

    private static String getDatabasePath(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return context.getApplicationInfo().dataDir + "/databases";
        } else {
            return "/data/data/" + context.getPackageName() + "/databases";
        }
    }

    public static void copyDataBase(Context context, DataBaseInitCallback callback) {

        InputStream input = null;
        OutputStream output = null;

        try {
            input = context.getAssets().open(DATABASE_NAME);

            File databaseDirectory = new File(getDatabasePath(context));
            if (!databaseDirectory.exists()) {
                databaseDirectory.mkdir();
            }

            output = new FileOutputStream(getDatabasePath(context) + "/" + DATABASE_NAME);

            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = input.read(mBuffer)) > 0) {
                output.write(mBuffer, 0, mLength);
            }
            callback.onSuccess();
        } catch (IOException e) {
            Log.d("DaraBaseError", "Error copying database");
            callback.onError(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                Log.d("DataBaseError", "Error closing input/output streams");
                callback.onError(e);
            }
        }
    }

    public void open() {
        mDataBase = mInstance.getWritableDatabase();
    }

    @Override
    public void close() {
        if (mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    public List<Question> getQuestions(int testId, int questionsCount) {

        open();

        String sql = "SELECT * FROM Questions WHERE test_id = " + String.valueOf(testId) + " ORDER BY RANDOM() LIMIT " + String.valueOf(questionsCount);

        Cursor cursor = mDataBase.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
        } else {
            return Collections.emptyList();
        }

        List<Question> items = new ArrayList<>();
        do {
            items.add(addQuestion(cursor, testId));
        } while (cursor.moveToNext());

        cursor.close();

        close();
        return items;
    }

    private Question addQuestion(Cursor cursor, int testId) {
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        int answer_right = cursor.getInt(cursor.getColumnIndex("answer_right"));
        ArrayList<Answer> answers = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            String answer = cursor.getString(cursor.getColumnIndex("answer_" + i));
            if (answer != null) {
                answers.add(new Answer(i, answer));
            }
        }

        answers.get(answer_right - 1).setThisAnswerRight();
        Collections.shuffle(answers, new Random(System.nanoTime()));

        String question_text = cursor.getString(cursor.getColumnIndex("questions_text"));
        String explanation = cursor.getString(cursor.getColumnIndex("explanation"));
        String code = cursor.getString(cursor.getColumnIndex("code"));

        return new Question(id, question_text, answers, code, explanation, testId);
    }

    public List<Test> getTests() {

        open();

        String sql = "SELECT * FROM Tests";

        Cursor cursor = mDataBase.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
        } else {
            return Collections.emptyList();
        }

        List<Test> items = new ArrayList<>();
        do {
            Test test = new Test();
            test.id = cursor.getInt(cursor.getColumnIndex("_id"));
            test.name = cursor.getString(cursor.getColumnIndex("name"));
            test.question_count = cursor.getInt(cursor.getColumnIndex("question_count"));

            items.add(test);
        } while (cursor.moveToNext());

        cursor.close();
        close();
        return items;
    }

    public List<Category> getQuestionCategories() {
        open();

        String sql = "SELECT * FROM Categories";
        Cursor cursor = mDataBase.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
        } else {
            return Collections.emptyList();
        }

        List<Category> items = new ArrayList<>();
        do {

            String image = cursor.getString(cursor.getColumnIndex("image_string"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            Category item = new Category(id, title, description, image, mCtx);
            items.add(item);
        } while (cursor.moveToNext());

        cursor.close();
        close();

        return items;
    }

    public List<InterviewQuestion> getInterviewQuestions(int categoryId) {
        open();

        String sql = "SELECT * FROM InterviewQuestions WHERE category_id = " + String.valueOf(categoryId);
        Cursor cursor = mDataBase.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
        } else {
            return Collections.emptyList();
        }

        List<InterviewQuestion> items = new ArrayList<>();
        do {

            String question = cursor.getString(cursor.getColumnIndex("question"));
            String answer = cursor.getString(cursor.getColumnIndex("answer"));
            items.add(new InterviewQuestion(question, answer));
        } while (cursor.moveToNext());

        cursor.close();

        close();

        return items;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
