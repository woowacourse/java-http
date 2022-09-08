package org.apache.coyote.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public abstract class AbstractController implements Controller {

    abstract public boolean support(final String uri, final String httpMethods);

    protected boolean supportInternal(final String uriInput,
                                      final String httpMethodsInput,
                                      final AbstractController controller) {
        final String uri;
        try {
            uri = (String) controller.getClass().getDeclaredMethod("uri").invoke(controller);
            List<String> httpMethods = getHttpMethods(controller);
            return uri.equals(uriInput) && httpMethods.contains(httpMethodsInput);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getHttpMethods(final AbstractController controller) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .map(it -> it.getName())
                .filter(it -> it.startsWith("do"))
                .map(it -> it.replaceAll("do", ""))
                .map(it -> it.toUpperCase())
                .collect(Collectors.toList());
    }

    abstract public String uri();

    @Override
    abstract public void service(final HttpRequest request, final HttpResponse response);

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
