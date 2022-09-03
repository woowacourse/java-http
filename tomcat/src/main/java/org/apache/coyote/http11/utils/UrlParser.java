package org.apache.coyote.http11.utils;

import org.apache.coyote.http11.dto.Http11Request;
import org.apache.coyote.http11.dto.LoginQueryDataDto;

public class UrlParser {
    private static final String PATH_STANDARD = "?";
    private static final String REQUEST_STANDARD = "&";
    private static final String DATA_STANDARD = "=";
    private static final int VALUE_INDEX = 1;
    private static final String HTML_EXTENSION = ".html";

    public static Http11Request loginQuery(String uri) {
        int point = uri.indexOf(PATH_STANDARD);
        if (point == -1) {
            return new Http11Request(uri+ HTML_EXTENSION);
        }
        String path = uri.substring(0, point) + HTML_EXTENSION;

        String queryRequest = uri.substring(point + 1);
        if (queryRequest.isEmpty()) {
            throw new IllegalArgumentException("요청으로 들어오는 값이 없습니다.");
        }
        String[] dataMap = queryRequest.split(REQUEST_STANDARD);
        String account = dataMap[0].split(DATA_STANDARD)[VALUE_INDEX];
        String password = dataMap[1].split(DATA_STANDARD)[VALUE_INDEX];

        return new Http11Request(new LoginQueryDataDto(account, password), path);
    }

}
