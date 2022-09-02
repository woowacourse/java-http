package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private String url;
    private Map<String, String> queryStrings;
    private HttpHeaders httpHeaders;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        parseUri(bufferedReader);
        this.httpHeaders = new HttpHeaders(bufferedReader);
    }

    private void parseUri(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final String uri = firstLine.split(" ")[1];
        if (uri.contains("?")) {
            final int index = uri.indexOf("?");
            this.url = uri.substring(0, index);
            this.queryStrings = extractQueryString(uri.substring(index + 1));
            return;
        }
        this.url = uri;
    }

    private Map<String, String> extractQueryString(final String querystring) {
        final Map<String, String> queryStrings = new LinkedHashMap<>();
        final String[] split = querystring.split("&");
        for (String query : split) {
            final String[] parameterAndValue = query.split("=");
            queryStrings.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return queryStrings;
    }

    public boolean isFileRequest() {
        return this.url.contains(".");
    }

    public String getUrl() {
        return url;
    }

    public String getQueryString(final String parameter) {
        if (!queryStrings.containsKey(parameter)) {
            return "";
        }
        return queryStrings.get(parameter);
    }

    public String getFileExtension() {
        if (isFileRequest()) {
            final int index = url.indexOf(".");
            return url.substring(index + 1);
        }
        return "html";
    }
}
