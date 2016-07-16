package com.drojj.javatests.model.fireweb;

public class FireUser {

    public String uid;

    public String name;

    public FireUser() {
    }

    public static Builder newBuilder() {
        return new FireUser().new Builder();
    }

    public class Builder {

        public Builder setName(String name) {
            FireUser.this.name = name;

            return this;
        }

        public Builder setUid(String uid) {
            FireUser.this.uid = uid;
            return this;
        }

        public FireUser build(){
            return FireUser.this;
        }


    }

}
