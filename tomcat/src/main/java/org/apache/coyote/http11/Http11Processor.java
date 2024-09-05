package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            List<String> lines = new ArrayList<>();

            String line;
            while ((line = bufferedReader.readLine()) != null){
                lines.add(line);
            }

            StringTokenizer stringTokenizer = new StringTokenizer(lines.get(0));

            String method = stringTokenizer.nextToken();
            String requestUrl = stringTokenizer.nextToken();

            if(method.equals("GET") && requestUrl.equals("/")){
                responseRoot(outputStream);
            }
            if(method.equals("GET") && requestUrl.equals("/index.html")){
                responseIndex(outputStream);
            }
            if(method.equals("GET") && requestUrl.contains(".css")){
                responseCss(outputStream, requestUrl);
            }
            if(method.equals("GET") && requestUrl.contains(".js")){
                responseJs(outputStream, requestUrl);
            }
            if(method.equals("GET") && requestUrl.contains("login")){
                responseLogin(outputStream, requestUrl);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseLogin(OutputStream outputStream, String requestUrl) throws IOException {
        int index = requestUrl.indexOf("?");
        String queryString = requestUrl.substring(index + 1);
        String[] pairs = queryString.split("&");
        String[] keyValue = pairs[0].split("=");
        String account = keyValue[1];

        if(InMemoryUserRepository.findByAccount(account).isPresent()){
            User user = InMemoryUserRepository.findByAccount(account).get();
            log.info(user.toString());
        }

        URL url = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseJs(OutputStream outputStream, String requestUrl) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUrl);
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/js;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseCss(OutputStream outputStream, String requestUrl) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUrl);
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/css;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseIndex(OutputStream outputStream) throws IOException {
        URL url = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void responseRoot(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
