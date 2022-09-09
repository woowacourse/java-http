package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.view.ExceptionPage;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;

public abstract class AbstractController implements Controller {

    private final List<String> paths;

    protected AbstractController(List<String> paths) {
        this.paths = paths;
    }

    protected AbstractController(String path) {
        this(List.of(path));
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            doService(request, response);
        } catch (HttpException exception) {
            handleException(exception, response);
        }
    }

    private void doService(HttpRequest request, HttpResponse response) {
        if (request.isMethodOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethodOf(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        methodNotFound(response);
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected void methodNotFound(HttpResponse response) {
        response.status(HttpStatus.NOT_FOUND).setViewResource("/404.html");
    }

    private void handleException(HttpException exception, HttpResponse response) {
        final var status = exception.getStatus();
        final var path = ExceptionPage.toUri(status);
        response.status(status).setViewResource(path);
    }

    @Override
    public List<String> getMappedPaths() {
        return paths;
    }
}
