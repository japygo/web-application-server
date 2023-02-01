package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Logger log = LoggerFactory.getLogger(HttpSessions.class);

    public static final String SESSION_ID = "JSESSIONID";
    private static final Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession session = sessions.get(id);

        if (session == null) {
            session = new HttpSession();
            sessions.put(session.getId(), session);
        }
        return session;
    }

    public static void removeSession(String id) {
        sessions.remove(id);
    }
}
