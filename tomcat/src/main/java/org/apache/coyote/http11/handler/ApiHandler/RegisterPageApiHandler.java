package org.apache.coyote.http11.handler.ApiHandler;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class RegisterPageApiHandler implements Handler {

    private static final Pattern REGISTER_URI_PATTERN = Pattern.compile("/register");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, REGISTER_URI_PATTERN);
    }

    @Override
    public Object getResponse(HttpRequest httpRequest) {
        return ApiHandlerResponse.of(HttpStatus.OK, new LinkedHashMap<>(), "/register.html", ContentType.HTML);
    }
}
