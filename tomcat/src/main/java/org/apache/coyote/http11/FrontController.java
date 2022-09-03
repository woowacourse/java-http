package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.exception.FileNotExistException;

import nextstep.jwp.exception.InvalidLoginRequestException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.model.User;

public class FrontController {

    private static final String STATIC_RESOURCE_PATH = "static";

    public static HttpResponse staticFileRequest(HttpRequest httpRequest) throws IOException {
        return staticFileRequest(httpRequest.getUri());
    }

    public static HttpResponse staticFileRequest(String fileName) throws IOException {
        try {
            Path filePath = findFilePath(fileName);
            String content = new String(Files.readAllBytes(filePath));

            String contentType = FileExtension.findContentType(fileName);
            return new HttpResponse(HttpStatus.OK, contentType, content);
        } catch (FileNotExistException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, null, "");
        }
    }

    private static Path findFilePath(String fileName) {
        try {
            return Path.of(Objects.requireNonNull(FrontController.class.getClassLoader()
                    .getResource(STATIC_RESOURCE_PATH + fileName)).getPath());
        } catch (NullPointerException e) {
            throw new FileNotExistException(fileName + " 파일이 존재하지 않습니다.");
        }
    }

    public static HttpResponse nonStaticFileRequest(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getUri().equals("/")) {
            String contentType = FileExtension.HTML.getContentType();
            return new HttpResponse(HttpStatus.OK, contentType, "Hello world!");
        }

        if (httpRequest.getUri().startsWith("/login")) {
            return performLogin(httpRequest);
        }
        return new HttpResponse(HttpStatus.NOT_FOUND, null, "");
    }

    private static HttpResponse performLogin(HttpRequest httpRequest) throws IOException {
        try {
            Map<String, String> queryValues = QueryStringParser.parseUri(httpRequest.getUri());
            LoginHandler loginHandler = new LoginHandler();
            User user = loginHandler.login(queryValues);

            return staticFileRequest("/login.html");
        } catch (InvalidLoginRequestException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, null, "");
        }
    }
}
