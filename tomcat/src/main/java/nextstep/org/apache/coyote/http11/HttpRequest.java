package nextstep.org.apache.coyote.http11;

import static nextstep.org.apache.coyote.http11.HttpUtil.parseMultipleValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String EMPTY_LINE = "";
    private static final String FORM_VALUES_DELIMITER = "&";
    private static final String FORM_KEY_VALUE_DELIMITER = "=";

    private StartLine startLine;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private final Cookies cookies = new Cookies();
    private Map<String, String> parsedBody = null;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.startLine = new StartLine(bufferedReader.readLine());
        extractHeaders(bufferedReader);

        if (startLine.hasQueryString()) {
            parseMultipleValues(queryParams,
                    startLine.getQueryString(), "&", "=");
        }
        if (headers.containsKey("Cookie")) {
            cookies.parseCookieHeaders(headers.get("Cookie"));
        }
        if (headers.containsKey("Content-Length")) {
            parseBody(bufferedReader);
        }
    }

    private void extractHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        while (!EMPTY_LINE.equals(line = bufferedReader.readLine())) {
            String[] splited = line.split(": ");
            headers.put(splited[KEY_INDEX], splited[VALUE_INDEX].strip());
        }
    }

    private void parseBody(BufferedReader bufferedReader) throws IOException{
        parsedBody = new HashMap<>();
        String requestBody = extractRequestBody(bufferedReader);
        parseMultipleValues(parsedBody,
                requestBody, FORM_VALUES_DELIMITER, FORM_KEY_VALUE_DELIMITER);
    }

    private String extractRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
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

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookies getCookies() {
        return cookies.defensiveCopy();
    }

    public String getParsedBodyValue(String name) {
        return parsedBody.get(name);
    }
}
