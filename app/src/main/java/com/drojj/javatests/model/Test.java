package com.drojj.javatests.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Test implements Parcelable {

    public int id;

    public String name;

    public int question_count;

    public int progress;

    public long last_time_passed;

    public Test() {
    }

    protected Test(Parcel in) {
        id = in.readInt();
        name = in.readString();
        question_count = in.readInt();
        progress = in.readInt();
        last_time_passed = in.readLong();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(question_count);
        parcel.writeInt(progress);
        parcel.writeLong(last_time_passed);
    }
}
