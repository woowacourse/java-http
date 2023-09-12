package coyote.http;

public class RequestFixture {

    public static final String ROOT_REQUEST = "GET / HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

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

    public static final String POST_SUCCESS_LOGIN_REQUEST = "POST /login HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test\n" +
            "Content-Length: 30\n" +
            "\n" +
            "account=gugu&password=password";

    public static final String POST_FAIL_LOGIN_REQUEST = "POST /login HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test\n" +
            "Content-Length: 30\n" +
            "\n" +
            "account=test&password=test";

    public static final String GET_REGISTER_REQUEST = "GET /register HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String POST_REGISTER_LOGIN_REQUEST = "POST /register HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test\n" +
            "Content-Length: 59\n" +
            "\n" +
            "account=test&password=test&email=test@test.com";

    public static final String CSS_REQUEST = "GET /css/styles.css HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String HTML_REQUEST = "GET /index.html HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";

    public static final String NOT_FOUND_REQUEST = "GET /sdadpoq312311wopem HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
            "\n" +
            "message body";
}
