package org.apache.coyote.http;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String QUERY_DELIMETER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String URL_DELIMITER = "?";

    private final HttpRequestLine httpRequestLine;
    private final Header header;
    private final String body;
    private final Session session;


    public HttpRequest(final HttpRequestLine httpRequestLine, Header header, String body) {
        this.httpRequestLine = httpRequestLine;
        this.header = header;
        this.body = body;
        this.session = SessionManager.add()
                .orElseThrow(() -> new NoSuchElementException("세션이 정상적으로 저장되지 않았습니다."));
    }

    public Optional<String> findQueryByKey(String queryKey){
        if(hasQueryInUri(getFullUri())){
            return findValue(queryKey, getFullUri());
        }

        if(isFormUrlEncoded()){
            return findValue(queryKey, body);
        }

        throw new NoSuchElementException("요청내에 QueryString이 존재하지 않습니다.");
    }

    private boolean isFormUrlEncoded() {
        final String contentType = header.getHeaderMap().get("Content-Type");
        return contentType!= null && contentType.equals("application/x-www-form-urlencoded");
    }


    private boolean hasQueryInUri(final String uri) {
        return uri.contains(URL_DELIMITER);
    }

    private Optional<String> findValue(String queryKey, String target){
        final Map<String, String> queryMap = makeQueryMap(target);

        final String queryValue = queryMap.get(queryKey);
        return Optional.ofNullable(queryValue);
    }

    private Map<String, String> makeQueryMap(final String target) {
        final Integer index = target.indexOf(URL_DELIMITER);
        final String queryString = target.substring(index + 1);
        final String[] s = queryString.split(QUERY_DELIMETER);
        return Arrays.stream(s)
                .map(q -> q.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getFullUri(){
        return httpRequestLine.getUri();
    }

    public String getUri() {
        Integer index = getFullUri().indexOf(URL_DELIMITER);
        if (hasQueryInUri(getFullUri())) {
            return getFullUri().substring(0, index);
        }
        return getFullUri();
    }

    public Header getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public Session getSession() {
        return session;
    }

    public HttpCookie getCookie(){
        return header.getCookie();
    }
}
