package org.apache.coyote.http11.util;

import static org.apache.coyote.http11.common.HttpHeaders.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.common.HttpBody;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpRequest;
import org.apache.coyote.http11.common.HttpStartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParser {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParser.class);

    private static final String LINE_BEFORE_READ = " ";
    private static final int START_LINE = 0;

    private RequestParser() {
    }

    public static HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHeaders = readHeaders(bufferedReader);

        final String rawStartLine = rawHeaders.remove(START_LINE);
        final HttpStartLine startLine = HttpStartLine.from(rawStartLine);
        final HttpHeaders headers = HttpHeaders.from(rawHeaders);
        final HttpBody body = HttpBody.from(readBody(bufferedReader, headers));

        LOG.info("============= HTTP REQUEST =============");
        LOG.info("{}\n{}\n\n{}", rawStartLine, headers, body);

        return new HttpRequest(startLine, headers, body);
    }

    private static List<String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = new ArrayList<>();

        String line = LINE_BEFORE_READ;
        while (!line.isEmpty()) {
            line = bufferedReader.readLine();
            rawHttpRequest.add(line);
        }
        return rawHttpRequest;
    }

    private static String readBody(final BufferedReader bufferedReader, final HttpHeaders headers) throws IOException {
        final String header = headers.getHeader(CONTENT_LENGTH);
        if (header == null) {
            return null;
        }

        final int contentLength = Integer.parseInt(header);
        final char[] buffer = new char[contentLength];
        if (bufferedReader.ready()) {
            bufferedReader.read(buffer);
        }

        return new String(buffer);
    }
}
