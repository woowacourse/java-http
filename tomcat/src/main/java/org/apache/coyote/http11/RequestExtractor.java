package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.RequestBody;

public class RequestExtractor {

    private RequestExtractor() {
    }

    public static Request extract(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String startLine = reader.readLine();
        List<String> headers = extractHeaders(reader);
        List<String> body = extractBody(reader);

        return Request.from(startLine, Headers.from(headers), RequestBody.from(body));
    }

    private static List<String> extractHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private static List<String> extractBody(BufferedReader reader) throws IOException {
        List<String> body = new ArrayList<>();
        if (reader.ready()) {
            char[] buffer = new char[1024];
            reader.read(buffer);
            body.addAll(Arrays.asList(new String(buffer).trim()
                    .split("&")));
        }
        return body;
    }
}
