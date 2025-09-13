package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Request {

    private String method;
    private String path;
    private String targetResource;
    private String protocol;
    private String body = "";
    private Map<String, String> headers = new HashMap<>();
    private final Map<String, Http11Cookie> cookies = new HashMap<>();


    public Http11Request(String method, String targetResource, String protocol, Map<String, String> headers, String body) {
        this.method = method;
        this.targetResource = targetResource;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
        this.path = parsePath(targetResource);

        String cookieHeader = headers.get("Cookie");
        if (cookieHeader != null) {
            this.cookies.putAll(Http11Cookie.parse(cookieHeader));
        }
    }

    public Optional<String> getSession(final String sessionId) {
        return getCookie(sessionId)
                .map(Http11Cookie::getValue);
    }

    private String parsePath(final String targetResource) {
        if(targetResource.contains("?")) {
            return targetResource.substring(1, targetResource.indexOf("?"));
        }

        return targetResource;
    }

    public Map<String, String> parseBody() {
        HashMap<String, String> bodyMap = new HashMap<>();

        if (body == null || body.isEmpty()) {
            return bodyMap;
        }

        String[] infos = body.split("&");
        for (String info : infos) {
            String[] parsedInfo = info.split("=");
            if (parsedInfo.length == 2) {
                bodyMap.put(parsedInfo[0], parsedInfo[1]);
            }
        }

        return bodyMap;
    }

    public Optional<Http11Cookie> getCookie(String name) {
        Http11Cookie http11Cookie = cookies.get(name);

        return Optional.ofNullable(http11Cookie);
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }


    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getTargetResource() {
        return targetResource;
    }

    public String getBody(){
        return body;
    }
}
