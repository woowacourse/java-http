package nextstep.jwp.http.method;

import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.PageController;
import nextstep.jwp.http.HttpResponse;

public abstract class Method {
    protected final PageController pageController;
    protected final JwpController jwpController;

    Method() {
        this.pageController = new PageController();
        this.jwpController = new JwpController();
    }

    public abstract HttpResponse matchFunction();
}
