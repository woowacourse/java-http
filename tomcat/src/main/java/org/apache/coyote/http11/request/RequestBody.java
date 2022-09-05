package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBody {

    private static final Logger log = LoggerFactory.getLogger(RequestBody.class);
    private static final int DEFAULT_QUERY_PARAMETER_PAIR_SIZE = 2;

    private final String contents;

    private RequestBody(final String contents) {
        this.contents = contents;
    }

    public static RequestBody of(final BufferedReader bufferedReader, final RequestHeaders requestHeaders) {
        try {
            return readBody(bufferedReader, requestHeaders);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 Request Body 형식이 아닙니다.");
        }

    }

    private static RequestBody readBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders)
            throws IOException {
        if (bufferedReader.ready()) {
            final int contentLength = Integer.parseInt(requestHeaders.findField("Content-Length"));
            final char[] contents = new char[contentLength];
            bufferedReader.read(contents, 0, contentLength);

            return new RequestBody(new String(contents));
        }

        return new RequestBody("");
    }

    public Map<String, String> parseApplicationFormData() {
        final Map<String, String> params = new HashMap<>();
        final String[] paramPairs = contents.split("&");

        for (final String param : paramPairs) {
            addParamPair(params, param);
        }

        return params;
    }

    private void addParamPair(final Map<String, String> params, final String param) {
        if (param == null || param.isBlank()) {
            return;
        }

        final String[] pair = param.split("=");
        if (pair.length != DEFAULT_QUERY_PARAMETER_PAIR_SIZE) {
            throw new IllegalArgumentException("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
        params.put(pair[0], pair[1]);
    }

    public String getContents() {
        return contents;
    }
}
