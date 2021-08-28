package nextstep.mockweb.request;

import java.util.Map;
import nextstep.jwp.request.HttpMethod;

public class RequestInfo {

    private static final String ENTER = "\r\n";

    private HttpMethod httpMethod;
    private String url;
    private Object body;
    private HeaderInfo headerInfo;

    public RequestInfo(HttpMethod httpMethod, String url, Object body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.body = body;
        this.headerInfo = new HeaderInfo();
    }

    public String asRequest() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeRequestLine());
        stringBuilder.append(makeRequestHeader());
        stringBuilder.append(ENTER);
        stringBuilder.append(makeRequestBody());
        return stringBuilder.toString();
    }

    private String makeRequestLine() {
        final String requestLine = String.format("%s %s HTTP/1.1 %s", httpMethod.name(), url, ENTER);
        return requestLine;
    }

    private String makeRequestHeader() {
        return headerInfo.asRequestForm();
    }

    private String makeRequestBody() {
        if(body == null) {
            return "";
        }
        // TODO : Converter 기능 추가
        return (String) body;
    }

    public void addHeader(Map<String, String> headers) {
        headers.forEach((key,value) -> headerInfo.addHeader(key, value));
    }
}
