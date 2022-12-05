package model;

import java.sql.Date;
import java.util.Objects;

public class User {
    public final String username;
    public final String email;
    public final String password;
    public final int auth;

    public User(String username, String email, String password, int auth) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }
}
