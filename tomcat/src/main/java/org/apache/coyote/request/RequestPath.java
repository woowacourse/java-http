package org.apache.coyote.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.http.InvalidRequestPathException;

public class RequestPath {

    private static final String PARAMETER_START_DELIMITER = "\\?";
    private static final String PARAMETER_COMPONENT_DELIMITER = "&";
    private static final String PARAMETER_DELIMITER = "=";
    private static final int PARAMETER_EXIST_PATH_LENGTH = 2;
    private static final int ELEMENT_COUNT = 2;

    private final String fullPath;
    private final String uriPath;
    private final Map<String, String> parameters;

    private RequestPath(final String fullPath, final String uriPath, final Map<String, String> parameters) {
        this.fullPath = fullPath;
        this.uriPath = uriPath;
        this.parameters = parameters;
    }

    public static RequestPath from(final String path) {
        if (path == null || path.isEmpty()) {
            throw new InvalidRequestPathException("Path is Null Or Empty");
        }
        final String[] paths = path.split(PARAMETER_START_DELIMITER);
        final Map<String, String> parameters = getParameters(paths);
        return new RequestPath(path, paths[0], parameters);
    }

    private static Map<String, String> getParameters(final String[] paths) {
        if (paths.length == PARAMETER_EXIST_PATH_LENGTH) {
            return Arrays.stream(paths[1].split(PARAMETER_COMPONENT_DELIMITER))
                    .map(pathParameter -> {
                        final String[] parameter = pathParameter.split(PARAMETER_DELIMITER);
                        if (parameter.length != ELEMENT_COUNT) {
                            throw new InvalidRequestPathException("Parameter Element Count Not Match");
                        }
                        return parameter;
                    })
                    .collect(Collectors.toMap(
                            parameter -> parameter[0],
                            parameter -> parameter[1],
                            (exist, replace) -> replace
                    ));
        }
        return new HashMap<>();
    }

    public String getUriPath() {
        return uriPath;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}
