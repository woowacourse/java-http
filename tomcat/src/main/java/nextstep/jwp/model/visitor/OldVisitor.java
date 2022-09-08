package nextstep.jwp.model.visitor;

import nextstep.jwp.model.user.User;

public class OldVisitor extends Visitor {

    public OldVisitor(String sessionId) {
        super(sessionId);
    }

    @Override
    public void maintainLogin(User user) {
        sessionManager.addUserInSession(sessionId, user);
    }

    @Override
    public boolean isLogin() {
        return sessionManager.checkLogin(sessionId);
    }
}
