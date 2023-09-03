package nextstep.jwp.controller;

import nextstep.servlet.controller.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface RestController extends Controller {

    @Override
    boolean canHandle(HttpRequest request);

    @Override
    HttpResponse handle(HttpRequest request);
}
