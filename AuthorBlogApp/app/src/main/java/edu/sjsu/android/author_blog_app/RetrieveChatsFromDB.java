package edu.sjsu.android.author_blog_app;

public class RetrieveChatsFromDB {
    public boolean seen;
    public long timeStamp;

    public RetrieveChatsFromDB(){

    }

    public RetrieveChatsFromDB(boolean seen, long timeStamp) {
        this.seen = seen;
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
