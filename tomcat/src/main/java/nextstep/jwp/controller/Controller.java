package nextstep.jwp.controller;

import nextstep.jwp.http.RequestEntity;
import nextstep.jwp.http.ResponseEntity;

public interface Controller {

    ResponseEntity execute(RequestEntity requestEntity);
}
