package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.LoginHandler;
import org.apache.coyote.http11.request.HttpRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginPostResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) throws Exception {
        final LoginHandler loginHandler = new LoginHandler();
        if (loginHandler.login(request.getRequestBody())) {
            return successLoginResponse(request);
        }

        return failLoginResponse();
    }

    private String successLoginResponse(final HttpRequest request) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND, ContentType.HTML, new String(getResponseBodyBytes("/index.html"), UTF_8));
        httpResponse.addJSessionId(request);
        return httpResponse.getResponse();
    }

    private String failLoginResponse() throws IOException {
        final HttpResponse httpResponse = new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.HTML, new String(getResponseBodyBytes("/401.html"), UTF_8));
        return httpResponse.getResponse();
    }

    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static" + resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }

}
