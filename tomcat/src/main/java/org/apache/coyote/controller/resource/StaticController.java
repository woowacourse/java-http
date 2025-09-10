package org.apache.coyote.controller.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class StaticController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            responseStaticFile(httpHeader, response);
        }
    }

    private void responseStaticFile(final HttpHeader httpHeader, final HttpResponse httpResponse) throws IOException {
        final String path = httpHeader.getPurePath();
        final String body = getStaticResponseBody("static" + path);
        httpResponse.updateStatusLine("HTTP/1.1", StatusCode.OK);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", ResourceType.findResourceType(path).getType());
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
