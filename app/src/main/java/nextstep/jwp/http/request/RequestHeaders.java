package nextstep.jwp.http.request;

import static nextstep.jwp.http.common.HttpHeader.CONTENT_LENGTH;
import static nextstep.jwp.http.common.HttpHeader.COOKIE;
import static nextstep.jwp.http.common.HttpHeader.TRANSFER_ENCODING;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import nextstep.jwp.exception.HttpRequestNotHaveBodyException;
import nextstep.jwp.exception.InvalidRequestHeader;

public class RequestHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;
    private static final String NEW_LINE = System.getProperty("line.separator");

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

    public boolean requestHasCookie() {
        return headers.containsKey(COOKIE.toLowerString());
    }

    public boolean requestHasBody() {
        return headers.containsKey(CONTENT_LENGTH.toLowerString())
            || headers.containsKey(TRANSFER_ENCODING.toLowerString());
    }

    private boolean requestNotHaveBody() {
        return !headers.containsKey(CONTENT_LENGTH.toLowerString())
            || headers.containsKey(TRANSFER_ENCODING.toLowerString());
    }

    public int getContentLength() {
        if (requestNotHaveBody()) {
            throw new HttpRequestNotHaveBodyException();
        }

        return Integer.parseInt(headers.get(CONTENT_LENGTH.toLowerString()));
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(NEW_LINE);

        for (Entry<String, String> entry : headers.entrySet()) {
            String header = String.format("%s: %s", entry.getKey(), entry.getValue());
            stringJoiner.add(header);
        }

        return stringJoiner.toString();
    }
}
