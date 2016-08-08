package com.drojj.javatests.utils.elementshelper;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.*;


public class QuestionElement {

    private final ElementType mType;

    private final String mData;

    public QuestionElement(ElementType type, String string) {
        mData = string;
        mType = type;
    }

    public View getView(Context ctx) {
        switch (mType) {
            case CODE:
                return createCodeView(ctx);
            case TEXT:
                return createTextView(ctx, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            default:
                return null;
        }
    }

    private TextView createTextView(Context ctx, LinearLayout.LayoutParams params) {
        TextView v = new TextView(ctx);
        v.setText(Html.fromHtml(mData));
        v.setLayoutParams(params);
        return v;
    }

    private HorizontalScrollView createCodeView(Context ctx) {
        TextView textView = createTextView(ctx, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        HorizontalScrollView codeView = new HorizontalScrollView(ctx);

        codeView.addView(textView);
        codeView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
        codeView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        codeView.setSmoothScrollingEnabled(true);
        codeView.setHorizontalScrollBarEnabled(false);

        return codeView;
    }
}