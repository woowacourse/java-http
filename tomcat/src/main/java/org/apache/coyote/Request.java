package org.apache.coyote;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {

    private static final String START_LINE_DELIMITER = " ";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int NO_SPLIT_SIGN = 1;
    private static final String DEFAULT_REQUEST_EXTENSION = "strings";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";

    private String method;
    private String requestUrl;
    private String version;

    public Request(final String method, final String requestUrl, final String version) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public static Request from(final String requestLine) {
        String[] line = requestLine.split(START_LINE_DELIMITER);
        if (line.length != REQUEST_LINE_LENGTH) {
            throw new HttpRequestStartLineNotValidException();
        }
        return new Request(line[0], line[1], line[2]);
    }

    public String getRequestExtension() {
        String[] nameExtension = splitRequestUrlWith(EXTENSION_DELIMITER);
        if (nameExtension.length == NO_SPLIT_SIGN) {
            return DEFAULT_REQUEST_EXTENSION;
        }
        return nameExtension[1];
    }

    public Map<String, String> getQueryParameters() {
        String[] requestSplit = requestUrl.split(QUERY_PARAMETER_DELIMITER);
        if (requestSplit.length == NO_SPLIT_SIGN) {
            return Collections.emptyMap();
        }
        String queryParameters = requestSplit[1];
        return Arrays.stream(queryParameters.split("&"))
                .collect(Collectors.toMap(parameter -> getSplitValue(parameter, "=", 0),
                        parameter -> getSplitValue(parameter, "=", 1)));
    }

    private String getSplitValue(final String content, final String delimiter, final int index) {
        return content.split(delimiter)[index];
    }

    public boolean isFileRequest() {
        return splitRequestUrlWith(EXTENSION_DELIMITER).length != NO_SPLIT_SIGN;
    }

    public boolean isSameRequestUrl(final String url) {
        return splitRequestUrlWith(QUERY_PARAMETER_DELIMITER)[0].equals(url);
    }

    private String[] splitRequestUrlWith(final String delimiter) {
        return requestUrl.split(delimiter);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }
}
