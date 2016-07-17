package com.drojj.javatests.model;

import android.content.Context;

import com.drojj.javatests.utils.ResourcesGetter;

public class Category {

    private int mId;

    private String mTitle;

    private String mDescription;

    private int mImageResId;

    public Category(int id, String mTitle, String mDescription, String imageRes, Context ctx) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mImageResId = ResourcesGetter.getDrawableId(ctx, imageRes);
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public int getId() {
        return mId;
    }
}
