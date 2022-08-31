package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Request {

    private static final String DEFAULT_PATH = "/index";

    private final Map<String, String> queryMap;
    private String path;
    private HttpStatusCode httpStatusCode;

    public Request(String uri, HttpStatusCode httpStatusCode){
        this.httpStatusCode = httpStatusCode;
        path = uri;
        queryMap = new HashMap<>();

        if (uri.contains("?")) {
            separateQueryParams(uri);
        }

        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }

        if (!path.contains(".")) {
            path = path + ".html";
        }
    }

    public Request(String uri) {
        this(uri, HttpStatusCode.HTTP_STATUS_OK);
    }

    private void separateQueryParams(String uri) {
        int index = uri.indexOf("?");
        path = uri.substring(0, index);

        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] queryEntry = query.split("=");
            queryMap.put(queryEntry[0], queryEntry[1]);
        }
    }

    public boolean isValidLoginRequest() {
        return path.startsWith("/login")
            && queryMap.keySet().containsAll(List.of("account", "password"));
    }

    public String getQueryParam(String paramName) {
        return queryMap.get(paramName);
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
}
