package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidRequestHeaderException;

public class RequestHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;
    private static final String HEADER_DELIMITER = ":";
    private static final String CONTENT_LENGTH = "content-length";
    private static final String BLANK = "";

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (isNotEmpty(line)) {
            String[] values = splitLine(line);
            String key = values[KEY_INDEX].toLowerCase();
            String value = values[VALUE_INDEX].trim().toLowerCase();

            headers.put(key, value);
            line = bufferedReader.readLine();
        }

        return new RequestHeaders(headers);
    }

    private static String[] splitLine(String line) {
        String[] values = line.split(HEADER_DELIMITER, EXPECT_LINE_LENGTH);
        if (values.length != EXPECT_LINE_LENGTH) {
            throw new InvalidRequestHeaderException();
        }

        return values;
    }

    private static boolean isNotEmpty(String line) {
        if (line == null || BLANK.equals(line)) {
            return false;
        }
        return true;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }
}
