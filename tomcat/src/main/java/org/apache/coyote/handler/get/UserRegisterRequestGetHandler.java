package org.apache.coyote.handler.get;

import org.apache.coyote.common.Headers;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;

import java.util.Map;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class UserRegisterRequestGetHandler implements RequestHandler {

    private static final String REGISTER_PAGE_URI = "/register.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String registerPageResource = ResourceReader.read(REGISTER_PAGE_URI);
        final ResponseBody registerPageBody = new ResponseBody(registerPageResource);

        final Headers registerPageResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(registerPageBody.length())
        ));

        return new HttpResponse(HTTP_1_1, OK, registerPageResponseHeader, registerPageBody);
    }
}
