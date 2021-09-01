package nextstep.jwp.model.web.request;

import nextstep.jwp.model.web.HttpMethod;

import java.util.Map;

public class RequestLine {

    private final HttpMethod method;
    private final String uri;
    private final String versionOfProtocol;
    private final RequestParams requestParams;

    public RequestLine(String requestLine) {
        String[] infos = requestLine.split(" ");
        this.method = HttpMethod.findMethod(infos[0]);
        this.uri = parseUri(infos[1]);
        this.versionOfProtocol = infos[2];
        this.requestParams = parseRequestParams(infos[1]);
    }

    private String parseUri(String uri) {
        return uri.split("\\?")[0];
    }

    private RequestParams parseRequestParams(String uri) {
        RequestParams params = new RequestParams();

        if (uri.contains("?")) {
            for (String keyAndValue : uri.split("\\?")[1].split("&")) {
                String[] kV = keyAndValue.split("=");
                params.add(kV[0], kV[1]);
            }
        }
        return params;
    }

    public String getUri() {
        return uri;
    }

    public String getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public Map<String, String> getRequestParams() {
        return requestParams.getParams();
    }
}
