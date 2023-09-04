package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.coyote.http.util.HttpConsts;

public class Url {

    private static final String STATIC_RESOURCE_EXTENSION_SEPARATOR = ".";
    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final int INVALID_QUERY_PARAMETER_INDEX = -1;
    private static final int VALID_PATH_TOKEN_LENGTH = 2;

    private final String path;
    private final List<String> hierarchy;
    private final String resourceName;

    private Url(
            final String path,
            final List<String> hierarchy,
            final String resourceName
    ) {
        this.path = path;
        this.hierarchy = hierarchy;
        this.resourceName = resourceName;
    }

    public static Url from(final String url) {
        final int queryParameterDelimiterIndex = url.indexOf(QUERY_PARAMETER_DELIMITER);
        final String path = findPath(url, queryParameterDelimiterIndex);
        final String[] urlTokens = url.split(HttpConsts.SLASH);

        if (urlTokens.length == 0) {
            return new Url(url, Collections.emptyList(), HttpConsts.BLANK);
        }

        final String resourceName = urlTokens[urlTokens.length - 1];
        final List<String> hierarchy = Arrays.asList(Arrays.copyOf(urlTokens, urlTokens.length - 1));

        return new Url(path, hierarchy, resourceName);
    }

    private static String findPath(final String url, final int queryParameterDelimiterIndex) {
        if (queryParameterDelimiterIndex == INVALID_QUERY_PARAMETER_INDEX) {
            return url;
        }

        return url.substring(0, queryParameterDelimiterIndex);
    }

    public boolean matchesByPath(final String targetPath, final String rootContextPath) {
        if (HttpConsts.SLASH.equals(rootContextPath)) {
            return path.equals(targetPath);
        }

        final String[] pathTokens = path.split(rootContextPath);

        if (pathTokens.length < VALID_PATH_TOKEN_LENGTH) {
            return false;
        }

        return targetPath.equals(pathTokens[pathTokens.length - 1]);
    }

    public boolean isWelcomePageUrl(final String rootContextPath) {
        return path.equals(rootContextPath);
    }

    public boolean isStaticResource() {
        if (resourceName.isEmpty() || resourceName.isBlank()) {
            return false;
        }

        return resourceName.contains(STATIC_RESOURCE_EXTENSION_SEPARATOR);
    }

    public boolean startsWithRootContextPath(final String rootContextPath) {
        return path.startsWith(rootContextPath);
    }
}
