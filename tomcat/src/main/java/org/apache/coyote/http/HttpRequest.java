package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String QUERY_DELIMETER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String URL_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final String uri;
    private final Header header;
    private final String body;


    public HttpRequest(final String requestLine, Header header, String body) {
        final String[] parseRequest = requestLine.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        this.uri = parseRequest[1];
        this.header = header;
        this.body = body;
    }

    public String findQueryByKey(String queryKey){
        if(hasQueryInUri(uri)){
            return findValue(queryKey, uri);
        }

        if(isFormUrlEncoded()){
            return findValue(queryKey, body);
        }

        throw new NoSuchElementException("요청내에 QueryString이 존재하지 않습니다.");
    }

    private boolean isFormUrlEncoded() {
        final String contentType = header.getContentType();
        return contentType!= null && contentType.equals("application/x-www-form-urlencoded");
    }


    private boolean hasQueryInUri(final String uri) {
        return uri.contains(URL_DELIMITER);
    }

    private String findValue(String queryKey, String target){
        final Map<String, String> queryMap = makeQueryMap(target);

        final String queryValue = queryMap.get(queryKey);
        validateNullValue(queryKey, queryValue);
        return queryValue;
    }

    private Map<String, String> makeQueryMap(final String target) {
        final Integer index = target.indexOf(URL_DELIMITER);
        final String queryString = target.substring(index + 1);
        final String[] s = queryString.split(QUERY_DELIMETER);
        return Arrays.stream(s)
                .map(q -> q.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    private void validateNullValue(final String queryKey, final String queryValue) {
        if (queryValue == null) {
            throw new NoSuchElementException(String.format("%s의 값을 찾을 수 없습니다.", queryKey));
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        Integer index = uri.indexOf(URL_DELIMITER);
        if (hasQueryInUri(uri)) {
            return uri.substring(0, index);
        }
        return uri;
    }

    public String getBody() {
        return body;
    }
}
