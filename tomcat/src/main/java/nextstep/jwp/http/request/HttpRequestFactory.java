package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidRequestHeaderException;
import nextstep.jwp.http.common.HttpHeaders;

public class HttpRequestFactory {

    private static final String BLANK = "";
    private static final String HEADERS_PARAM_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;

    public static HttpRequest create(final BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.create(bufferedReader.readLine());
        HttpHeaders requestHeaders = new HttpHeaders(
            parseRequestHeadersValue(bufferedReader));
        RequestBody requestBody = parseRequestBodyValue(bufferedReader, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static Map<String, String> parseRequestHeadersValue(
        final BufferedReader bufferedReader) throws IOException {

        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (isNotEmpty(line)) {
            String[] values = convertBlankDeleteAndLowerCase(parseLine(line));
            headers.put(values[HEADER_KEY_INDEX], values[HEADER_VALUE_INDEX]);
            line = bufferedReader.readLine();
        }

        return headers;
    }

    private static boolean isNotEmpty(final String line) {
        return line != null && !BLANK.equals(line);
    }

    private static String[] parseLine(String line) {
        String[] values = line.split(HEADERS_PARAM_DELIMITER);
        if (values.length != EXPECT_LINE_LENGTH) {
            throw new InvalidRequestHeaderException();
        }

        return values;
    }

    private static String[] convertBlankDeleteAndLowerCase(final String[] lines) {
        lines[HEADER_KEY_INDEX] = lines[HEADER_KEY_INDEX].strip();
        lines[HEADER_VALUE_INDEX] = lines[HEADER_VALUE_INDEX].strip();
        return new String[] {lines[HEADER_KEY_INDEX], lines[HEADER_VALUE_INDEX]};
    }

    private static RequestBody parseRequestBodyValue(final BufferedReader bufferedReader,
                                                     final HttpHeaders requestHeaders)
        throws IOException {

        int contentLength = requestHeaders.getContentLength();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new RequestBody(String.valueOf(buffer));
    }
}
