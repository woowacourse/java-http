package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PATH = "static";

    private final Socket connection;
    private final SessionManager sessionManager;


    public Http11Processor(final Socket connection) {
        this.connection = connection;
        sessionManager = new SessionManager();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.equals("\r\n")) {
                    builder.append(line).append("\n");
                    continue;
                }
                builder.append(line).append("\n");
            }

            HttpRequest httpRequest = new HttpRequest(builder.toString());
            String response = "";

            String body = "";
            if (httpRequest.getStartLine().getMethod() == HttpMethod.POST) {
                int contentLength = Integer.parseInt(httpRequest.getHeaders().get("Content-Length"));
                StringBuilder bodyBuilder = new StringBuilder();
                if (contentLength > 0) {
                    char[] bodyBuffer = new char[contentLength];
                    reader.read(bodyBuffer, 0, contentLength);
                    bodyBuilder.append(new String(bodyBuffer));
                }
                body = bodyBuilder.toString();
                String[] bodies = body.split("&");
                Map<String, String> params = new HashMap<>();
                for (String value : bodies) {
                    String[] keyValue = value.split("=");
                    params.put(keyValue[0], keyValue[1]);
                }

                if (httpRequest.getPath().equals("/register")) {
                    User user = new User(params.get("account"), params.get("password"), params.get("email"));
                    InMemoryUserRepository.save(user);
                    System.out.println("회원가입 성공: " + user.getAccount());
                    response = responseForGetMethod(httpRequest, 200, "/index.html");
                }

                if (httpRequest.getPath().equals("/login")) {
                    User user = InMemoryUserRepository.findByAccount(params.get("account"))
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
                    if (!user.checkPassword(params.get("password"))) {
                        response = responseForGetMethod(httpRequest, 401, "/401.html");
                    } else {
                        HttpCookie httpCookie = new HttpCookie();
                        httpCookie.addSessionId();

                        String cookie = "JSESSIONID=" + httpCookie.getCookieValue("JSESSIONID");
                        response = generateResponseWithCookie(302, cookie, "text/html;charset=utf-8 ",
                                generateResponseBody("/index.html"));

                        Session session = new Session(httpCookie.getCookieValue("JSESSIONID"));
                        session.setAttribute("user", user);

                        sessionManager.add(session);
                        System.out.println(response);
                    }
                    System.out.println(user);
                }
            }

            if (httpRequest.getStartLine().getMethod() == HttpMethod.GET) {
                response = responseForGetMethod(httpRequest, 200, httpRequest.getPath());
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String responseForGetMethod(HttpRequest httpRequest, int statuscode, String uri)
            throws URISyntaxException, IOException {
        String responseBody = "";
        String path = uri;

        if (uri.equals("/")) {
            responseBody = "Hello world!";
            return generateResponse(200, "text/html;charset=utf-8 ", responseBody);
        }
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            String queryString = "";
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);

            Map<String, String> map = new HashMap<>();
            String[] strings = queryString.split("&");
            for (String string : strings) {
                String[] keyValue = string.split("=");
                map.put(keyValue[0], keyValue[1]);
            }
        }

        if (path.equals("/login")) {
            if (httpRequest.getHeaders().containsKey("Cookie")) {
                String cookie = httpRequest.getHeaders().get("Cookie");
                String[] values = cookie.split("; ");
                path = "/index.html";
            } else {
                path = "/login.html";
            }
        }

        if (path.equals("/register")) {
            path = "/register.html";
        }

        System.out.println(path);

        URL resource = getUrl(path);
        responseBody = new String(Files.readAllBytes(Path.of(resource.toURI())));
        if (resource.getPath().endsWith(".css")) {
            return generateResponse(statuscode, "text/css;charset=utf-8 ", responseBody);
        } else {
            return generateResponse(statuscode, "text/html;charset=utf-8 ", responseBody);
        }
    }

    private String generateResponseBody(String path) throws URISyntaxException, IOException {
        URL resource = getUrl(path);
        return new String(Files.readAllBytes(Path.of(resource.toURI())));
    }


    private String generateResponse(int statuscode, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + statuscode + " OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String generateResponseWithCookie(int statuscode, String cookie, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + statuscode + " OK ",
                "Set-Cookie: " + cookie,
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private URL getUrl(String path) {
        path = DEFAULT_PATH + path;
        return getClass().getClassLoader().getResource(path);
    }
}
