package model;

import javax.websocket.Session;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatSession {
    public final User user;
    public final Session session;
    public Group group;

    public ChatSession(User user, Session session) throws SQLException, ClassNotFoundException {
        this.user = user;
        this.session = session;
        this.group = null;
    }
}
