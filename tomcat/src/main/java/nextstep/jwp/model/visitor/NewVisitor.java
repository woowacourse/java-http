package nextstep.jwp.model.visitor;

import nextstep.jwp.model.user.User;

public class NewVisitor extends Visitor {

    public NewVisitor() {
        super();
    }

    @Override
    public void maintainLogin(User user) {
        return;
    }

    @Override
    public boolean isLogin() {
        return false;
    }
}
