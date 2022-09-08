package org.apache.catalina.servlet;

import java.util.List;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.support.HttpMethod;

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
        if (request.isMethodOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethodOf(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException("API not implemented");
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    @Override
    public List<String> getMappedPaths() {
        return paths;
    }
}
