package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.session.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginGetResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) throws Exception {
        if (request.hasJSessionId()) {
            if (SessionManager.isExist(request.getJSessionId())) {
                final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, ContentType.from("/index.html"), new String(getResponseBodyBytes("/index.html"), UTF_8));
                return httpResponse.getResponse();
            }
        }

        final String resourcePath = request.getRequestLine().getRequestUrl() + ".html";
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, ContentType.from(resourcePath), new String(getResponseBodyBytes(resourcePath), UTF_8));
        return httpResponse.getResponse();
    }

    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static" + resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }
}
