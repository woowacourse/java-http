package org.apache.coyote.http11.controller;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.util.Parser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Handler {
    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String path = httpRequest.getUrl();
        final String fileName = Parser.convertResourceFileName(path);

        try {
            httpResponse.setOkResponse(fileName);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("서버 에러가 발생했습니다.");
        }
    }
}
