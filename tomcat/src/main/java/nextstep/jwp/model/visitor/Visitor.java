package nextstep.jwp.model.visitor;

import nextstep.jwp.model.user.User;
import org.apache.catalina.session.SessionManager;

public abstract class Visitor {

    protected final SessionManager sessionManager = SessionManager.connect();
    protected final String sessionId;

    protected Visitor() {
        this.sessionId = sessionManager.createSession();
    }

    protected Visitor(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public abstract void maintainLogin(User user);

    public abstract boolean isLogin();

    public boolean isNewVisitor() {
        return this instanceof NewVisitor;
    }
}
