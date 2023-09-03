package org.apache.coyote.http11.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {

    public static BufferedReader requestToInput(String httpRequest) {
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader request = new BufferedReader(new InputStreamReader(inputStream));
        return request;
    }
}
