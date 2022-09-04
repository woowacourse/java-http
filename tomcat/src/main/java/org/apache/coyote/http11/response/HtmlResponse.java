package org.apache.coyote.http11.response;

import java.net.URL;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.support.FileUtils;

public class HtmlResponse extends HttpResponse {

    private static final String START_PATH_FLAG = "/";

    private HtmlResponse(final HttpStatus status,
                         final HttpHeaders unnecessaryHeaders,
                         final String responseBody) {
        super(ContentType.TEXT_HTML, status, unnecessaryHeaders, responseBody);
    }

    public static HttpResponse of(final HttpStatus status,
                                  final HttpHeaders unnecessaryHeaders,
                                  final String htmlNameWithoutExtension) {
        final URL resource = FileUtils.getResourceFromUri(START_PATH_FLAG + htmlNameWithoutExtension + ".html")
                .orElseThrow(() -> new NotFoundException(
                        String.format("html파일이 존재하지 않습니다. %s", htmlNameWithoutExtension))
                );

        final String responseBody = FileUtils.readContentByStaticFilePath(resource.getPath());
        return new HtmlResponse(status, unnecessaryHeaders, responseBody);
    }
}
