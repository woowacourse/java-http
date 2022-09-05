package nextstep.jwp.controller;

import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

public interface Controller {

    ResponseEntity execute(RequestEntity requestEntity);
}
