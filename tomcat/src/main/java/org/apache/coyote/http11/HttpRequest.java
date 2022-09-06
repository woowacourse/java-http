package org.apache.coyote.http11;

public class HttpRequest {

    private static final String SPACE = " ";
    private static final int RESOURCE_INDEX = 1;
    private static final String LOGIN_REQUEST = "login";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String FILE_SOURCE_QUERY_STRING_DELIMITER = "\\?";
    private static final int QUERY_STRING_INDEX = 1;
    private static final int FILE_NAME_INDEX = 0;

    private final String fileSource;
    private final QueryParameter queryParameter;

    public HttpRequest(String fileSource, QueryParameter queryParameter) {
        this.fileSource = fileSource;
        this.queryParameter = queryParameter;
    }

    public static HttpRequest from(String request) {
        String requestFirstLine = request.split("\r\n")[0];
        String resource = requestFirstLine.split(SPACE)[RESOURCE_INDEX];
        if (!resource.contains(QUERY_STRING_PREFIX)) {
            return new HttpRequest(resource, null);
        }
        return initializeWithQueryString(resource);
    }

    private static HttpRequest initializeWithQueryString(String resource) {
        String[] fileNameAndQueryString = resource.split(FILE_SOURCE_QUERY_STRING_DELIMITER);
        String queryString = fileNameAndQueryString[QUERY_STRING_INDEX];
        String fileName = fileNameAndQueryString[FILE_NAME_INDEX];
        QueryParameter query = QueryParameter.from(queryString);
        return new HttpRequest(fileName, query);
    }

    public String getFileSource() {
        return fileSource;
    }

    public boolean isLoginRequest() {
        return fileSource.contains(LOGIN_REQUEST);
    }

    public String getQueryParamValueOf(String key) {
        if (queryParameter == null) {
            throw new QueryStringNotFoundException();
        }
        return queryParameter.getValues()
                .get(key);
    }
}
