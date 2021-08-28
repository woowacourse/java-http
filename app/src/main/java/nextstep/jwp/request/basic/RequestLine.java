package nextstep.jwp.request.basic;

public class RequestLine {

    private static final String SPACE = " ";
    private static final String QUERY_REGEX = "\\?";
    private static final int METHOD_INDEX = 0;
    private static final int TOTAL_URL_INDEX = 1;

    private static final int URL_INDEX = 0;
    private static final int QUERY_INDEX = 1;

    private HttpMethod httpMethod;
    private String url;
    private String queryString;

    public RequestLine(String line) {
        validateLine(line);
        String[] values = line.split(SPACE);
        this.httpMethod = HttpMethod.valueOf(values[METHOD_INDEX]);

        String[] urlForm = values[TOTAL_URL_INDEX].split(QUERY_REGEX);
        this.url = urlForm[URL_INDEX];
        if (urlForm.length >= QUERY_INDEX + 1) {
            this.queryString = urlForm[QUERY_INDEX];
        }
    }

    private void validateLine(String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalStateException();
        }
    }


    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public String httpUrl() {
        return url;
    }

    public String queryString() {
        return queryString;
    }
}
