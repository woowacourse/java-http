package org.apache.coyote.http11.http11handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.model.User;
import org.apache.coyote.ExtensionContentType;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.slf4j.Logger;

public class LoginPageHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String DIRECTORY = "static";

    private QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(String uri) {
        uri = queryStringProcessor.removeQueryString(uri);
        return uri.equals(URI);
    }

    @Override
    public Map<String, String> handle(Logger log, String uri) {
        if (queryStringProcessor.existQueryString(uri)) {
            checkValidUser(log, queryStringProcessor.extractQueryString(uri));
            uri = queryStringProcessor.removeQueryString(uri);
        }

        uri = handlerSupporter.addHtmlExtension(uri);
        return handlerSupporter.extractElements(uri);
    }

    private void checkValidUser(Logger log, Map<String, String> queryString) {
        User user = InMemoryUserRepository.findByAccount(queryString.get("account")).orElseThrow(
                LoginFailedException::new);
        if (user.checkPassword(queryString.get("password"))) {
            log.info(String.valueOf(user));
        }
    }

    private String extractBody(String uri) {
        try {
            return Files.readString(new File(Objects.requireNonNull(
                    getClass().getClassLoader().getResource(DIRECTORY + uri)).getFile()).toPath());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private String getContentType(String uri) {
        String extension = extractExtension(uri);
        return ExtensionContentType.toContentType(extension);
    }

    private String extractExtension(String uri) {
        int extensionStartIndex = uri.lastIndexOf(".") + 1;
        return uri.substring(extensionStartIndex);
    }

    private String getContentLength(String uri) {
        return Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource(DIRECTORY + uri)).getFile()).length());
    }
}
