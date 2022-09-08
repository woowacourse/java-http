package nextstep.jwp.controller;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startline.Extension;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HomeController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(Extension.HTML.getContentType())
                    .responseBody("Hello world!")
                    .build();
        }

        throw new MethodNotAllowedException();
    }
}
