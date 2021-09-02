package nextstep.jwp.httpmessage;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    public static final DefaultHttpSession DEFAULT_HTTP_SESSION = new DefaultHttpSession();

    private String id;
    private final Map<String, Object> values = new HashMap<>();

    protected HttpSession() {
    }

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public boolean isDefaultHttpSession() {
        return false;
    }

    private static class DefaultHttpSession extends HttpSession {
        @Override
        public boolean isDefaultHttpSession() {
            return true;
        }
    }
}
