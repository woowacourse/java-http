package nextstep.jwp.controller;

import static org.apache.coyote.http11.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.HeaderField.CONTENT_TYPE;
import static org.apache.coyote.http11.HeaderField.LOCATION;

import java.io.IOException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {

        String requestUri = request.getRequestUri();
        ResponseBody body = ResponseBody.from(requestUri);
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(CONTENT_TYPE, ContentType.find(requestUri) + ";charset=utf-8 ")
                .addHeader(CONTENT_LENGTH, body.getBody());
        return HttpResponse.create(HttpStatus.OK, headers, body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {

        ResponseBody body = new ResponseBody();
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(LOCATION, "/index");
        return HttpResponse.create(HttpStatus.FOUND, headers, body);
    }
}
