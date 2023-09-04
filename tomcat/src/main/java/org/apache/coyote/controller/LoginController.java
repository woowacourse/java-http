package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public String run(final String parsedUri) throws IOException {
        final Map<String, String> queryStrings = parseQueryStrings(parsedUri);
        if (queryStrings.isEmpty()) {
            final FileResolver file = FileResolver.findFile(parsedUri);
            return file.getResponse();
        }
        return login(queryStrings);
    }

    private String login(final Map<String, String> queryStrings) {
        log.info("queryStrings = ", queryStrings);
        if (isValidUser(queryStrings)) {
            return createRedirectResponse("/index.html");
        }
        return createRedirectResponse("/401.html");
    }

    private Map<String, String> parseQueryStrings(final String parsedUri) {
        if (!parsedUri.contains("?")) {
            return Collections.emptyMap();
        }
        final int index = parsedUri.indexOf("?");
        final String queryStringUri = parsedUri.substring(index + 1);
        final String[] strings = queryStringUri.split("&");
        Map<String, String> queryStrings = new HashMap<>();
        for (final String string : strings) {
            final String[] keyValue = string.split("=");
            queryStrings.put(keyValue[0], keyValue[1]);
        }
        return queryStrings;
    }

    private String createRedirectResponse(final String fileName) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + fileName + " ",
                ""
        );
    }

    private boolean isValidUser(final Map<String, String> queryStrings) {
        final User account = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                                                   .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));
        return account.checkPassword(queryStrings.get("password"));
    }
}
