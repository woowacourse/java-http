package com.techcourse.servlet;

import static org.apache.coyote.http11.HttpStatus.METHOD_NOT_SUPPORTED;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    protected abstract void doGet(HttpRequest req, HttpResponse resp);
    protected abstract void doPost(HttpRequest req, HttpResponse resp);
    protected abstract void doPut(HttpRequest req, HttpResponse resp);
    protected abstract void doDelete(HttpRequest req, HttpResponse resp);
    protected abstract void doPatch(HttpRequest req, HttpResponse resp);
    protected void sendMethodNotAllowed(HttpRequest req, HttpResponse resp) {
        resp.setResponse(METHOD_NOT_SUPPORTED, "Method '%s' is not Supported".formatted(req.getMethod()));
    }

    protected boolean validateHasBody(HttpRequest req, String... bodies) {
        Set<String> notExistBodies = Arrays.stream(bodies)
                .filter(body -> !req.hasBody(body))
                .collect(Collectors.toSet());

        if(notExistBodies.isEmpty()) {
            return true;
        }
        log.error("요청에 필요한 값이 존재하지 않습니다. {}", notExistBodies);
        return false;
    }
}
