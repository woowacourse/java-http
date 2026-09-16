package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        InMemoryUserRepository.findAll()
                .forEach(user -> log.info("registered user: {}", user));

        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
