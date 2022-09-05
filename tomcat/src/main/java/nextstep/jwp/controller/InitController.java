package nextstep.jwp.controller;

import org.apache.coyote.annotation.Controller;
import org.apache.coyote.annotation.RequestMapping;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

@Controller
public class InitController{

    @RequestMapping
    public HttpResponse home(final HttpRequest httpRequest){
        return HttpResponse.ok().body("Hello world!").build();
    }
}
