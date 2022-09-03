package nextstep.jwp.controller;

import org.apache.coyote.http.HttpResponse;


public class InitController {

    public HttpResponse home(){
        return HttpResponse.ok().body("Hello world!").build();
    }
}
