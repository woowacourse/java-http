package nextstep.jwp.model.http_request;

import java.util.Map;

public class JwpGetHttpRequest extends JwpHttpRequest{

    public JwpGetHttpRequest(String method, String uri, String httpVersion, Map<String, String> headers, Map<String, String> params) {
        super(method, uri, httpVersion, headers, params);
    }


}
