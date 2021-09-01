package nextstep.jwp.http.request;

import nextstep.jwp.http.stateful.HttpCookie;

public class HttpRequest {

    private static final String GET = "GET";
    private static final String POST = "POST";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeader;
    private final String httpBody;
    private final Parameters parameters;
    private final HttpCookie httpCookie;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpHeader httpHeader, HttpCookie httpCookie, String httpBody,
        Parameters parameters) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeader = httpHeader;
        this.httpCookie = httpCookie;
        this.httpBody = httpBody;
        this.parameters = parameters;
    }

    public String getRequestURI() {
        return httpRequestLine.getRequestURI();
    }

    public String getParameter(String name){
        return parameters.getParameter(name);
    }

    public String getMethod(){
        return httpRequestLine.getMethod();
    }

    public boolean isGetRequest(){
        return GET.equals(httpRequestLine.getMethod());
    }

    public boolean isPostRequest(){
        return POST.equals(httpRequestLine.getMethod());
    }

    public String getSessionId(){
        return httpCookie.getSessionId();
    }
}
