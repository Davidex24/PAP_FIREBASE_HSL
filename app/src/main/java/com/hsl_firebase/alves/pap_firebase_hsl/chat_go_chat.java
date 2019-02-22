package com.hsl_firebase.alves.pap_firebase_hsl;

public class chat_go_chat {

    public boolean seen;
    public long timestamp;

    public chat_go_chat(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public chat_go_chat(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

}
