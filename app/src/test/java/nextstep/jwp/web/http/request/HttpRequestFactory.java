package nextstep.jwp.web.http.request;

public class HttpRequestFactory {

    public static String getIndexHtml() {
        return String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String invalidUrl() {
        return String.join("\r\n",
            "GET /invalid HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "",
            "");
    }

    public static String getFirstLogin() {
        return String.join("\r\n",
            "GET /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String getFirstLoginWithCookie() {
        return String.join("\r\n",
            "GET /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: yummy-cookie=choco",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String getAlreadyLogin() {
        return String.join("\r\n",
            "GET /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: JSESSIONID=sessionId",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String postLoginWithRequestBody() {
        return String.join("\r\n",
            "POST /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Content-Length: 30",
            "Content-Type: application/x-www-form-urlencoded",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "account=gugu&password=password");
    }

    public static String postLoginWithInvalidUser() {
        return String.join("\r\n",
            "POST /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Content-Length: 30",
            "Content-Type: application/x-www-form-urlencoded",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "account=invalid&password=invalid");
    }

    public static String getError401Html() {
        return String.join("\r\n",
            "GET /401.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String getRegister() {
        return String.join("\r\n",
            "GET /register HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "");
    }

    public static String postRegisterUser() {
        return String.join("\r\n",
            "POST /register HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "sec-ch-ua: \"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"9\"",
            "sec-ch-ua-mobile: ?0",
            "Upgrade-Insecure-Requests: 1",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Content-Length: 61",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Sec-Fetch-Site: none",
            "Sec-Fetch-Mode: navigate",
            "Sec-Fetch-User: ?1",
            "Sec-Fetch-Dest: document",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ko-KR,ko;q=0.9",
            "",
            "account=newUser&password=password&email=hkkang%40woowahan.com");
    }
}
