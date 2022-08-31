package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class HttpRequest {

    private static final String DEFAULT_PATH = "/index";
    private final HttpMethod httpMethod;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> queryParams;
    private HttpCookie cookie;
    private String path;

    public HttpRequest(BufferedReader bufferedReader, HttpStatusCode httpStatusCode) throws IOException {
        String requestLine = bufferedReader.readLine();
        httpRequestHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] headerEntity = line.replaceAll(" ", "").split(":");
            httpRequestHeaders.put(headerEntity[0], headerEntity[1]);
            line = bufferedReader.readLine();
        }

        StringTokenizer requestTokens = new StringTokenizer(requestLine);
        this.httpStatusCode = httpStatusCode;
        this.httpMethod = HttpMethod.of(requestTokens.nextToken());
        path = requestTokens.nextToken();
        formatPath();
        queryParams = new HashMap<>();
        cookie = new HttpCookie();

        if (isPostRequest()) {
            int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String[] userInfos = new String(buffer).split("&");
            for (String userInfo : userInfos) {
                String[] infoEntity = userInfo.split("=");
                queryParams.put(infoEntity[0], infoEntity[1]);
            }
        }
    }

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this(bufferedReader, HttpStatusCode.HTTP_STATUS_OK);
    }

    private HttpRequest(HttpMethod httpMethod, String uri, HttpStatusCode httpStatusCode) {
        this.httpMethod = httpMethod;
        this.httpStatusCode = httpStatusCode;
        httpRequestHeaders = new HashMap<>();
        queryParams = new HashMap<>();
        cookie = new HttpCookie();
        path = uri;
        formatPath();
    }

    public static HttpRequest of(HttpMethod httpMethod, String uri, HttpStatusCode httpStatusCode) {
        return new HttpRequest(httpMethod, uri, httpStatusCode);
    }

    private void formatPath() {
        if (path.contains("?")) {
            separateQueryParams(path);
        }

        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }

        if (!path.contains(".")) {
            path = path + ".html";
        }
    }

    private void separateQueryParams(String uri) {
        int index = uri.indexOf("?");
        path = uri.substring(0, index);

        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] queryEntry = query.split("=");
            queryParams.put(queryEntry[0], queryEntry[1]);
        }
    }

    public String getQueryParam(String paramName) {
        return queryParams.get(paramName);
    }

    public String getStaticPath() {
        return "static" + path;
    }

    public String getContentType() {
        String extension = StringUtils.substringAfterLast(path, ".");

        if (extension.equals("ico")) {
            return "image/apng";
        }
        return "text/" + extension;
    }

    public String getHttpStatusCode() {
        return httpStatusCode.toString();
    }

    public boolean isPostRequest() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public boolean isValidLoginRequest() {
        return isPostRequest() && path.startsWith("/login");
    }

    public boolean isValidRegisterRequest() {
        return isPostRequest() && path.startsWith("/register");
    }

    public void addCookie(String key, String value) {
        cookie.addCookie(key, value);
    }

    public String getCookie() {
        return cookie.toString();
    }
}
