package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestMethod method;
    private String path;
    private final Map<String, String> queryParams;
    private final HttpCookie httpCookie;
    private final Map<String, String> headers;
    private final Map<String, String> formParams;
    private Session session;

    public HttpRequest(
            HttpRequestMethod method,
            String path,
            Map<String, String> queryParams,
            Map<String, String> headers,
            HttpCookie httpCookie,
            Map<String, String> formParams
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.httpCookie = httpCookie;
        this.formParams = formParams;
        findSession();
    }

    private void findSession() {
        String jsessionid = httpCookie.getCookie("JSESSIONID");
        this.session = SessionManager.findSession(jsessionid);
    }

    public Session getSession(boolean create) {
        if (this.session != null) {
            return this.session;
        }

        if (create) {
            this.session = SessionManager.createSession();
            return this.session;
        }
        return null;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public String getFormParam(String key) {
        return formParams.get(key);
    }

    public boolean hasMethod(HttpRequestMethod method) {
        return this.method == method;
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public boolean endsWith(String type) {
        return path.endsWith(type);
    }
}
