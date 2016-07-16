package com.drojj.javatests.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.Test;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestsAdapter  extends RecyclerView.Adapter<TestsAdapter.TestHolder>{

    private ArrayList<Test> items;
    private ItemClickListener listener;
    private OnStartEndLoad mListener;

    public TestsAdapter() {
        this.items = new ArrayList<>();
    }

    public interface ItemClickListener{
        void onClick(Test test);
    }

    public interface OnStartEndLoad{
        void onStart();
        void onFinish();
    }

    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tests_list_offline_card_item, parent, false);
        return new TestHolder(v);
    }

    @Override
    public void onBindViewHolder(final TestHolder holder, int position) {

        final Test test2 = items.get(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(test2);
            }
        });

        holder.testName.setText(test2.name);

        String progress = String.valueOf(test2.progress) + "/" + String.valueOf(test2.question_count);
        holder.testProgress.setText(progress);

        if(test2.last_time_passed == 0){
            holder.lastTime.setText(R.string.test_never_be_passed);
        }else{
            Date date = new Date(test2.last_time_passed);

            Locale locale = new Locale("ru", "RU");
            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            String lasttime = "Вы проходили этот тест " + df.format(date);
            holder.lastTime.setText(lasttime);
        }

        holder.progressBar.setMax(test2.question_count);
        holder.progressBar.setProgress(test2.progress);
        float percents = ((float) test2.progress) / ((float) test2.question_count);

        if (percents < 0.5f) {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#B71C1C"), PorterDuff.Mode.SRC_IN);
        } else if (percents >= 0.5f && percents < 0.75f) {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#F57F17"), PorterDuff.Mode.SRC_IN);
        } else {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#1B5E20"), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TestHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.testitem_name) TextView testName;

        @BindView(R.id.testitem_lasttime) TextView lastTime;

        @BindView(R.id.testitem_progress) TextView testProgress;

        @BindView(R.id.testitem_progressbar) ProgressBar progressBar;

        @BindView(R.id.testitem_cardview) CardView cardView;

        public TestHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnClick(ItemClickListener listener){
        this.listener = listener;
    }

    public void clear(){
        items.clear();
    }

    public void addAll(List<Test> list){
        items.addAll(list);
        list.clear();
        notifyDataSetChanged();
    }

    public void setOnFinishLoadListener(OnStartEndLoad listener){
        mListener = listener;
    }

    public void updateTestInfo(boolean isFirstTimeCalled){

        if(isFirstTimeCalled){
            mListener.onStart();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        DatabaseReference testProgressReference = reference.child("test_progress");
        DatabaseReference testsLastTimeReference = reference.child("tests_last_time");

        for(int i = 0; i< items.size();i++){

            final Test test = items.get(i);

            final int finalI = i;

            testProgressReference.child("test" + String.valueOf(test.id)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        test.progress = ((Long) dataSnapshot.getValue()).intValue();
                    } else {
                        test.progress = 0;
                    }
                    notifyDataSetChanged();

                    if(finalI ==items.size()-1){
                        mListener.onFinish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            testsLastTimeReference.child("test"+String.valueOf(test.id)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        test.last_time_passed = 0;
                    } else {
                        test.last_time_passed = (long) dataSnapshot.getValue();
                    }
                    notifyDataSetChanged();

                    if(finalI ==items.size()-1){
                        mListener.onFinish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
