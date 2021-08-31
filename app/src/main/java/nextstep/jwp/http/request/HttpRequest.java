package nextstep.jwp.http.request;

public class HttpRequest {

    private static final String GET = "GET";
    private static final String POST = "POST";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeader;
    private final String httpBody;
    private final Parameters parameters;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpHeader httpHeader, String httpBody,
        Parameters parameters) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeader = httpHeader;
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
}
