package com.drojj.javatests.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.widget.TextView;

public class FlipAnimation {

    public static final int FROM_LEFT = -1;
    public static final int FROM_RIGHT = 1;

    private FlipAnimation() {
        throw new IllegalAccessError("Anim class");
    }

    public static void startRecyclerChildAnimation(final View view, int direction) {

        float from = view.getPivotX() + (direction) * 1000f;
        float to = view.getPivotX();

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.X, from, to);
        animator.setDuration(500);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    public static void startCardAnimation(View view){
        float from = view.getPivotX() - 1000f;
        float to = view.getX();

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.X, from, to);
        animator.setDuration(500);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    public static void animateText(final TextView question_counter, final String s) {
        AnimatorSet setFirst = new AnimatorSet();
        setFirst.setDuration(250).playTogether(
                ObjectAnimator.ofFloat(question_counter, View.SCALE_X, 1f, 1.2f),
                ObjectAnimator.ofFloat(question_counter, View.SCALE_Y, 1f, 1.2f)
        );
        setFirst.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                question_counter.setText(s);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet setSecond = new AnimatorSet();
        setSecond.setDuration(250).playTogether(
                ObjectAnimator.ofFloat(question_counter, View.SCALE_X, 1.2f, 1f),
                ObjectAnimator.ofFloat(question_counter, View.SCALE_Y, 1.2f, 1f)
        );



        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                setFirst,
                setSecond);
        set.start();

    }
}
