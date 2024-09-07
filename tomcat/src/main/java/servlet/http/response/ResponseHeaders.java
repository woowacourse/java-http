package servlet.http.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import servlet.http.HttpHeader;

public class ResponseHeaders implements Assemblable {

    private final Map<String, String> headers;

    private final ResponseCookie responseCookie;

    protected ResponseHeaders() {
        this(new LinkedHashMap<>(), new ResponseCookie());
    }

    private ResponseHeaders(Map<String, String> headers, ResponseCookie responseCookie) {
        this.headers = headers;
        this.responseCookie = responseCookie;
    }

    protected void contentType(String contentType) {
        headers.put(HttpHeader.CONTENT_TYPE.value(), "%s;charset=utf-8".formatted(contentType));
    }

    protected void contentLength(int contentLength) {
        headers.put(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(contentLength));
    }

    protected void location(String location) {
        headers.put(HttpHeader.LOCATION.value(), location);
    }

    protected void setJsessionid(String jsessionid) {
        responseCookie.setJsessionid(jsessionid);
    }

    @Override
    public void assemble(StringBuilder builder) {
        responseCookie.assemble(builder);
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(convert(entry));
        }
        builder.append("\r\n");
    }

    private String convert(Entry<String, String> entry) {
        return "%s: %s \r\n".formatted(entry.getKey(), entry.getValue());
    }
}
