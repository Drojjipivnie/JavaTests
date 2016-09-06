package com.drojj.javatests.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseUtils {

    public static final String USERS = "users";

    public static final String TESTS_CATALOG = "tests_catalog";

    public static final String TEST = "test";

    public static final String TEST_LAST_TIME_PASSED = "last_time_passed";
    public static final String TEST_MAX_SCORE = "max_score";
    public static final String TEST_ENTRIES = "test_entries";

    public static final String ENTRY_SCORE = "score";
    public static final String ENTRY_TIME = "time";

    public static void insertNewScore(int testId, int score) {
        getTestMaxScoreReference(testId).setValue(score);
    }

    public static void updateLastTimeTestPassed(int testId, int rightAnswers) {
        long time = System.currentTimeMillis();

        getTestLastTimeReference(testId).setValue(time);

        String key = getTestEntriesReference(testId).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(TEST_ENTRIES + "/" + key + "/" + ENTRY_TIME, time);
        childUpdates.put(TEST_ENTRIES + "/" + key + "/" + ENTRY_SCORE, rightAnswers);
        getTestReferenceById(testId).updateChildren(childUpdates);
    }

    public static void clearUserTestHistory(int testId, OnCompleteListener<Void> completeListener, OnFailureListener failureListener, OnSuccessListener<Void> successListener) {
        DatabaseReference reference = getTestReferenceById(testId);

        reference.removeValue()
                .addOnCompleteListener(completeListener)
                .addOnFailureListener(failureListener)
                .addOnSuccessListener(successListener);
    }

    public static DatabaseReference getTestReferenceById(int testId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            //TODO:IF user not logged in
            throw new RuntimeException();
        }

        return FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS)
                .child(user.getUid())
                .child(TESTS_CATALOG)
                .child(TEST + String.valueOf(testId));
    }

    public static DatabaseReference getTestLastTimeReference(int testId) {
        return getTestReferenceById(testId).child(TEST_LAST_TIME_PASSED);
    }

    public static DatabaseReference getTestMaxScoreReference(int testId) {
        return getTestReferenceById(testId).child(TEST_MAX_SCORE);
    }

    public static DatabaseReference getTestEntriesReference(int testId) {
        return getTestReferenceById(testId).child(TEST_ENTRIES);
    }
}
