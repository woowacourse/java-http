package nextstep.org.apache.coyote.http11.request;

import static nextstep.org.apache.coyote.http11.HttpUtil.parseMultipleValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import nextstep.org.apache.catalina.cookie.Cookies;
import nextstep.org.apache.coyote.http11.HttpHeader;

public class Http11Request {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String EMPTY_LINE = "";
    private static final String FORM_VALUES_DELIMITER = "&";
    private static final String FORM_KEY_VALUE_DELIMITER = "=";

    private StartLine startLine;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<HttpHeader, String> headers = new HashMap<>();
    private final Cookies cookies = new Cookies();
    private Map<String, String> parsedBody = null;

    public Http11Request(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.startLine = new StartLine(bufferedReader.readLine());
        extractHeaders(bufferedReader);

        if (startLine.hasQueryString()) {
            parseMultipleValues(queryParams,
                    startLine.getQueryString(), "&", "=");
        }
        if (headers.containsKey(HttpHeader.COOKIE)) {
            cookies.parseCookieHeaders(headers.get(HttpHeader.COOKIE));
        }
        if (headers.containsKey(HttpHeader.CONTENT_LENGTH)) {
            parseBody(bufferedReader);
        }
    }

    private void extractHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        while (!EMPTY_LINE.equals(line = bufferedReader.readLine())) {
            extractHeader(line);
        }
    }

    private void extractHeader(String line) {
        try {
            String[] splited = line.split(": ");
            headers.put(HttpHeader.getHttpHeader(splited[KEY_INDEX]), splited[VALUE_INDEX].strip());
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void parseBody(BufferedReader bufferedReader) throws IOException{
        parsedBody = new HashMap<>();
        String requestBody = extractRequestBody(bufferedReader);
        parseMultipleValues(parsedBody,
                requestBody, FORM_VALUES_DELIMITER, FORM_KEY_VALUE_DELIMITER);
    }

    private String extractRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getMethod() {
        return startLine.getHttpMethod();
    }

    public String getPathInfo() {
        return startLine.getPath();
    }

    public String getHeader(HttpHeader httpHeader) {
        return headers.get(httpHeader);
    }

    public Cookies getCookies() {
        return cookies.defensiveCopy();
    }

    public String getParsedBodyValue(String name) {
        return parsedBody.get(name);
    }
}
