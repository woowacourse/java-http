package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpReader(inputStream).getHttpRequest();
            RequestLine requestLine = httpRequest.requestLine();
            log.info(requestLine.getMethod());
            if (requestLine.isGet() && requestLine.isRoot()) {
                responseRoot(outputStream, requestLine);
            }
            if (requestLine.isGet() && requestLine.isIndex()) {
                responseIndex(outputStream, requestLine);
            }
            if (requestLine.isGet() && requestLine.hasCss()) {
                responseCss(outputStream, requestLine);
            }
            if (requestLine.isGet() && requestLine.hasJs()) {
                responseJs(outputStream, requestLine);
            }
            if (requestLine.isGet() && requestLine.has401()) {
                response401(outputStream, requestLine);
            }
            if (requestLine.isGet() && requestLine.hasRegister()) {
                responseRegister(outputStream, requestLine);
            }
            if (requestLine.isPost() && requestLine.hasRegister()) {
                responseRegisterPost(outputStream, httpRequest);
            }
            if (requestLine.isGet() && requestLine.hasLogin() && !requestLine.hasQuestion()) {
                responseLogin(outputStream, requestLine);
            }
            if (requestLine.isPost() && requestLine.hasLogin()) {
                responseLoginUser(outputStream, httpRequest);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseRoot(OutputStream outputStream, RequestLine requestLine) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseIndex(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseCss(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestLine.getRequestUrl());
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/css;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseJs(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestLine.getRequestUrl());
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/js;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void response401(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestLine.getRequestUrl());
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseRegister(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestLine.getRequestUrl() + ".html");
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseRegisterPost(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        Map<String, String> body = httpRequest.requestBody().getBody();
        User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
        HttpHeader httpHeader = new HttpHeader();
        StatusLine statusLine = new StatusLine(httpRequest.requestLine().getProtocol(), "302", "Found");
        httpHeader.putHeader("Location", "/index.html");
        String response = String.join("\r\n",
                statusLine.getStatusLine(),
                httpHeader.getHttpHeader(),
                "");

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseLogin(OutputStream outputStream, RequestLine requestLine) throws IOException {
        URL url = getClass().getClassLoader().getResource("static/login.html");

        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
                requestLine.getProtocol() + " 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseLoginUser(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        Map<String, String> body = httpRequest.requestBody().getBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(body.get("account"));
        HttpHeader httpHeader = new HttpHeader();
        String response;
        if (user.isPresent()) {
            StatusLine statusLine = new StatusLine(httpRequest.requestLine().getProtocol(), "302", "Found");
            httpHeader.putHeader("Location", "/index.html");
            response = String.join("\r\n",
                    statusLine.getStatusLine(),
                    httpHeader.getHttpHeader(),
                    "");
        } else {
            StatusLine statusLine = new StatusLine(httpRequest.requestLine().getProtocol(), "302", "Found");
            httpHeader.putHeader("Location", "/401.html");
            response = String.join("\r\n",
                    statusLine.getStatusLine(),
                    httpHeader.getHttpHeader(),
                    "");
        }

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
