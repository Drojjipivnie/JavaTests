package com.drojj.javatests.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionCategoriesAdapter extends ArrayAdapter<Category> {

    private LayoutInflater mInflater;

    public QuestionCategoriesAdapter(Context context, List<Category> list) {
        super(context, 0, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);
        View view = convertView;

        CategoryHolderItem viewHolderItem = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.questions_category_item, null);
            viewHolderItem = new CategoryHolderItem(view);
            view.setTag(viewHolderItem);
        } else {
            viewHolderItem = (CategoryHolderItem) convertView.getTag();
        }
        //TODO:Fragment transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolderItem.image.setTransitionName("transition_image" + position);
        }
        viewHolderItem.image.setImageResource(category.getImageResId());
        viewHolderItem.title.setText(category.getTitle());
        viewHolderItem.description.setText(category.getDescription());

        return view;
    }

    static class CategoryHolderItem {
        @BindView(R.id.category_image)
        ImageView image;

        @BindView(R.id.category_description)
        TextView description;

        @BindView(R.id.category_title)
        TextView title;

        public CategoryHolderItem(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
