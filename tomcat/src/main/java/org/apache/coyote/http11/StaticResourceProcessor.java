package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceProcessor {
    
    public static final String STATIC_RESOURCE_PATH = "static";
    public static final String HEADER_DELIMITER = "\\s+";
    public static final String QUERY_PARAM_STARTER = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public static ResourceResolutionResult resolveResourcePath(String requestLine) {
        String[] splitRequestLine = requestLine.split(HEADER_DELIMITER);
        String requestUri = splitRequestLine[1];

        int queryParamIndex = requestUri.indexOf(QUERY_PARAM_STARTER);
        String resource = requestUri;
        Map<String, String> queryParams = null;
        
        if (queryParamIndex != -1) {
            resource = requestUri.substring(0, queryParamIndex);
            String queryString = requestUri.substring(queryParamIndex + 1);
            queryParams = parseQueryString(queryString);
        }

        if (hasNoExtension(resource)) {
            if (resource.equals("/")) {
                resource = "/index.html";
            } else {
                resource += HTML_EXTENSION;
            }
        }
        return ResourceResolutionResult.of(STATIC_RESOURCE_PATH + resource, queryParams);
    }

    private static boolean hasNoExtension(String resource) {
        int lastDotIndex = resource.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == resource.length() - 1) {
            return true;
        }
        return false;
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] paramTokens = queryString.split(PARAM_SEPARATOR);
        for (String paramToken : paramTokens) {
            String[] keyValue = paramToken.split(KEY_VALUE_SEPARATOR);
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }
}
