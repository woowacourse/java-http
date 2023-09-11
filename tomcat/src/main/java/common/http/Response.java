package common.http;

public interface Response {

    void addVersionOfTheProtocol(String versionOfTheProtocol);

    void addHttpStatus(HttpStatus httpStatus);

    void addContentType(ContentType contentType);

    void sendRedirect(String redirectURL);

    void addStaticResourcePath(String name);

    void addCookie(Cookie cookie);

    boolean hasStaticResourcePath();

    String getStaticResourcePath();

    void addBody(String body);
}
