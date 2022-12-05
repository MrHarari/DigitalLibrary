package model;

import java.io.Serializable;
import java.sql.Date;

public class Borrowed implements Serializable {
    public final Copy copy;
    public final User user;
    public final long timeBorrowed;

    public Borrowed(Copy copy, User user, long date) {
        this.copy = copy;
        this.user = user;
        this.timeBorrowed = date;
    }

    @Override
    public String toString() {
        return "Borrowed{" +
                "copy='" + copy + '\'' +
                ", user='" + user + '\'' +
                ", timeBorrowed=" + timeBorrowed +
                '}';
    }

    public int DaysPassed() {
        return (int) ((System.currentTimeMillis() - timeBorrowed) / (1000 * 60 * 60 * 24));
    }
}
