package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.exception.ExceptionListener;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;

public abstract class AbstractController implements Controller {

    private final List<String> paths;
    private final ExceptionListener exceptionListener;

    protected AbstractController(List<String> paths, ExceptionListener exceptionListener) {
        this.paths = paths;
        this.exceptionListener = exceptionListener;
    }

    protected AbstractController(String path,ExceptionListener exceptionListener) {
        this(List.of(path), exceptionListener);
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
        exceptionListener.handle(exception, response);
    }

    @Override
    public List<String> getMappedPaths() {
        return paths;
    }
}
