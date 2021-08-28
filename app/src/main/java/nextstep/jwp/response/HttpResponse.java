package nextstep.jwp.response;

public interface HttpResponse {

    void addStatus(StatusCode statusCode);

    void addHeader(String key, String ... values);

    void addBody(String content);

    String totalResponse();

    void addPage(String path);

    void addRedirectUrl(String url);
}
