package org.apache.coyote.controller.error;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.error.ErrorPage;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpResponse.HttpResponse;

public class ErrorPageController implements Controller {

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        String path = httpHeader.getPurePath();
        responseErrorPage(path, response);
    }

    private void responseErrorPage(
            final String errorPagePath,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = getStaticResponseBody("static" + errorPagePath);
        httpResponse.updateStatusLine("HTTP/1.1", ErrorPage.findStatusCode(errorPagePath));
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private String getStaticResponseBody(final String fileUrl) throws IOException {
        try {
            final URI uri = getClass().getClassLoader()
                    .getResource(fileUrl)
                    .toURI();
            final Path htmlPath = Path.of(uri);
            final byte[] read = Files.readAllBytes(htmlPath);
            final String body = new String(read, StandardCharsets.UTF_8);
            return body;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("존재하지 않은 정적 파일입니다.");
        }
    }
}
