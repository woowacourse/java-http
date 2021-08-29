package nextstep.jwp.request;

import java.util.Map;
import java.util.Objects;

public class ClientRequest {

    private static final String REQUEST_SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final RequestUrl requestUrl;
    private final RequestHttpHeader requestHttpHeader;
    private final RequestBody requestBody;

    private ClientRequest(HttpMethod httpMethod, RequestUrl requestUrl, RequestHttpHeader requestHttpHeader, RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.requestHttpHeader = requestHttpHeader;
        this.requestBody = requestBody;
    }

    public static ClientRequest of(HttpMethod httpMethod, String url) {
        return new ClientRequest(httpMethod, RequestUrl.of(url), null, null);
    }

    public static ClientRequest from(String requestInfo, Map<String, String> requestHttpHeader, String requestBody) {
        final String[] requestInfos = requestInfo.split(REQUEST_SEPARATOR);
        return new ClientRequest(
                HttpMethod.of(requestInfos[0]),
                RequestUrl.of(requestInfos[1]),
                RequestHttpHeader.of(requestHttpHeader),
                RequestBody.of(requestBody));
    }

    public RequestHttpHeader getRequestHttpHeader() {
        return requestHttpHeader;
    }

    public String searchRequestBody(String key) {
        return requestBody.find(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRequest that = (ClientRequest) o;
        return Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(requestUrl, that.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUrl);
    }
}
