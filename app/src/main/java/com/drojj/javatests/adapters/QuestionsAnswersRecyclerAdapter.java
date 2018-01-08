package com.drojj.javatests.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.question.Answer;
import com.drojj.javatests.utils.FormatUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionsAnswersRecyclerAdapter extends RecyclerView.Adapter<QuestionsAnswersRecyclerAdapter.AnswerHolder> {

    private List<Answer> mAnswers;
    private int mChosenAnswerPosition;
    private int[] statistics;

    public QuestionsAnswersRecyclerAdapter(List<Answer> answers, int chosenAnswer) {
        this.mAnswers = answers;
        this.mChosenAnswerPosition = chosenAnswer;
    }

    @Override
    public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_result_card_item, parent, false);
        return new AnswerHolder(v);
    }

    @Override
    public void onBindViewHolder(AnswerHolder holder, int position) {
        holder.answer_text.setText(mAnswers.get(position).getAnswerText());
        if (mChosenAnswerPosition == position) {
            holder.radioButton.setChecked(true);
        }
        if (mAnswers.get(position).isAnswerRight()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
        }
        if (statistics != null) {
            StringBuilder builder = new StringBuilder().append(statistics[mAnswers.get(position).getOrderCount() - 1]).append("/").append(FormatUtils.arrSum(statistics));
            holder.stats.setText(builder.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }

    public void showStatistics(int[] statistics) {
        this.statistics = statistics;
        notifyDataSetChanged();
    }

    public class AnswerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.result_answer_txt)
        TextView answer_text;

        @BindView(R.id.result_answer_radio)
        RadioButton radioButton;

        @BindView(R.id.result_image_right)
        ImageView imageView;

        @BindView(R.id.result_stats)
        TextView stats;

        public AnswerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
