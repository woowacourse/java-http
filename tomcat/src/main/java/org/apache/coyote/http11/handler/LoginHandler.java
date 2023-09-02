package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class LoginHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("static/login.html");
        if (resource == null) {
            throw new IllegalArgumentException("해당 리소스를 찾을 수 없습니다.");
        }
        File file = new File(resource.getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType("text/html;charset=utf-8");
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK, headers);
        response.setContentBody(body);
        return response;
    }
}
