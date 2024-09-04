package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            String response = createResponse(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final InputStream inputStream) throws IOException {
        Http11Request http11Request = Http11Request.from(inputStream);
        log.info("http request : {}", http11Request);

        try {
            String responseBody = getResponseBody(http11Request);
            String contentType = getContentType(http11Request);

            return String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );
        } catch (IllegalArgumentException e) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: " + "/401.html",
                    "Content-Length: 0 ",
                    ""
            );
        }
    }

    private String getResponseBody(Http11Request http11Request) throws IOException {
        if (http11Request.isStaticResourceRequest()) {
            return getStaticResource(http11Request.getEndpoint());
        }
        if ("/login".equals(http11Request.getEndpoint())) {
            RequestTarget requestTarget = http11Request.getRequestTarget();
            if (requestTarget.hasParam("account")) {
                String account = requestTarget.getParam("account");
                String password = requestTarget.getParam("password");

                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("잘못된 비밀번호입니다.");
                }
                log.info("user {}", user);
                return getStaticResource("/index.html");
            }
            return getStaticResource("/login.html");
        }
        return "Hello world!";
    }

    private String getStaticResource(String name) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + name);
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getContentType(Http11Request http11Request) {
        String accept = http11Request.getHeader("Accept")
                .orElse("text/html");

        return accept + ";charset=utf-8";
    }

}
