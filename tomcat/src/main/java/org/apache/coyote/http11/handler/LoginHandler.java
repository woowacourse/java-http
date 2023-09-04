package org.apache.coyote.http11.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.MemberService;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginHandler implements Handler {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getHeaders().containsKey("Cookie")) {
            String cookies = request.getHeaders().get("Cookie");
            for (String cookie : cookies.split(";")) {
                String[] probableSessionCookie = cookie.split("=");
                if (probableSessionCookie.length == 2) {
                    String sessionId = probableSessionCookie[1];
                    Session session = SessionManager.findSession(sessionId);
                    if (Objects.nonNull(session)) {
                        return new HttpResponse.Builder()
                                .setHttpStatusCode(HttpStatusCode.FOUND)
                                .setLocation("/index.html")
                                .setCookie("JSESSIONID=" + session.getId()).build();
                    }
                }
            }
        }
        if (request.getHeaders().containsKey("Content-Type") && request.getHeaders()
                .get("Content-Type").contains("application/x-www-form-urlencoded")) {
            return MemberService.login(request);
        }
        String url = "/login.html";
        try (
                FileInputStream fileStream = new FileInputStream(
                        findStaticResourceURL(url).getFile())
        ) {
            String path = findStaticResourceURL(url).getFile();
            String extension = getResourceExtension(path);
            return new HttpResponse.Builder()
                    .setHttpStatusCode(HttpStatusCode.OK)
                    .setContentType(toTextContentType(extension))
                    .setBody(fileStream.readAllBytes())
                    .build();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException();
        }
    }

    private URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }

    private String getResourceExtension(String path) {
        return path.split("\\.")[1];
    }

    private String toTextContentType(String extension) {
        return "text/" + extension;
    }
}
