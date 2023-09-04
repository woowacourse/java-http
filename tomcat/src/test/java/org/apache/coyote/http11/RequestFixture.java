package org.apache.coyote.http11;

public class RequestFixture {

    public static final String REQUEST = "GET /index.html HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String REQUEST_WITH_QUERY_STRING = "GET /login?account=account&password=password HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String GET_LOGIN_REQUEST = "GET /login HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String GET_REGISTER_REQUEST = "GET /register HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

}
