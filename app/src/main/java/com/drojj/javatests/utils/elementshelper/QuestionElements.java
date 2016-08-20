package com.drojj.javatests.utils.elementshelper;

import android.content.Context;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;


public class QuestionElements {

    private final Typeface mRegularFont;

    private List<QuestionElement> mElements;

    public QuestionElements(Context ctx, String[] strings) {
        mElements = new ArrayList<>(strings.length);
        parseStrings(strings);
        mRegularFont = Typeface.createFromAsset(ctx.getAssets(), "fonts/NotoSans-Regular.ttf");
    }

    private void parseStrings(String[] strings) {
        for (String string : strings) {
            if (string.startsWith("<bloc")) {           //Код
                mElements.add(new QuestionElement(ElementType.CODE, string, this));
            } else if (string.startsWith("<intimg>")) {
                mElements.add(new QuestionElement(ElementType.PICTURE, string.substring(8), this));
            } else {
                mElements.add(new QuestionElement(ElementType.TEXT, string, this));
            }
        }
    }

    public List<QuestionElement> getElements() {
        return mElements;
    }

    public Typeface getRegularFont() {
        return mRegularFont;
    }
}
