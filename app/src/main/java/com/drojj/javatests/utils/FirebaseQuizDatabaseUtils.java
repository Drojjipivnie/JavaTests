package com.drojj.javatests.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseQuizDatabaseUtils {

    public static void insertNewScore(int testId,int score) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid()).child("test_progress").child("test" + String.valueOf(testId)).setValue(score);
    }

    public static void updateLastTimeTestPassed(int testId,int rightAnswers){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid()).child("tests_last_time").child("test" + String.valueOf(testId)).setValue(System.currentTimeMillis());

        String key = reference.child("users").child(user.getUid()).child("tests_entrys").child("test" + String.valueOf(testId)).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/" + user.getUid() + "/tests_entrys/test" + String.valueOf(testId) + "/" + key + "/time", System.currentTimeMillis());
        childUpdates.put("users/" + user.getUid() + "/tests_entrys/test" + String.valueOf(testId) + "/" + key + "/score", rightAnswers);
        reference.updateChildren(childUpdates);
    }
}
