package com.drojj.javatests.database;

import com.drojj.javatests.model.fireweb.FireUser;
import com.drojj.javatests.model.question.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseUtils {

    public static final String USERS = "users";

    public static final String TESTS_CATALOG = "tests_catalog";

    public static final String TEST = "test";

    public static final String TEST_LAST_TIME_PASSED = "last_time_passed";
    public static final String TEST_MAX_SCORE = "max_score";
    public static final String TEST_ENTRIES = "test_entries";

    public static final String SCORE = "score";
    public static final String TIME = "time";

    public static final String FEEDBACK = "feedback";
    public static final String FEEDBACK_THEME = "theme";
    public static final String FEEDBACK_MESSAGE = "message";

    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "user_email";

    public static final String QUESTION_STATISTICS = "question_statistics";
    public static final String NEQ_QUESTIONS = "new_questions";

    public static void insertNewScore(int testId, int score) {
        getTestMaxScoreReference(testId).setValue(score);
    }

    public static void updateLastTimeTestPassed(int testId, int rightAnswers) {
        long time = System.currentTimeMillis();

        getTestLastTimeReference(testId).setValue(time);

        String key = getTestEntriesReference(testId).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(TEST_ENTRIES + "/" + key + "/" + TIME, time);
        childUpdates.put(TEST_ENTRIES + "/" + key + "/" + SCORE, rightAnswers);
        getTestReferenceById(testId).updateChildren(childUpdates);
    }

    public static void clearUserTestHistory(int testId, OnFailureListener failureListener, OnSuccessListener<Void> successListener) {
        DatabaseReference reference = getTestReferenceById(testId);

        reference.removeValue()
                .addOnFailureListener(failureListener)
                .addOnSuccessListener(successListener);
    }

    public static DatabaseReference getTestReferenceById(int testId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
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

    public static void sendFeedback(String theme, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FEEDBACK);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String key = reference.push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FEEDBACK_THEME, theme);
        childUpdates.put(FEEDBACK_MESSAGE, message);
        childUpdates.put(USER_ID, user.getUid());
        childUpdates.put(USER_EMAIL, user.getEmail());
        childUpdates.put(TIME, System.currentTimeMillis());

        reference.child(key).updateChildren(childUpdates);
    }

    public static void sendNewQuestion(Question question, String comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(NEQ_QUESTIONS);

        String key = reference.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("test_id", question.getId());
        childUpdates.put("question_text", question.getQuestionText());
        childUpdates.put("code", question.getCode());
        childUpdates.put("answers", question.getAnswers());
        childUpdates.put("comment", comment);

        reference.child(key).updateChildren(childUpdates);
    }

    public static DatabaseReference getQuestionStatisticsReference(int testId, int questionId) {
        return FirebaseDatabase.getInstance().getReference()
                .child(QUESTION_STATISTICS)
                .child(TEST + String.valueOf(testId))
                .child(String.valueOf(questionId));
    }

    public static void updateQuestionAnswersStatistics(int testId, List<Question> list) {
        DatabaseReference referenceStatistics = FirebaseDatabase.getInstance().getReference()
                .child(QUESTION_STATISTICS)
                .child(TEST + String.valueOf(testId));

        for (Question question : list) {
            final DatabaseReference answerReference = referenceStatistics.child(String.valueOf(question.getId())).child(String.valueOf(question.getChosenRealIndex()));
            answerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        answerReference.setValue(1);
                    } else {
                        long currentValue = (long) dataSnapshot.getValue();
                        answerReference.setValue(currentValue + 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public static void createFireUser(final FireUser user) {
        FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(user.uid)
                .setValue(user);
                /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUserInfo(user, callback);
                        } else {
                            callback.onCreationFailed(task.getException());
                        }
                    }
                });*/
    }

    public static void updateUserInfo(FireUser user) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.name)
                .build();

        FirebaseAuth.getInstance()
                .getCurrentUser()
                .updateProfile(changeRequest);
                /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onCreationFinished();
                        } else {
                            callback.onCreationFailed(task.getException());
                        }
                    }
                });*/
    }

    public static Task<Void> deleteUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.delete();
        return FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(currentUser.getUid())
                .removeValue();
    }
}
