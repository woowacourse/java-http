package com.techcourse.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.coyote.http.HttpRequest;

public abstract class ControllerTest {

    protected HttpRequest httpRequest(final String input) throws IOException {
        byte[] inputBytes = input.getBytes();
        var inputStream = new ByteArrayInputStream(inputBytes);
        return new HttpRequest(inputStream);
    }
}
