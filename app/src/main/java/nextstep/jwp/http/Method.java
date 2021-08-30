package nextstep.jwp.http;

import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.PageController;

public abstract class Method {
    protected final PageController pageController;
    protected final JwpController jwpController;

    public Method() {
        this.pageController = new PageController();
        this.jwpController = new JwpController();
    }

    protected String makeResponse(final HttpStatus status, final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.toString(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public abstract String matchFunction();
}
