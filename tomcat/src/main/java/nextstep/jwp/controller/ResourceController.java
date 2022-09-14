package nextstep.jwp.controller;

import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.response.HttpStatus.OK;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.InternalException;
import org.apache.catalina.webutils.IOUtils;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest,
                         final HttpResponse httpResponse) {
        setResource(httpRequest, httpResponse);
    }

    private void setResource(final HttpRequest httpRequest,
                             final HttpResponse httpResponse) {
        final String path = httpRequest.getStartLine().getPath();
        final String fileName = Parser.convertResourceFileName(path);
        setResourceByFileName(fileName, httpResponse);
    }

    protected void setResource(final String fileName,
                               final HttpResponse httpResponse) {
        setResourceByFileName(fileName, httpResponse);
    }

    private void setResourceByFileName(final String fileName,
                                       final HttpResponse httpResponse) {
        final String fileType = Parser.parseFileType(fileName);
        try {
            final String body = IOUtils.readResourceFile(fileName);
            final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE.getValue(), ContentType.of(fileType), UTF_8.getValue());
            httpResponse.setHttpStatus(OK);
            httpResponse.addHeader(contentType);
            httpResponse.setBody(body);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("서버 에러가 발생했습니다.");
        }
    }

    protected void setRedirectHeader(final HttpResponse httpResponse, final ResourceUrls resourceUrls) {
        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), resourceUrls.getValue());
        httpResponse.setHttpStatus(REDIRECT);
        httpResponse.addHeader(location);
    }
}
