package nextstep.jwp.presentation;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.getUri();
        final String content = ResourceLoader.getContent(uri);

        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        final String contentType = ResourceLoader.getContentType(uri);
        httpHeaders.put(HttpHeader.CONTENT_TYPE, contentType);

        return new HttpResponse(HttpStatus.OK, httpHeaders, content);
    }
}
