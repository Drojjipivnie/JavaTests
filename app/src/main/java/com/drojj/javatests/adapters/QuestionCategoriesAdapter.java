package com.drojj.javatests.adapters;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionCategoriesAdapter extends RecyclerView.Adapter<QuestionCategoriesAdapter.CategoryViewHolder> {

    private ArrayList<Category> mList;

    private CategoryClickListener mListener;

    public QuestionCategoriesAdapter(ArrayList<Category> list, CategoryClickListener listener) {
        mList = list;
        mListener = listener;
    }

    public interface CategoryClickListener {
        void onCategoryClicked(CategoryViewHolder holder, int position);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.questions_category_item, parent, false);

        return new CategoryViewHolder(cell);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        Category category = mList.get(position);

        holder.image.setImageResource(category.getImageResId());
        holder.title.setText(category.getTitle());
        holder.description.setText(category.getDescription());

        ViewCompat.setTransitionName(holder.image, String.valueOf(position) + "_image");

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCategoryClicked(holder, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        View v;

        @BindView(R.id.category_image)
        public ImageView image;

        @BindView(R.id.category_title)
        TextView title;

        @BindView(R.id.category_description)
        TextView description;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
