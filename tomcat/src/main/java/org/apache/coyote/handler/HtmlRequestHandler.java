package org.apache.coyote.handler;

import org.apache.coyote.common.Headers;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.exception.CoyoteHttpException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.common.MediaType.TEXT_HTML;

public class HtmlRequestHandler implements RequestHandler {

    private static final String TYPE = ".html";
    private static final String DEFAULT = "Hello world!";
    private static final String RESOURCE = "static";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final ResponseBody responseBody = parseToResponseBody(httpRequest);
        final Headers responseHeader = parseToResponseHeader(responseBody);

        return new HttpResponse(httpRequest.httpVersion(), HttpStatus.OK, responseHeader, responseBody);
    }

    private ResponseBody parseToResponseBody(final HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.requestUri();
        if (requestPath.isDefault()) {
            return new ResponseBody(DEFAULT);
        }

        if (!requestPath.source().endsWith(TYPE)) {
            requestPath = new RequestPath(requestPath.source() + TYPE);
        }

        try {
            final URL resourceUrl = getClass().getClassLoader().getResource(RESOURCE + requestPath.source());
            final Path resourcePath = new File(resourceUrl.getFile()).toPath();

            return new ResponseBody(Files.readString(resourcePath));
        } catch (NullPointerException | IOException e) {
            throw new CoyoteHttpException("응답할 html 파일을 읽던 도중에 오류가 발생하였습니다.");
        }
    }

    private Headers parseToResponseHeader(final ResponseBody responseBody) {
        final Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source());
        responseHeaders.put(CONTENT_LENGTH.source(), String.valueOf(responseBody.length()));

        return new Headers(responseHeaders);
    }
}
