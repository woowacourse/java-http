package org.apache.coyote;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;

public class Response {

    private String protocolVersion;

    private Map<String, String> headers;    // 쿠키는 별도로 관리

    private CookieManager cookieManager = new CookieManager();

    private String body = "";

    private HttpStatusCode httpStatusCode;

    public Response() {
        headers = new LinkedHashMap<>();
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void addHeader(String key, String val) {
        headers.put(key, val);
    }

    public void addCookie(String key, String val) {
        cookieManager.addCookie(key, val);
    }

    public Map<String, String> getCookieMap() {
        return cookieManager.getCookieMap();
    }

    public int getCookieMapSize(){
        return cookieManager.getCookieMap().size();
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getStatusCode() {
        return String.valueOf(httpStatusCode.getCode());
    }

    public String getStatusMessage() {
        return httpStatusCode.getMessage();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getContentLength() {
        return String.valueOf(body.getBytes().length);
    }

}
