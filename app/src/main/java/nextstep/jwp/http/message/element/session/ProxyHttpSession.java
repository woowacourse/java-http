package nextstep.jwp.http.message.element.session;

import java.util.Objects;
import java.util.Optional;

public class ProxyHttpSession implements Session {

    private Session httpSession;
    private boolean isNew;

    public ProxyHttpSession() {
    }

    public ProxyHttpSession(Session httpSession) {
        this.httpSession = httpSession;
    }

    public boolean isNew() {
        return isNew;
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        createSessionIfNotExist();
        return httpSession.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        createSessionIfNotExist();
        httpSession.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        createSessionIfNotExist();
        httpSession.removeAttribute(key);
    }

    @Override
    public String getSessionId() {
        createSessionIfNotExist();
        return httpSession.getSessionId();
    }

    private void createSessionIfNotExist() {
        if (Objects.isNull(this.httpSession)) {
            this.httpSession = new HttpSession();
            isNew = true;
        }
    }
}
