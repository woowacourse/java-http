package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            final SessionManager sessionManager = SessionManager.getInstance();
            final HttpRequest httpRequest;
            try {
                httpRequest = new HttpRequest(inputStream);
            } catch (final IOException e) {
                send500Response(outputStream);
                throw new UncheckedServletException(e);
            }
            final String requestURI = httpRequest.getRequestURI();
            final HttpMethod method = httpRequest.getMethod();
            final String requestBody = httpRequest.getRequestBody();

            if (requestURI.equals("/") || requestURI.equals("/index.html")) {
                send200Response("/index.html", outputStream);
                return;
            }
            if (requestURI.equals("/login") && method == HttpMethod.GET) {
                final String sessionId = httpRequest.getCookieValue("JSESSIONID");
                if (sessionManager.contains(sessionId)) {
                    send302Response("/index.html", outputStream);
                    return;
                }
                send200Response("/login.html", outputStream);
                return;
            }
            if (requestURI.startsWith("/login") && method == HttpMethod.POST) {
                final String[] split = requestBody.split("&");
                final String account = split[0].split("=")[1];
                final String password = split[1].split("=")[1];
                InMemoryUserRepository.findByAccount(account).ifPresentOrElse(
                        user -> {
                            if (user.checkPassword(password)) {
                                final String sessionId = UUID.randomUUID().toString();
                                final Session session = new Session(sessionId);
                                session.setAttribute("user", user);
                                sessionManager.add(session);
                                send302ResponseWithCookie("/login", sessionId, outputStream);
                                return;
                            }
                            send401Response(outputStream);
                        },
                        () -> {
                            send401Response(outputStream);
                        }
                );
                return;
            }
            if (requestURI.equals("/register") && method == HttpMethod.GET) {
                send200Response("/register.html", outputStream);
                return;
            }
            if (requestURI.equals("/register") && method == HttpMethod.POST) {
                final String[] split = requestBody.split("&");
                final String account = split[0].split("=")[1];
                final String email = split[1].split("=")[1].replace("%40", "@");
                final String password = split[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, password, email));
                send302Response("/index.html", outputStream);
                return;
            }
            send200Response(requestURI, outputStream);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void send200Response(final String resource, final OutputStream outputStream) {
        try {
            final URL resourceUrl = getResourceUrl(resource);
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            final String response = create200HttpResponse(resourcePath);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final FileNotFoundException e) {
            send404Response(outputStream);
        } catch (final IOException e) {
            send500Response(outputStream);
        }
    }

    private void send302Response(final String redirectResource, final OutputStream outputStream) {
        try {
            final String response = create302HttpResponse(redirectResource);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            send500Response(outputStream);
        }
    }

    private void send302ResponseWithCookie(
            final String redirectResource,
            final String sessionId,
            final OutputStream outputStream
    ) { // todo 3단계에 리팩터링 예정
        try {
            final String redirectResponse = create302HttpResponse(redirectResource);
            final String cookieHeader = String.format("Set-Cookie: JSESSIONID=%s\r\n", sessionId);
            final String response = String.join("\r\n", redirectResponse, cookieHeader);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            send500Response(outputStream);
        }
    }

    private void send401Response(final OutputStream outputStream) {
        try {
            final String response = create401HttpResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            send500Response(outputStream);
        }
    }

    private void send404Response(final OutputStream outputStream) {
        try {
            final String response = create404HttpResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            send500Response(outputStream);
        }
    }

    private void send500Response(final OutputStream outputStream) {
        try {
            final String response = create500HttpResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    // 나중에 HttpRequest 리팩터링에 사용하기 위해 남겨둠
    private void parseLoginRequest(final String requestURL, final OutputStream outputStream) {
        final int QUERY_KEY_INDEX = 0;
        final int QUERY_VALUE_INDEX = 1;

        try {
            final String queryString = requestURL.split("\\?")[1];
            final Map<String, String> queries = Arrays.stream(queryString.split("&"))
                    .collect(
                            Collectors.toMap(
                                    query -> query.split("=")[QUERY_KEY_INDEX],
                                    query -> query.split("=")[QUERY_VALUE_INDEX]
                            )
                    );
            final String account = queries.get("account");
            final String password = queries.get("password");
        } catch (final NullPointerException
                       | ArrayIndexOutOfBoundsException
                       | PatternSyntaxException
                       | ClassCastException e) {
            send500Response(outputStream);
            throw new UncheckedServletException(e);
        }
    }

    private String create200HttpResponse(final Path resourcePath) throws IOException {
        final String contentType = Files.probeContentType(resourcePath);
        final String responseBody = new String(Files.readAllBytes(resourcePath));
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String create302HttpResponse(final String redirectResource) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                String.format("Location: http://localhost:8080%s ", redirectResource),
                "Content-Length: 0 ");
    }

    private String create401HttpResponse() {
        String responseBody;

        try {
            final URL resourceUrl = getResourceUrl("/401.html");
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            responseBody = new String(Files.readAllBytes(resourcePath));
        } catch (final IOException e) {
            responseBody = "<html><body><h1>401 Unauthorized</h1><p>Access to this resource is denied.</p></body></html>";
        }

        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html; charset=utf-8 ",
                String.format("Content-Length: %s ", responseBody.getBytes().length),
                "",
                responseBody);
    }

    private String create404HttpResponse() {
        String responseBody;

        try {
            final URL resourceUrl = getResourceUrl("/404.html");
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            responseBody = new String(Files.readAllBytes(resourcePath));
        } catch (final IOException e) {
            responseBody = "<html><body><h1>404 Not Found</h1><p>This requested URL was not found on this server.</p></body></html>";
        }

        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html; charset=utf-8 ",
                String.format("Content-Length: %s ", responseBody.getBytes().length),
                "",
                responseBody);
    }

    private String create500HttpResponse() {
        String responseBody;

        try {
            final URL resourceUrl = getResourceUrl("/500.html");
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            responseBody = new String(Files.readAllBytes(resourcePath));
        } catch (final IOException e) {
            responseBody = "<html><body><h1>500 Internal</h1></body></html>";
        }

        return String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ",
                "Content-Type: text/html; charset=utf-8 ",
                String.format("Content-Length: %s ", responseBody.getBytes().length),
                "",
                responseBody);
    }

    private URL getResourceUrl(final String resource) throws FileNotFoundException {
        final URL resourceUrl = getClass().getClassLoader().getResource("static" + resource);
        if (resourceUrl == null) {
            throw new FileNotFoundException("요청한 리소스를 찾을 수 없습니다.");
        }
        return resourceUrl;
    }
}
