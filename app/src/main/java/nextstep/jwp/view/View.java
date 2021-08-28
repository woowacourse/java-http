package nextstep.jwp.view;

import nextstep.jwp.handler.Model;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class View {

    private final String content;

    public View(String content) {
        this.content = content;
    }

    public static View of(String content) {
        return new View(content);
    }

    public HttpResponse render(Model model) {
        HttpStatus httpStatus = model.getHttpStatus();

        if (httpStatus.isOK()) {
            return HttpResponse.ok(content);
        }

        if (httpStatus.isFound()) {
            return HttpResponse.redirect(model.getLocation());
        }

        if (httpStatus.isUnauthorized()) {
            return HttpResponse.unauthorized(content);
        }

        throw new IllegalArgumentException();
    }
}
