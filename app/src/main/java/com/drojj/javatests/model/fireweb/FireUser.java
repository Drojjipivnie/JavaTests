package com.drojj.javatests.model.fireweb;

public class FireUser {

    public String uid;

    public String name;

    public String email;

    public long sign_up_time;

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

        public Builder setEmail(String email) {
            FireUser.this.email = email;
            return this;
        }

        public Builder setSignUpTime(long time) {
            FireUser.this.sign_up_time = time;
            return this;
        }

        public FireUser build() {
            return FireUser.this;
        }


    }

    public static FireUser createUser(String name, String email, String uid){
        return FireUser.newBuilder()
                .setName(name)
                .setEmail(email)
                .setSignUpTime(System.currentTimeMillis())
                .setUid(uid)
                .build();
    }

}
