package org.apache.http.request;

import java.util.Objects;

import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;

public class RequestLine {

    private static final String REQUEST_LINE_PART_DELIMITER = " ";
    private static final int REQUEST_LINE_PART_SIZE = 3;
    private static final int HTTP_METHOD_PART_ORDER = 0;
    private static final int HTTP_PATH_PART_ORDER = 1;
    private static final int HTTP_VERSION_PART_ORDER = 2;

    private final HttpMethod method;
    private final String path;
    private final HttpVersion version;

    public RequestLine(HttpMethod method, String path, HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public RequestLine(String method, String path, String version) {
        this.method = HttpMethod.valueOf(method);
        this.path = path;
        this.version = HttpVersion.getHttpVersion(version);
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineParts = requestLine.split(REQUEST_LINE_PART_DELIMITER);
        checkRequestLinePartSize(requestLineParts.length);

        return new RequestLine(
                requestLineParts[HTTP_METHOD_PART_ORDER],
                requestLineParts[HTTP_PATH_PART_ORDER],
                requestLineParts[HTTP_VERSION_PART_ORDER]
        );
    }

    private static void checkRequestLinePartSize(int requestLinePartsSize) {
        if (requestLinePartsSize != REQUEST_LINE_PART_SIZE) {
            throw new IllegalArgumentException("RequestLine 형식이 맞지 않습니다. method, path, version으로 구성해주세요.");
        }
    }

    public boolean hasSameMethod(HttpMethod httpMethod) {
        return this.method == httpMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(path, that.path) && version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, version);
    }

    @Override
    public String toString() {
        return String.join(REQUEST_LINE_PART_DELIMITER, method.toString(), path, version.getValue());
    }
}
