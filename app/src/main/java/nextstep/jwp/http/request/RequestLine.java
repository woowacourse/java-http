package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import nextstep.jwp.exception.InvalidHttpRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> queryParams;
    private String protocol;

    public RequestLine(String line) {
        try {
            String[] tokens = splitByBlank(line);
            this.method = HttpMethod.of(tokens[0]);
            String uri = tokens[1];
            if (uri.contains("?")) {
                final int index = uri.indexOf("?");
                this.path = uri.substring(0, index);
                this.queryParams = extractQueryParams(uri.substring(index + 1));
            } else {
                path = uri;
            }
            this.protocol = tokens[2];
        } catch (InvalidHttpRequestException exception) {
            log.error("Exception invalid http request", exception);
        }
    }

    private String[] splitByBlank(String line) throws InvalidHttpRequestException {
        if (line == null) {
            throw new InvalidHttpRequestException("HTTP Request Line이 null일 수 없습니다.");
        }
        String[] tokens = line.split(" ");
        if (tokens.length != 3) {
            throw new InvalidHttpRequestException("RequestLine의 형식이 올바르지 않습니다.");
        }
        return tokens;
    }

    private Map<String, String> extractQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] tokens = queryString.split("&");
        for (String token : tokens) {
            if (Strings.isNullOrEmpty(token)) {
                continue;
            }
            String[] tmp = token.split("=");
            if (tmp.length == 2) {
                params.put(tmp[0], tmp[1]);
            }
        }
        return params;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isGet() {
        return HttpMethod.GET == method;
    }

    public boolean isPost() {
        return HttpMethod.POST == method;
    }
}
