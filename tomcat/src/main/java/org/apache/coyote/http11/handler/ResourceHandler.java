package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.http.response.HttpStatus.OK;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.catalina.utils.IOUtils;
import org.apache.catalina.utils.Parser;
import org.apache.coyote.http11.exception.InternalException;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class ResourceHandler implements Handler{
    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return generateResourceResponse(httpRequest);
    }

    protected HttpResponse generateResourceResponse(final HttpRequest httpRequest) {
        final String path = httpRequest.getStartLine().getPath();
        final String fileName = Parser.convertResourceFileName(path);
        final String fileType = Parser.parseFileType(fileName);
        try {
            final String body = IOUtils.readResourceFile(fileName);
            final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, ContentType.of(fileType), UTF_8.getValue());
            final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));

            return HttpResponse.of(HTTP11, OK, body, contentType, contentLength);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("서버 에러가 발생했습니다.");
        }
    }
}
