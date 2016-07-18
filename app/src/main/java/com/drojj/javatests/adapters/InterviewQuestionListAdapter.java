package com.drojj.javatests.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.InterviewQuestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InterviewQuestionListAdapter extends ArrayAdapter<InterviewQuestion> {

    private LayoutInflater mInflater;

    public InterviewQuestionListAdapter(Context context, List<InterviewQuestion> list) {
        super(context, 0, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InterviewQuestion question = getItem(position);
        View view = convertView;

        InterviewQuestionHolderItem viewHolderItem = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.question_item, null);
            viewHolderItem = new InterviewQuestionHolderItem(view);
            view.setTag(viewHolderItem);
        } else {
            viewHolderItem = (InterviewQuestionHolderItem) convertView.getTag();
        }

        viewHolderItem.question.setText(question.getQuestion());
        viewHolderItem.number.setText(String.valueOf(position+1));

        return view;
    }

    static class InterviewQuestionHolderItem {
        @BindView(R.id.question_text)
        TextView question;

        @BindView(R.id.question_number)
        TextView number;

        public InterviewQuestionHolderItem(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
