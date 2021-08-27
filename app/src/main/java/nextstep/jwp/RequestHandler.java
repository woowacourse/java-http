package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;


public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);
            String path = httpRequest.getPath();
            URL url = getClass().getClassLoader().getResource("static" + path);
            String response = null;
            Path filePath = null;
            String responseBody = null;
            if (url != null) {
                File file = new File(url.getFile());
                if (file.exists()) {
                    filePath = new File(url.getFile()).toPath();
                    responseBody = new String(Files.readAllBytes(filePath));
                    httpResponse.setStatus(200);
                    httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
                    httpResponse.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
                    httpResponse.write(responseBody);
                    httpResponse.flush();
                }
            } else {
                if ("/login".equals(path)) {
                    String queryString = httpRequest.getQueryString();
                    if (httpRequest.getQueryString() == null) {
                        url = getClass().getClassLoader().getResource("static" + path + ".html");
                        filePath = new File(url.getFile()).toPath();
                        responseBody = new String(Files.readAllBytes(filePath));
                        httpResponse.setStatus(200);
                        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
                        httpResponse.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
                        httpResponse.write(responseBody);
                        httpResponse.flush();
                    } else {
                        String[] split = queryString.split("&");
                        String account = null;
                        String password = null;
                        for (String data : split) {
                            String[] splitData = data.split("=");
                            if ("account".equals(splitData[0])) {
                                account = splitData[1];
                            }
                            if ("password".equals(splitData[0])) {
                                password = splitData[1];
                            }
                        }
                        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            if (user.checkPassword(password)) {
                                httpResponse.setStatus(302);
                                httpResponse.sendRedirect("/index.html");
                            } else {
                                httpResponse.setStatus(302);
                                httpResponse.sendRedirect("/401.html");
                            }
                        } else {
                            httpResponse.setStatus(302);
                            httpResponse.sendRedirect("/401.html");
                        }
                    }
                }
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
