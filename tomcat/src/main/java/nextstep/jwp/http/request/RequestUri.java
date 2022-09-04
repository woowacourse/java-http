package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestUri {

    private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<>();

    private static final String REQUEST_PARAM_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_VALUE_DELIMITER = "=";
    private static final int URI_INDEX = 0;
    private static final int QUERY_PARAMETER_INDEX = 1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXTENSION_QUERY_PARAMETER_VALUE = 2;

    private final String value;
    private final Map<String, String> queryParameters;

    private RequestUri(final String value, final Map<String, String> queryParameters) {
        Objects.requireNonNull(value);
        this.value = value;
        this.queryParameters = queryParameters;
    }

    public static RequestUri create(final String uri) {
        String[] parseValues = uri.split(REQUEST_PARAM_DELIMITER);

        String uriValue = parseValues[URI_INDEX];
        Map<String, String> queryParameterValue = createConditionQueryParameter(parseValues);

        return new RequestUri(uriValue, queryParameterValue);
    }

    private static Map<String, String> createConditionQueryParameter(final String[] parseValues) {
        if (parseValues.length == EXTENSION_QUERY_PARAMETER_VALUE) {
            String queryParamValue = parseValues[QUERY_PARAMETER_INDEX];
            return parseQueryParameter(queryParamValue);
        }
        return EMPTY_PARAMETERS;
    }

    private static Map<String, String> parseQueryParameter(final String queryParamValue) {
        Map<String, String> queryParams = new HashMap<>();
        String[] parseQueryParameterValues = queryParamValue.split(QUERY_PARAMETER_DELIMITER);
        for (String parseQueryParameterValue : parseQueryParameterValues) {
            String[] param = parseQueryParameterValue.split(QUERY_PARAMETER_VALUE_DELIMITER);
            queryParams.put(param[KEY_INDEX], param[VALUE_INDEX]);
        }

        return queryParams;
    }

    public String getUri() {
        return value;
    }

    public String getQueryParameterValue(final String parameter) {
        return queryParameters.get(parameter);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
