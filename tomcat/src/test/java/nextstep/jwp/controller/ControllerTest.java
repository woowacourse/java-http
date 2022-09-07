package nextstep.jwp.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class ControllerTest {

    protected BufferedReader toBufferedReader(String request) {
        return new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
    }
}
