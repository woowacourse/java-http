package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.FOUND;
import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.header.HttpHeaders.LOCATION;

import java.util.Map;
import nextstep.jwp.application.RegisterService;
import nextstep.jwp.util.ResourceLoaderUtil;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.http11.handler.AbstractController;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = ResourceLoaderUtil.loadContent(request.requestUrl());
        response.setVersion(request.protocolVersion());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, ContentType.negotiate(request.requestUrl()));
        response.addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        response.setBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> requestBody = request.getRequestBody();
        registerService.register(
                requestBody.get("account"),
                requestBody.get("email"),
                requestBody.get("password")
        );

        response.setVersion(request.protocolVersion());
        response.setStatus(FOUND);
        response.addHeader(LOCATION, "index.html");
    }
}
