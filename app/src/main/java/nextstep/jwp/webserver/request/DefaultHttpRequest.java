package nextstep.jwp.webserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.webserver.exception.BadRequestException;
import nextstep.jwp.webserver.response.HttpResponse;

public class DefaultHttpRequest implements HttpRequest {

    private static final String UTF_8 = "UTF-8";
    private static final String COOKIE = "Cookie";
    private static final String SESSION_ID_KEY = "JSESSIONID";

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestParams requestParams;
    private HttpCookie httpCookie;
    private HttpSession httpSession;

    public DefaultHttpRequest(InputStream inputStream) {

        try {
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            this.requestLine = new RequestLine(br.readLine());
            this.requestHeader = new RequestHeader(br);
            this.requestParams = new RequestParams(br, requestHeader, requestLine.queryString());
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    @Override
    public HttpMethod httpMethod() {
        return requestLine.httpMethod();
    }

    @Override
    public String httpUrl() {
        return requestLine.httpUrl();
    }

    @Override
    public String getAttribute(String key) {
        return requestParams.getParam(key);
    }

    @Override
    public Map<String, String> getRequestParams() {
        return new HashMap<>(requestParams.getParams());
    }

    @Override
    public void prepareCookieAndSession(HttpResponse httpResponse, HttpSessions httpSessions) {
        this.httpCookie = new HttpCookie(requestHeader.get(COOKIE));
        String sessionId = httpCookie.getValue(SESSION_ID_KEY);
        if(sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            httpResponse.addHeader("Set-Cookie", SESSION_ID_KEY + "=" + sessionId);
        }
        this.httpSession = httpSessions.getSession(sessionId);
    }

    @Override
    public HttpCookie getCookie() {
        return httpCookie;
    }

    @Override
    public HttpSession getSession() {
        return httpSession;
    }
}
