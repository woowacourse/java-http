package nextstep.org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpUtil {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX = 0;
    private static final String RESOURCES_PATH_PREFIX = "static";

    private HttpUtil() {
    }

    public static void parseMultipleValues(
            Map<String, String> parsedValues,
            String multipleValues,
            String valuesDelimiter, String keyValueDelimiter
    ) {
        Arrays.asList(multipleValues.split(valuesDelimiter)).forEach(header -> {
            String[] splited = header.split(keyValueDelimiter);
            parsedValues.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
        });
    }

    public static String selectFirstContentTypeOrDefault(String acceptHeader) {
        if (Objects.isNull(acceptHeader)) {
            return "text/html";
        }
        List<String> acceptHeaderValues = Arrays.asList(acceptHeader.split(","));
        return acceptHeaderValues.get(ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX);
    }

    public static Optional<String> createResponseBody(String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return Optional.of("Hello world!");
        }

        String resourceName = preprocessRequestPath(requestPath);
        URL resource = HttpUtil.class.getClassLoader().getResource(resourceName);
        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    private static String preprocessRequestPath(String requestPath) {
        String resourceName = RESOURCES_PATH_PREFIX + requestPath;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        return resourceName;
    }
}
