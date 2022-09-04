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
import org.apache.coyote.web.request.Request;
import org.apache.coyote.web.response.BodyResponse;
import org.apache.coyote.web.response.Response;
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

    public Response handle(final Request request) throws IOException {
        String responseBody = fileHandler.getFileLines(request.getRequestLine().getRequestUrl());
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.from(request.getRequestExtension()).getValue()));
        if (request.isSameRequestUrl("/login.html")) {
            checkLogin(request);
            return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
        }

        if (request.isSameRequestUrl("/register.html")) {
            checkLogin(request);
            return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
        }

        return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
    }

    private void checkLogin(final Request request) {
        Optional<String> sessionValue = request.getSession();
        if (sessionValue.isPresent()) {
            Optional<Session> sessionOrEmpty = SessionManager.findSession(sessionValue.get());
            sessionOrEmpty.ifPresent(session -> session.getAttribute("user")
                    .ifPresentOrElse(existUser -> logger.info("user = {}", existUser), session::invalidate));
        }
    }
}
