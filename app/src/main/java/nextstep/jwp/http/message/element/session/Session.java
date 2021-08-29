package nextstep.jwp.http.message.element.session;

import java.util.Optional;

public interface Session {
    Optional<Object> getAttribute(String key);
    void setAttribute(String key, Object value);
    void removeAttribute(String key);
    String getSessionId();
}
