package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestUri {

    private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<>();
    private static final String EXTENSION_DELIMITER = ".";

    private final String value;
    private final Map<String, String> queryParameters;

    private RequestUri(final String value, final Map<String, String> queryParameters) {
        Objects.requireNonNull(value);
        this.value = value;
        this.queryParameters = queryParameters;
    }

    public static RequestUri create(final String uri) {
        String[] parseValues = uri.split("\\?");

        String uriValue = parseValues[0];
        Map<String, String> queryParameterValue = createConditionQueryParameter(parseValues);

        return new RequestUri(uriValue, queryParameterValue);
    }

    private static Map<String, String> createConditionQueryParameter(final String[] parseValues) {
        if (parseValues.length == 2) {
            String queryParamValue = parseValues[1];
            return parseQueryParameter(queryParamValue);
        }
        return EMPTY_PARAMETERS;
    }

    private static Map<String, String> parseQueryParameter(final String queryParamValue) {
        Map<String, String> queryParams = new HashMap<>();
        String[] parseQueryParameterValues = queryParamValue.split("&");
        for (String parseQueryParameterValue : parseQueryParameterValues) {
            String[] param = parseQueryParameterValue.split("=");
            queryParams.put(param[0], param[1]);
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
