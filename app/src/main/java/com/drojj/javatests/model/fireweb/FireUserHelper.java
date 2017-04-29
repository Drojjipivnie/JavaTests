package com.drojj.javatests.model.fireweb;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireUserHelper {

    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private String mCurrentUid;

    public FireUserHelper(String uid) {
        mCurrentUid = uid;
    }

    public void createFireUser(FireUser user) {
        mDatabase.child("users").child(mCurrentUid).setValue(user);
    }
}
