package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
    private static final Logger log = LoggerFactory.getLogger(HttpSession.class);

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public HttpSession() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        HttpSessions.removeSession(id);
    }
}
