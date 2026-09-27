package org.apache.catalina;

import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    private static final String ALLOW_HEADER = "Allow";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();
        if (!allowedMethods().contains(method)) {
            sendMethodNotAllowed(response);
            return;
        }
        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected abstract Set<HttpMethod> allowedMethods();

    private void sendMethodNotAllowed(final HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        final String allow = allowedMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(", "));
        response.addHeader(ALLOW_HEADER, allow);
    }
}
