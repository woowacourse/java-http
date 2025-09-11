package org.apache.coyote.http11;

public class AbstractController implements Controller {

    private final String url;

    public AbstractController(String url) {
        this.url = url;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        switch (httpRequest.getRequestLine().method()) {
            case POST -> doPost(httpRequest, httpResponse);
            case GET -> doGet(httpRequest, httpResponse);
            default -> throw new IllegalStateException("Unexpected value: " + httpRequest.getRequestLine().method());
        }
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    public String getUrl() {
        return url;
    }
}
