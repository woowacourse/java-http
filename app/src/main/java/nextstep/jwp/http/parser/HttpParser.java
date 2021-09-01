package nextstep.jwp.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.exception.InternalServerErrorException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.message.HttpCookie;
import nextstep.jwp.http.message.HttpCookies;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpMethod;
import nextstep.jwp.http.message.HttpRequestLine;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class HttpParser {

    private HttpParser() {
    }

    public static HttpRequest parse(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String body = "";

        try {
            HttpRequestLine requestLine = HttpRequestLine.createByString(br.readLine());
            HttpHeaders headers = HttpHeaders.createByString(readHeaders(br));
            if (headers.isExistsContentLength()) {
                body = readBody(br, headers.contentLength());
            }

            return new HttpRequestImpl(headers, requestLine, body);
        } catch (IOException e) {
            throw new InternalServerErrorException();
        }
    }

    private static String readHeaders(BufferedReader br) throws IOException {
        StringBuilder headers = new StringBuilder();
        for (;;) {
            String line = br.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            headers
                .append(line)
                .append("\r\n");
        }
        return headers.toString();
    }

    private static String readBody(BufferedReader br, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    static class HttpRequestImpl implements HttpRequest {
        private static final String SESSION_ID = "JSESSIONID";

        private HttpHeaders headers;
        private HttpRequestLine requestLine;
        private String body;
        private HttpCookies cookies;
        private ApplicationContext applicationContext;

        public HttpRequestImpl(HttpHeaders headers, HttpRequestLine requestLine, String body) {
            this.headers = headers;
            this.requestLine = requestLine;
            this.body = body;
            parseCookieFromHeader(headers);
        }

        private void parseCookieFromHeader(HttpHeaders headers) {
            headers.getHeaderByName("Cookie")
                .ifPresentOrElse(
                    cookie -> this.cookies = HttpCookies.parseFrom(cookie),
                    () -> this.cookies = HttpCookies.EMPTY_COOKIES
                );
        }

        @Override
        public HttpMethod getMethod() {
            return requestLine.getMethod();
        }

        @Override
        public Optional<String> getQueryString() {
            return requestLine.getQueryString();
        }

        @Override
        public Map<String, String> getQueryStringAsMap() {
            Map<String, String> queryString = new HashMap<>();
            getQueryString()
                .ifPresent(query -> {
                    String[] splittedQuery = query.split("&");
                    for (String q : splittedQuery) {
                        String[] keyValue = q.split("=");
                        if (2 == keyValue.length) {
                            queryString.put(keyValue[0], keyValue[1]);
                        }
                    }
                });

            return queryString;
        }

        @Override
        public String getRequestURI() {
            return requestLine.getUri()
                .orElseThrow();
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public String getHeader(String name) {
            return headers.getHeaderByName(name)
                .orElseThrow();
        }

        @Override
        public HttpCookies getCookies() {
            return cookies;
        }

        @Override
        public Optional<HttpSession> getSession() {
            HttpCookie cookie = cookies.findByName(SESSION_ID)
                .orElse(null);

            if (Objects.isNull(cookie)) {
                return Optional.empty();
            }

            return Optional.ofNullable(HttpSessions.getSession(cookie.getValue()));
        }

        @Override
        public String getBody() {
            return body;
        }

        @Override
        public ApplicationContext getApplicationContext() {
            return applicationContext;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public String asString() {
            return String.join("\r\n",
                requestLine.asString(),
                headers.asString(),
                body
                );
        }
    }
}
