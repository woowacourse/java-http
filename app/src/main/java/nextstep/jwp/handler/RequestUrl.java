package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

public class RequestUrl {
    private final String url;
    private final String path;
    private final Map<String, String> queryParams = new HashMap<>();

    public RequestUrl(String url) {
        this.url = url;
        if (url.contains("?")) {
            String[] querySplit = url.split("\\?");
            this.path = querySplit[0];
            String[] params = querySplit[1].split("&");

            for (String keyValue : params) {
                String[] param = keyValue.split("=");
                queryParams.put(param[0], param[1]);
            }
            return;
        }
        this.path = url;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }
}
