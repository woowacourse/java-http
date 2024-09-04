package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final String content;

    public HttpRequest(String content) {//TODO byte[]로 받기
        this.content = content;
    }

    public String getTarget() { //TODO 따로 빼야하나?
        String[] startLine = getStartLine().split(" ");
        return startLine[1];
    }

    public String getPath() {
        String target = getTarget();
        int i = target.lastIndexOf('?');
        if (i == -1) {
            return target; // login.html, login // login.css
        }
        return target.substring(0, i);
    }

    public Map<String, String> getParams() {
        Map<String, String> map = new LinkedHashMap<>();
        String target = getTarget();
        int i = target.indexOf('?');
        if (i == -1) {
            return map;
        }

        String query = target.substring(i + 1);
        String[] params = query.split("&");
        for (String param : params) {
            String[] split = param.split("=");
            String key = split[0];
            String value = split[1];
            map.put(key, value);
        }

        return map;
    }

    private String getStartLine() {
        return content.split("\n")[0];
    }
}
