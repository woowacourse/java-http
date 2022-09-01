package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestUri {

    private static final int URI_INDEX = 0;
    private static final int QUERY_PARAM_INDEX = 1;
    private static final String URI_QUERY_DELIMITER = "?";
    private static final String URI_QUERY_REGEX = "\\?";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PARAM_DELIMITER = "=";
    private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<>();

    private final String value;
    private final Map<String, String> queryParameters;

    public RequestUri(final String value, final Map<String, String> queryParameters) {
        Objects.requireNonNull(value);
        this.value = value;
        this.queryParameters = queryParameters;
    }

    public static RequestUri from(final String uri) {
        if (uri.contains(URI_QUERY_DELIMITER)) {
            return parseRequestUriWithQueryParam(uri);
        }
        return parseRequestUri(uri);
    }

    private static RequestUri parseRequestUriWithQueryParam(final String uri) {
        String[] uriValues = uri.split(URI_QUERY_REGEX);

        String uriValue = uriValues[URI_INDEX];
        Map<String, String> queryParameters = parseQueryParameter(uriValues[QUERY_PARAM_INDEX]);

        return new RequestUri(uriValue, queryParameters);
    }

    private static Map<String, String> parseQueryParameter(final String queryParam) {
        Map<String, String> queryParams = new HashMap<>();
        String[] queryParameterValues = queryParam.split(QUERY_PARAMS_DELIMITER);
        for (String queryParameterValue : queryParameterValues) {
            String[] param = queryParameterValue.split(QUERY_PARAM_DELIMITER);
            queryParams.put(param[0], param[1]);
        }

        return queryParams;
    }

    private static RequestUri parseRequestUri(final String uri) {
        // Todo : 해당 부분 리팩토링 필요
        if (uri.equals("/index.html")) {
            return new RequestUri("/index.html", EMPTY_PARAMETERS);
        }

        return new RequestUri(uri, EMPTY_PARAMETERS);
    }

    public String getValue() {
        return value;
    }

    public String getQueryParameter(String parameter) {
        return queryParameters.get(parameter);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
