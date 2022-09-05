package org.apache.coyote.support;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import support.IoUtils;
import support.StringUtils;

public class HttpRequest {
    private boolean isEmptyRequest = true; // 공백 등 무의미한 요청
    private String httpMethod = "";
    private String uri = "";
    private String postContent = ""; // only post request
    private String[] headers;

    /**
     * int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
     * <P />
     * char[] buffer = new char[contentLength];
     * <P />
     * reader.read(buffer, 0, contentLength);
     * <P />
     * String requestBody = new String(buffer);
     */
    public HttpRequest(final InputStream inputStream) {
        final String[] httpRequestFullString = IoUtils.readLines(inputStream);
        if (StringUtils.isEmpty(httpRequestFullString)) {
            return;
        }
        isEmptyRequest = false;
        headers = httpRequestFullString;
        final String headerFirstLines = httpRequestFullString[0];
        final String[] split = headerFirstLines.split(" ");
        httpMethod = split[0];
        uri = split[1];
        if (HttpMethod.POST.equalsIgnoreCase(httpMethod)) {
//            postContent = findPostContent(httpRequestStringLines);
            postContent = headers[headers.length - 1].replaceAll("\\s+", "");
        }
    }

    /**
     * TODO 아래의 내용을 파싱해야함
     *
     * @
     * %40
     *
     * !
     * %21
     *
     */
    private String findPostContent(final String[] httpRequestStringLines) {
        return Arrays.stream(httpRequestStringLines)
                .filter(it -> it.contains("JSESSIONID"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not found JSESSIONID "))
                .split("\\s+")
                [1];
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getPostContent() {
        return postContent;
    }

    public boolean isEmpty() {
        return isEmptyRequest;
    }

    public boolean isSame(String httpMethod, String uri) {
        return Objects.equals(this.httpMethod, httpMethod) && Objects.equals(this.uri, uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "isEmptyRequest=" + isEmptyRequest +
                ", httpMethod='" + httpMethod + '\'' +
                ", uri='" + uri + '\'' +
                ", postContent='" + postContent + '\'' +
                ", headers=" + Arrays.toString(headers) +
                '}';
    }
}
