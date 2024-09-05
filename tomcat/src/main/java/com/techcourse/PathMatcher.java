package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.ResourcesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PathMatcher {
    private static final Logger log = LoggerFactory.getLogger(PathMatcher.class);
    private final ResourcesReader resourcesReader;

    public PathMatcher(final ResourcesReader resourcesReader) {
        this.resourcesReader = resourcesReader;
    }

    public Resource match(final HttpRequest request) throws IOException {
        if (request.getPath()
                .equals("/login")) {
            final String account = request.getQueryParam("account");
            final String password = request.getQueryParam("password");
            InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password))
                    .ifPresent(user -> log.info("{}", user));
        }
        return resourcesReader.read(request.getPath());
    }

}
