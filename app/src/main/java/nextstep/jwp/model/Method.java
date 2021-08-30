package nextstep.jwp.model;

import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.PageController;

import java.util.Optional;

public abstract class Method {
    protected final PageController pageController;
    protected final JwpController jwpController;

    public Method() {
        this.pageController = new PageController();
        this.jwpController = new JwpController();
    }

    protected String makeResponse(final HttpStatus status, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.toString(),
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public abstract String matchFunction(final String requestBody);
}
