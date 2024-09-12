package support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class HttpRequestTestSupport {

    public static HttpRequest homeGet() throws IOException {
        return createRequest("GET", "/");
    }

    public static HttpRequest loginGet() throws IOException {
        return createRequest("GET", "/login");
    }

    public static HttpRequest loginGetWithCookie() throws IOException {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.getInstance().add(session);

        return createRequest("GET", "/login", "Cookie: JSESSIONID=" + sessionId);
    }

    public static HttpRequest loginPost() throws IOException {
        return createRequest("POST", "/login",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");
    }

    public static HttpRequest loginPostNotRegistered() throws IOException {
        return createRequest("POST", "/login",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=lily&password=password");
    }

    public static HttpRequest registerGet() throws IOException {
        return createRequest("GET", "/register");
    }

    public static HttpRequest registerPost() throws IOException {
        return createRequest("POST", "/register",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=lily&email=lily@email.com&password=password");
    }

    public static HttpRequest inValidRequest() throws IOException {
        return createRequest("GET", "/lily");
    }

    private static HttpRequest createRequest(String method, String path, String... headersAndBody) throws IOException {
        String requestString = String.join("\r\n",
                method + " " + path + " HTTP/1.1",
                String.join("\r\n", headersAndBody));

        BufferedReader reader = new BufferedReader(new StringReader(requestString));
        return HttpRequest.from(reader);
    }

    public static String loadResourceContent(String resourcePath) throws IOException {
        Path resource = getResourcePath("static" + resourcePath);
        return Files.readString(resource);
    }

    private static Path getResourcePath(String resourcePath) {
        String filePath = HttpRequestTestSupport.class.getClassLoader().getResource(resourcePath).getFile();
        return Path.of(filePath);
    }
}
