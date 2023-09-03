package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);
    private static final String FILE_DELIMITER = "\\.";
    private static final int FILE_EXTENSION_INDEX = 1;

    private final HandlerMapping handlerMapping;
    private final QueryString queryString;

    public Handler(final HandlerMapping handlerMapping, final QueryString queryString) {
        this.handlerMapping = handlerMapping;
        this.queryString = queryString;
    }

    public HttpResponse makeResponse() throws IOException {
        if (handlerMapping == null) {
            return makeNotFoundResponse();
        }
        final String[] split = handlerMapping.getResponse().split(FILE_DELIMITER);
        if (split.length == 1) {
            return makeStringResponse();
        }
        if (handlerMapping == HandlerMapping.LOGIN) {
            return makeLoginResponse(split[FILE_EXTENSION_INDEX]);
        }
        return makeFileResponse(split[FILE_EXTENSION_INDEX]);
    }

    private HttpResponse makeNotFoundResponse() throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        final String body = findFile("404.html");
        headers.put("Content-Type", ContentType.HTML.getValue());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.NOT_FOUND, new HttpHeader(headers), body);
    }

    private HttpResponse makeStringResponse() {
        final String body = handlerMapping.getResponse();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
    }

    private HttpResponse makeFileResponse(final String fileType) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        final String body = findFile(handlerMapping.getResponse());
        headers.put("Content-Type",
            ContentType.valueOf(fileType.toUpperCase()).getValue() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
    }

    private HttpResponse makeLoginResponse(final String fileType) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        final String body = findFile(handlerMapping.getResponse());
        headers.put("Content-Type",
            ContentType.valueOf(fileType.toUpperCase()).getValue() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        findMember();
        return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
    }

    private void findMember() {
        if (queryString == null) {
            return;
        }
        InMemoryUserRepository.findByAccount(queryString.getAccount())
            .ifPresent(user -> log.info("user : " + user));
    }

    private String findFile(final String response) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("static/" + response);
        return new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getPath()).toPath()));
    }
}
