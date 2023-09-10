package org.apache.coyote.request;

import static org.apache.coyote.utils.Constant.EMPTY;
import static org.apache.coyote.utils.Converter.parseFormData;

import java.util.Map;

public class Uri {
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;

    private final String path;
    private final Map<String, String> queryParams;

    private Uri(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static Uri from(final String uri) {
        final String[] uris = uri.split(QUERY_STRING_DELIMITER);
        final String path = uris[PATH_INDEX];

        String queryString = EMPTY;
        if (uris.length > 1) {
            queryString = uris[QUERY_STRING_INDEX];
        }
        final Map<String, String> queryParams = parseFormData(queryString);

        return new Uri(path, queryParams);
    }

    public boolean isQueryParamExist(final String... parameterNames) {
        boolean isExist = true;
        for (final String parameterName : parameterNames) {
            isExist = isExist && queryParams.containsKey(parameterName);
        }
        return isExist;
    }

    public String getQueryParam(final String parameterName) {
        return queryParams.get(parameterName);
    }

    public String getPath() {
        return path;
    }
}
