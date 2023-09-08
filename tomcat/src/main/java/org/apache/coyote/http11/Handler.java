package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);
    private static final String FILE_DELIMITER = "\\.";
    private static final int FILE_EXTENSION_INDEX = 1;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private final HandlerMapping handlerMapping;
    private final QueryString queryString;
    private final RequestBody requestBody;

    public Handler(
        final HandlerMapping handlerMapping,
        final QueryString queryString,
        final RequestBody requestBody
    ) {
        this.handlerMapping = handlerMapping;
        this.queryString = queryString;
        this.requestBody = requestBody;
    }

    public HttpResponse makeResponse() throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        if (handlerMapping == null) {
            return makeNotFoundResponse();
        }
        final String[] split = handlerMapping.getResponse().split(FILE_DELIMITER);
        if (split.length == 1) {
            return makeStringResponse(headers);
        }
        if (handlerMapping == HandlerMapping.LOGIN || handlerMapping == HandlerMapping.LOGIN_POST) {
            return makeLoginResponse(headers, split[FILE_EXTENSION_INDEX]);
        }
        if (handlerMapping == HandlerMapping.REGISTER_POST) {
            return makeRegisterResponse(headers, split[FILE_EXTENSION_INDEX]);
        }
        return makeFileResponse(headers, split[FILE_EXTENSION_INDEX]);
    }

    private HttpResponse makeNotFoundResponse() throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        final String body = findFile("404.html");
        headers.put(CONTENT_TYPE, ContentType.HTML.getValue());
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.NOT_FOUND, new HttpHeader(headers), body);
    }

    private HttpResponse makeStringResponse(final Map<String, String> headers) {
        final String body = handlerMapping.getResponse();
        headers.put(CONTENT_TYPE, ContentType.HTML.getValue() + ";charset=utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
    }

    private HttpResponse makeFileResponse(final Map<String, String> headers, final String fileType)
        throws IOException {
        final String body = findFile(handlerMapping.getResponse());
        headers.put(CONTENT_TYPE, ContentType.valueOf(fileType.toUpperCase()).getValue() + ";charset=utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
    }

    private HttpResponse makeLoginResponse(final Map<String, String> headers, final String fileType)
        throws IOException {
        final String body = findFile(handlerMapping.getResponse());
        headers.put(CONTENT_TYPE, ContentType.valueOf(fileType.toUpperCase()).getValue() + ";charset=utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));

        if (queryString == null && requestBody == null) {
            return new HttpResponse(StatusCode.OK, new HttpHeader(headers), body);
        }

        getMemberByAccount().ifPresentOrElse((member) -> {
            headers.put(LOCATION, "/401.html");
            if (isExistMember(member)) {
                log.info("user : " + member);
                headers.put(LOCATION, "/index.html");
            }
        }, () -> headers.put(LOCATION, "/401.html"));

        return new HttpResponse(StatusCode.REDIRECT, new HttpHeader(headers), body);
    }

    private boolean isExistMember(final User user) {
        if (requestBody == null) {
            return user.checkPassword(queryString.getPassword());
        }
        return user.checkPassword(requestBody.getPassword());
    }

    private Optional<User> getMemberByAccount() {
        if (requestBody == null) {
            return InMemoryUserRepository.findByAccount(queryString.getAccount());
        }
        return InMemoryUserRepository.findByAccount(requestBody.getAccount());
    }

    private HttpResponse makeRegisterResponse(
        final Map<String, String> headers,
        final String fileType
    ) throws IOException {
        final String body = findFile(handlerMapping.getResponse());
        headers.put(CONTENT_TYPE,
            ContentType.valueOf(fileType.toUpperCase()).getValue() + ";charset=utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        headers.put(LOCATION, "/index.html");
        final User registerUser = new User(requestBody.getAccount(), requestBody.getPassword(),
            requestBody.getEmail());
        InMemoryUserRepository.save(registerUser);
        return new HttpResponse(StatusCode.REDIRECT, new HttpHeader(headers), body);
    }

    private String findFile(final String response) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("static/" + response);
        return new String(
            Files.readAllBytes(new File(Objects.requireNonNull(resource).getPath()).toPath()));
    }
}
