package com.drojj.javatests.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.animations.FlipAnimation;
import com.drojj.javatests.model.question.Answer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.QuestionHolder> {

    private int mSelectedAnswer = -1;
    private List<Answer> mAnswers;
    private ArrayList<RadioButton> mButtons;

    public QuestionsRecyclerAdapter(List<Answer> answers) {
        this.mAnswers = answers;
        mButtons = new ArrayList<>();
    }

    @Override
    public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card_item, parent, false);
        return new QuestionHolder(v);
    }

    @Override
    public void onBindViewHolder(QuestionHolder holder, int position) {
        mButtons.add(holder.radioButton);
        holder.answer_text.setText(mAnswers.get(position).getAnswerText());
        if (position % 2 == 0) {
            holder.startAnimation(FlipAnimation.FROM_RIGHT);
        } else {
            holder.startAnimation(FlipAnimation.FROM_LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class QuestionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.answer_txt)
        TextView answer_text;

        @BindView(R.id.answer_radio)
        RadioButton radioButton;

        private View view;

        public QuestionHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            view = itemView;
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedAnswer != getAdapterPosition() && mSelectedAnswer != -1) {
                        mButtons.get(mSelectedAnswer).setChecked(false);
                    }

                    mSelectedAnswer = getAdapterPosition();
                    mButtons.get(mSelectedAnswer).setChecked(true);
                }
            };

            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);
        }

        public void startAnimation(int direction) {
            FlipAnimation.startRecyclerChildAnimation(view, direction);
        }
    }

    public int getChosenAnswer() {
        return mSelectedAnswer;
    }

    public boolean isAnyAnswerChosen() {
        return mSelectedAnswer != -1;
    }

    public boolean isAnswerRight() {
        return mAnswers.get(mSelectedAnswer).isAnswerRight();
    }
}
