package org.apache.coyote.handler.get;

import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class UserRegisterRequestGetHandler implements RequestHandler {

    private static final String REGISTER_PAGE_URI = "/register.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String registerPageResource = ResourceReader.read(REGISTER_PAGE_URI);
        final ResponseBody registerPageBody = new ResponseBody(registerPageResource);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.source())
                .setContentLength(registerPageBody.length())
                .setResponseBody(registerPageBody)
                .build();
    }
}
