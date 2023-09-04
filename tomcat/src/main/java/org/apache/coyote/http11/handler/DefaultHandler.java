package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.StaticFileLoader;

public class DefaultHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String detail = request.getUri().getDetail();
        String content = StaticFileLoader.load(detail);

        String extension = request.getUri().getExtension();

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.getDetailfromExtension(extension));
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

        return HttpResponse.create(StatusCode.OK, headers, content);
    }
}
