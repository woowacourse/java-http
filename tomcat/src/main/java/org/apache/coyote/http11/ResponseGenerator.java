package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.NotFoundUserException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StartLine;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResponseGenerator.class);
    public static final String ROOT_PATH = "/";
    private static final String STATIC_PATH = "static";
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT_KEY = "account";

    private ResponseGenerator() {
    }

    public static String generate(final Request request) throws IOException {
        if (ROOT_PATH.equals(request.getPath())) {
            final Response response = getDefaultResponse();
            return response.toMessage();
        }
        if (LOGIN_PATH.equals(request.getPath())) {
            final Response response = getLoginResponse(request);
            return response.toMessage();
        }

        final Response response = getFileResponse(request);
        return response.toMessage();
    }

    private static Response getDefaultResponse() {
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello world!";

        return Response.of(startLine, contentType, responseBody);
    }

    private static Response getLoginResponse(final Request request) throws IOException {
        final User user = InMemoryUserRepository.findByAccount(request.getQueryValue(ACCOUNT_KEY))
                                                .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));
        log.info(user.toString());

        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = getFileToResponseBody("/login.html");

        return Response.of(startLine, contentType, responseBody);
    }

    private static Response getFileResponse(final Request request) throws IOException {
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.findBy(request.getPath());
        final String responseBody = getFileToResponseBody(request.getPath());

        return Response.of(startLine, contentType, responseBody);
    }

    private static String getFileToResponseBody(final String fileName) throws IOException {
        final String path = STATIC_PATH + fileName;
        final URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        final String filePath = Objects.requireNonNull(resource).getPath();
        final File file = new File(URLDecoder.decode(filePath, StandardCharsets.UTF_8));

        return new String(Files.readAllBytes(file.toPath()));
    }
}
