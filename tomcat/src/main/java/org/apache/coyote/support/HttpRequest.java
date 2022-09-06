package org.apache.coyote.support;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import support.IoUtils;
import support.StringUtils;

public class HttpRequest {
    private boolean isEmptyRequest = true; // 공백 등 무의미한 요청
    private String httpMethod = "";
    private String uri = "";
    private Headers headers;
    private String postContent = ""; // only post request


    public HttpRequest(final BufferedReader reader) {
        final String firstLineHeader = IoUtils.readLine(reader);
        if (StringUtils.isEmpty(firstLineHeader)) {
            return;
        }
        isEmptyRequest = false;
        final String[] split = firstLineHeader.split(" ");
        httpMethod = split[0];
        uri = split[1];
        headers = new Headers(reader);
        if (HttpMethod.POST.equalsIgnoreCase(httpMethod)) {
//            postContent = findPostContent(httpRequestStringLines);
//            postContent = headers[headers.length - 1].replaceAll("\\s+", "");
            postContent = headers.findPostContent();
        }
    }

    /**
     * TODO 아래의 내용을 파싱해야함
     * @   %40
     * !   %21
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
                ", headers=" + headers +
                ", postContent='" + postContent + '\'' +
                '}';
    }
}
