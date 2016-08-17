package com.drojj.javatests.utils.elementshelper;

import java.util.ArrayList;
import java.util.List;


public class QuestionElements {

    private List<QuestionElement> mElements;

    public QuestionElements(String[] strings) {
        mElements = new ArrayList<>(strings.length);
        parseStrings(strings);
    }

    private void parseStrings(String[] strings) {
        for (String string : strings) {
            if (string.startsWith("<bloc")) {           //Код
                mElements.add(new QuestionElement(ElementType.CODE, string));
            } else if (string.startsWith("<intimg>")) {
                mElements.add(new QuestionElement(ElementType.PICTURE, string.substring(8)));
            } else {
                mElements.add(new QuestionElement(ElementType.TEXT, string));
            }
        }
    }

    public List<QuestionElement> getElements() {
        return mElements;
    }
}
