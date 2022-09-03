package nextstep.jwp.controller;

import nextstep.jwp.servlet.ViewResolver;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpException;

public class ErrorController {

    private final ViewResolver viewResolver;

    public ErrorController(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public HttpResponse handle(HttpException exception) {
        return viewResolver.findErrorPage(exception);
    }
}
