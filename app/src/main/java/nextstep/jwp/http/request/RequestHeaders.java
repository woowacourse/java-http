package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidRequestHeader;

public class RequestHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;
    private static final String CONTENT_LENGTH = "content-length";
    private static final String TRANSFER_ENCODING = "transfer-encoding";

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();

        while (isNotEmpty(line)) {
            String[] splitedLine = splitLine(line);

            String key = splitedLine[KEY_INDEX].toLowerCase();
            String value = splitedLine[VALUE_INDEX].trim().toLowerCase();

            headers.put(key, value);
            line = bufferedReader.readLine();
        }

        return new RequestHeaders(headers);
    }

    private static boolean isNotEmpty(String line) {
        return !"".equals(line);
    }

    private static String[] splitLine(String line) {
        String[] splitedLine = line.split(":", EXPECT_LINE_LENGTH);

        if (splitedLine.length != EXPECT_LINE_LENGTH) {
            throw new InvalidRequestHeader();
        }

        return splitedLine;
    }

    public boolean requestHasBody() {
        return headers.containsKey(CONTENT_LENGTH) || headers.containsKey(TRANSFER_ENCODING);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }
}
