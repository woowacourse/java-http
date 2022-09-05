package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestGenerator {

    public static HttpRequest generate(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return HttpRequest.from(reader);
    }
}
