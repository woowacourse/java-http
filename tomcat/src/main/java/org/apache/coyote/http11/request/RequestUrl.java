package org.apache.coyote.http11.request;

import java.util.HashMap;

public class RequestUrl {

    private final String url;
    private final HashMap<String, String> params = new HashMap<>();

    public RequestUrl(String url) {
        String[] urlSplit = url.split("\\?");
        this.url = urlSplit[0];
        if (urlSplit.length > 1) {
            String[] paramsSplit = urlSplit[1].split("&");
            for (String param : paramsSplit) {
                String[] keyValue = param.split("=");
                params.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public String getUrl() {
        return url;
    }
}
