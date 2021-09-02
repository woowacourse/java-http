package nextstep.jwp.webserver.request;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.webserver.util.HttpRequestParser;

public class RequestParams {

    private static final String AND_DELIMITER = "&";
    private static final String FORM_DATA = "application/x-www-form-urlencoded";

    private Map<String, String> params;
    private String body;

    public RequestParams(BufferedReader br, RequestHeader requestHeader, String queryString) throws IOException {
        this.params = parseParams(queryString);
        parseBody(br, requestHeader);
    }


    private Map<String, String> parseParams(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new HashMap<>();
        }
        return HttpRequestParser.parseValues(queryString, AND_DELIMITER);
    }

    private void parseBody(BufferedReader br, RequestHeader requestHeader) throws IOException {
        char[] body = readBody(br, requestHeader);

        if (isFormData(requestHeader.get(CONTENT_TYPE))) {
            addParams(String.copyValueOf(body));
            return;
        }

        addBody(String.copyValueOf(body));
    }

    private char[] readBody(BufferedReader br, RequestHeader requestHeader) throws IOException {
        int contentLength = requestHeader.contentLength();
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return body;
    }

    private boolean isFormData(String contentType) {
        return FORM_DATA.equals(contentType);
    }

    public void addBody(String content) {
        this.body = content;
    }

    public void addParams(String key, String value) {
        this.params.put(key, value);
    }

    private void addParams(String data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        params.putAll(HttpRequestParser.parseValues(data, AND_DELIMITER));
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
