package control.chat;

import model.ChatSession;
import model.DBUtil;
import model.Group;
import model.User;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class Chat {
    private static final ArrayList<ChatSession> userSessions = new ArrayList<>(); // List of all users connected to chat


    // Receives email, and password of a user and adds them to the list of the connected users
    @OnOpen
    public void onOpen(Session curSession) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        String query = curSession.getQueryString();
        String[] params = query.split("&");
        String email = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        User u = new User("", email, password, 0);
        if (DBUtil.UserExists(u)) {
            User temp = DBUtil.GetUser(email);
            ChatSession s = new ChatSession(new User(temp.username, email, password, temp.auth), curSession);
            userSessions.add(s);
        }
    }

    // Receives a request to close connection and removes them from the list of the connected users
    @OnClose
    public void onClose(Session curSession) {
        userSessions.removeIf(ses -> ses.session.equals(curSession));
    }

    // Receives a request type (either MSG or JOIN) and handles that request
    // MSG sends a message to all other users in the same group as the user
    // JOIN adds the user to a group
    @OnMessage
    public void onMessage(String message, Session userSession) throws SQLException, ClassNotFoundException {
        ChatSession invoker = null;
        String[] params = message.split(";");
        params = new String[]{params[0], String.join(";", Arrays.copyOfRange(params, 1, params.length))};

        for (ChatSession ses : userSessions) {
            if (ses.session == userSession)
                invoker = ses;
        }

        if (invoker == null) {
            onClose(userSession);
            return;
        }

        try {
        if (params[0].equalsIgnoreCase("MSG"))
            for (ChatSession ses : userSessions)
                if (!ses.session.equals(userSession))
                    if(ses.group.equals(invoker.group))
                        ses.session.getAsyncRemote().sendText(invoker.user.username + ";" + params[1]);
        } catch (NullPointerException ignored){}

        if (params[0].equalsIgnoreCase("JOIN"))
            invoker.group = DBUtil.GetGroup(params[1]);
    }
}