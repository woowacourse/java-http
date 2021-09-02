package nextstep.jwp.webserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.webserver.exception.BadRequestException;

public class DefaultHttpRequest implements HttpRequest {

    private static final String UTF_8 = "UTF-8";
    private static final String COOKIE = "Cookie";

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestParams requestParams;
    private HttpCookie httpCookie;
    private SessionUtil sessionUtil;

    public DefaultHttpRequest(InputStream inputStream) {

        try {
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            this.requestLine = new RequestLine(br.readLine());
            this.requestHeader = new RequestHeader(br);
            this.requestParams = new RequestParams(br, requestHeader, requestLine.queryString());
            this.httpCookie = new HttpCookie(requestHeader.get(COOKIE));
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
    public HttpCookie getCookie() {
        return httpCookie;
    }

    @Override
    public HttpSession getSession() {
        final String sessionId = httpCookie.getValue(SessionUtil.SESSION_ID_KEY);
        if(sessionId == null || sessionId.isEmpty()) {
            return sessionUtil.createSession();
        }
        return sessionUtil.getSession(sessionId);
    }

    @Override
    public void addSessionCreator(SessionUtil sessionUtil) {
        this.sessionUtil = sessionUtil;
    }
}
