package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

public class HttpUri {
    private final String uri;
    private final String totalUri;
    private final Map<String, String> queryParams = new HashMap<>();

    public HttpUri(String uri) {
        this.totalUri = uri;
        if (uri.contains("?")) {
            String[] querySplit = uri.split("\\?");
            this.uri = querySplit[0];
            String[] params = querySplit[1].split("&");

            for (String keyValue : params) {
                String[] param = keyValue.split("=");
                queryParams.put(param[0], param[1]);
            }
            return;
        }
        this.uri = uri;
    }

    public String getTotalUri() {
        return totalUri;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestUri() {
        return uri;
    }
}
