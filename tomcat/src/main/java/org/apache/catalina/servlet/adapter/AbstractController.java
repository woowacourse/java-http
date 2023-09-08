package org.apache.catalina.servlet.adapter;

import org.apache.coyote.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;

import static org.apache.coyote.http11.request.start.HttpMethod.*;

public abstract class AbstractController implements Controller {

    @Override
    public ResponseEntity service(HttpRequest request) {
        if (request.getHttpMethod().equals(GET)) {
            return doGet(request);
        }
        if (request.getHttpMethod().equals(POST)) {
            return doPost(request);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    protected ResponseEntity doPost(HttpRequest request) {
        return null;
    }
    protected ResponseEntity doGet(HttpRequest request) {
        return null;
    }
}