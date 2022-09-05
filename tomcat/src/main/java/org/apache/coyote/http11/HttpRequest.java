package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private static final String SPACE_DELIMITER = " ";
    private static final String QUERY_STRING_START = "?";
    private static final String QUERY_STRING_AND = "&";
    private static final String QUERY_STRING_EQUAL = "=";
    private static final String FILE_EXTENSION_DELIMITER = ".";

    private String url;
    private Map<String, String> queryStrings = new LinkedHashMap<>();
    private HttpHeaders httpHeaders;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        parseUri(bufferedReader);
        this.httpHeaders = new HttpHeaders(bufferedReader);
    }

    private void parseUri(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        final String uri = startLine.split(SPACE_DELIMITER)[1];
        if (uri.contains(QUERY_STRING_START)) {
            final int index = uri.indexOf(QUERY_STRING_START);
            this.url = uri.substring(0, index);
            this.queryStrings = extractQueryString(uri.substring(index + 1));
            return;
        }
        this.url = uri;
    }

    private Map<String, String> extractQueryString(final String querystring) {
        final Map<String, String> queryStrings = new LinkedHashMap<>();
        final String[] queries = querystring.split(QUERY_STRING_AND);
        for (String query : queries) {
            final String[] parameterAndValue = query.split(QUERY_STRING_EQUAL);
            queryStrings.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return queryStrings;
    }

    public boolean isFileRequest() {
        return this.url.contains(FILE_EXTENSION_DELIMITER);
    }

    public String getQueryString(final String parameter) {
        if (queryStrings.isEmpty() || !queryStrings.containsKey(parameter)) {
            return "";
        }
        return queryStrings.get(parameter);
    }

    public String getFileExtension() {
        if (isFileRequest()) {
            final int index = url.indexOf(FILE_EXTENSION_DELIMITER);
            return url.substring(index + 1);
        }
        return "html";
    }
    
    public String getUrl() {
        return url;
    }
}
