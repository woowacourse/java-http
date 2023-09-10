package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class AbstractController implements Controller {

    @Override
    public ResponseEntity<? extends Object> handle(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getRequestLine().getMethod();
        if (method == HttpMethod.GET) {
            return doGet(httpRequest);
        }
        if (method == HttpMethod.POST) {
            return doPost(httpRequest);
        }
        if (method == HttpMethod.PUT) {
            return doPut(httpRequest);
        }
        if (method == HttpMethod.DELETE) {
            return doDelete(httpRequest);
        }
        throw new UnsupportedOperationException("해당 HttpMethod 는 아직 지원하지 않습니다." + method);
    }

    public ResponseEntity<? extends Object> doGet(HttpRequest httpRequest) {
        return ResponseEntity.notAllowed();
    }

    public ResponseEntity<? extends Object> doPost(HttpRequest httpRequest) {
        return ResponseEntity.notAllowed();
    }

    public ResponseEntity<? extends Object> doPut(HttpRequest httpRequest) {
        return ResponseEntity.notAllowed();
    }

    public ResponseEntity<? extends Object> doDelete(HttpRequest httpRequest) {
        return ResponseEntity.notAllowed();
    }
}
