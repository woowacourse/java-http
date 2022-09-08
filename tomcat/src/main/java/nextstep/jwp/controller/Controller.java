package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public interface Controller {
    void service(HttpRequest request, ResponseEntity entity);

    boolean isMapped(HttpRequest request);
}
