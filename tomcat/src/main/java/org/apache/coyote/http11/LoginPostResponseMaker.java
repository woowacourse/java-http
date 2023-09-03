package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginPostResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final String request) throws Exception {
        String[] requestLines = request.split("\\s+");

//        if (requestLines.length < 2) {
//            throw new UncheckedServletException(new Exception("예외"));
//        }

        String resourcePath = requestLines[1];

        LoginHandler loginHandler = new LoginHandler();
        if (loginHandler.login(resourcePath)) {
            System.out.println("여기까지 도착");
            return successLoginResponse();
        }
        return failLoginResponse();
    }

    private String successLoginResponse() throws IOException {
        HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND, ContentType.HTML,new String(getResponseBodyBytes("/index.html"), UTF_8));
        return httpResponse.getResponse();
    }

    private String failLoginResponse() throws IOException {
        HttpResponse httpResponse = new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.HTML, new String(getResponseBodyBytes("/401.html"), UTF_8));
        return httpResponse.getResponse();
    }

    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static" + resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }
}
