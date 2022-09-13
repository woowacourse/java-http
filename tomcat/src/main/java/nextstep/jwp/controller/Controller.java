package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
