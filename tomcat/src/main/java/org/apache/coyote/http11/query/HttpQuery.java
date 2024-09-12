package org.apache.coyote.http11.query;

import com.techcourse.exception.client.BadRequestException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpQuery {

    private static final String regex = "^/\\w+\\?(?:[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+)(?:&[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+)*$";
    private static final Pattern pattern = Pattern.compile(regex);
    private static final String QUERY_START_CHAR = "?";

    private final Query query;

    public HttpQuery(Query query) {
        this.query = query;
    }

    public static HttpQuery createByUri(String uri) {
        if (!uri.contains(QUERY_START_CHAR)) {
            return null;
        }
        validateUri(uri);
        validateQueryCharacter(uri);
        String queries = uri.substring(uri.indexOf(QUERY_START_CHAR) + 1);
        return new HttpQuery(Query.create(queries));
    }

    private static void validateUri(String uri) {
        Matcher matcher = pattern.matcher(uri);
        if (!matcher.matches()) {
            throw new BadRequestException("잘못된 형식의 요청 쿼리입니다. = " + uri);
        }
    }

    private static void validateQueryCharacter(String uri) {
        if (!uri.contains(QUERY_START_CHAR)) {
            throw new BadRequestException("쿼리가 존재하지 않습니다.");
        }
    }

    public String findByKey(String key) {
        return query.findByKey(key);
    }

    public static String extractPath(String uri) {
        if (uri.contains(QUERY_START_CHAR)) {
            return uri.substring(0, uri.indexOf(QUERY_START_CHAR));
        }
        return uri;
    }
}
