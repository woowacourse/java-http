package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.RequestBody;

public class RequestExtractor {

    private RequestExtractor() {
    }

    public static Request extract(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String startLine = reader.readLine();
        Headers headers = extractHeaders(reader);
        RequestBody requestBody = extractBodyIfExists(reader, headers);

        return Request.from(startLine, headers, requestBody);
    }

    private static Headers extractHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            headers.add(line);
        }
        return Headers.fromLines(headers);
    }

    private static RequestBody extractBodyIfExists(BufferedReader reader,
                                                   Headers headers) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (Objects.nonNull(contentLength)) {
            return extractBody(reader, Integer.parseInt(contentLength));
        }
        return RequestBody.ofEmpty();
    }

    private static RequestBody extractBody(BufferedReader reader, int contentLength) throws IOException {
        List<String> body = new ArrayList<>();
        if (reader.ready()) {
            char[] buffer = new char[contentLength];
            reader.read(buffer);
            body.addAll(Arrays.asList(new String(buffer).trim()
                    .split("&")));
        }
        return RequestBody.from(body);
    }
}
