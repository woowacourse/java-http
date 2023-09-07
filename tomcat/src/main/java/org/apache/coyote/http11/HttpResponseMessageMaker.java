package org.apache.coyote.http11;

import static org.apache.catalina.servlet.common.HttpHeader.SET_COOKIE;

import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.session.Cookies;

public class HttpResponseMessageMaker {

    public static String make(HttpResponse response) {
        return response.statusLine().toString()
                + cookieHeadersToString(response.cookies())
                + response.headers().toString()
                + ((response.messageBody() == null) ? "" : response.messageBody());
    }

    private static String cookieHeadersToString(Cookies cookies) {
        StringBuilder sb = new StringBuilder();
        for (String name : cookies.cookies().keySet()) {
            sb.append(SET_COOKIE + ": ")
                    .append(name).append("=").append(cookies.get(name))
                    .append(" \r\n");
        }
        return sb.toString();
    }
}
