package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class StaticResourceResolver {

    public Optional<byte[]> read(String resourcePath) {
        URL resource = find(resourcePath);

        if (resource == null) {
            return Optional.empty();
        }

        return Optional.of(readBody(resource));
    }

    private URL find(String resourcePath) {
        return getClass()
                .getClassLoader()
                .getResource("static" + resourcePath);
    }

    private byte[] readBody(URL resource) {
        try (InputStream resourceStream = resource.openStream()) {
            return resourceStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
