package org.apache.coyote.web;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.file.DefaultFileHandler;
import org.apache.coyote.file.FileHandler;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.BodyResponse;
import org.apache.coyote.web.response.HttpResponse;
import org.apache.coyote.web.session.Session;
import org.apache.coyote.web.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRequestHandler {

    private final static Logger logger = LoggerFactory.getLogger(FileRequestHandler.class);

    private final FileHandler fileHandler;

    public FileRequestHandler() {
        this.fileHandler = new DefaultFileHandler();
    }

    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String responseBody = fileHandler.getFileLines(httpRequest.getHttpRequestLine().getRequestUrl());
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.from(httpRequest.getRequestExtension()).getValue()));
        if (httpRequest.isSameRequestUrl("/login.html")) {
            checkLogin(httpRequest);
            return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
        }

        if (httpRequest.isSameRequestUrl("/register.html")) {
            checkLogin(httpRequest);
            return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
        }

        return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
    }

    private void checkLogin(final HttpRequest httpRequest) {
        Optional<String> sessionValue = httpRequest.getSession();
        if (sessionValue.isPresent()) {
            Optional<Session> sessionOrEmpty = SessionManager.findSession(sessionValue.get());
            sessionOrEmpty.ifPresent(session -> session.getAttribute("user")
                    .ifPresentOrElse(existUser -> logger.info("user = {}", existUser), session::invalidate));
        }
    }
}
