package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.PathInfo;
import org.apache.coyote.http11.component.FileExtension;

public class HttpRequestUriParser {

    private static final String ROOT_PATH = "/";
    private static final String INDEX_FILE = "/index";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_DELIMITER = "=";

    private HttpRequestUriParser() {
    }

    public static HttpRequestUri parse(String requestUri) {
        int queryDelimiterIndex = requestUri.indexOf(QUERY_DELIMITER);
        if (queryDelimiterIndex == -1) {
            return new HttpRequestUri(getFileAccessor(requestUri));
        }
        String path = requestUri.substring(0, queryDelimiterIndex);
        String query = requestUri.substring(queryDelimiterIndex + 1);
        return new HttpRequestUri(getFileAccessor(path), getParams(query));
    }

    private static Map<String, String> getParams(String query) {
        String[] queryParams = query.split(QUERY_PARAMETER_DELIMITER);
        return Arrays.stream(queryParams)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(split -> split[0], split -> split[1]));
    }

    private static PathInfo getFileAccessor(String path) {
        int extensionIndex = path.lastIndexOf(EXTENSION_DELIMITER);
        if (path.equals(ROOT_PATH)) {
            return new PathInfo(INDEX_FILE, FileExtension.HTML);
        }
        if (extensionIndex == path.length() - 1) {
            throw new UncheckedHttpException(new IllegalArgumentException("파일 경로는 .으로 끝날 수 없습니다."));
        }

        if (extensionIndex == -1) {
            return new PathInfo(path, FileExtension.HTML);
        }
        String fileName = path.substring(0, extensionIndex);
        String extension = path.substring(extensionIndex);
        FileExtension fileExtension = FileExtension.from(extension);
        return new PathInfo(fileName, fileExtension);
    }
}
