package com.techcourse.web;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import org.apache.catalina.Session;
import org.apache.coyote.Adapter;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class ApplicationAdapter implements Adapter {

    private final RequestMapping requestMapping;

    public ApplicationAdapter(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public HttpResponse service(HttpRequest request, Session session) throws IOException {
        HttpResponse response = new HttpResponse();
        try {
            requestMapping.getController(request).service(request.withSession(session), response);
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
        return response;
    }
}
