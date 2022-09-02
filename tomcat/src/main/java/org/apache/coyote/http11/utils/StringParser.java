package org.apache.coyote.http11.utils;

import org.apache.coyote.http11.dto.Http11Request;

public class StringParser {

    public static Http11Request loginQuery(String uri) {
        int point = uri.indexOf("?");
        String path = uri.substring(0, point) + ".html";
        String queryString = uri.substring(point + 1);
        if (queryString.isEmpty()) {
            throw new IllegalArgumentException("요청으로들어오는 값이 없습니다.");
        }
        String[] split = queryString.split("&");
        String account = split[0].split("=")[1];
        String password = split[1].split("=")[1];
        return new Http11Request(account, password, path);
    }

}
