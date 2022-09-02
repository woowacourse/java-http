package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    public void get(Http11Request request, OutputStream outputStream) throws IOException {
        Http11Response response = makeResponse(request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private Http11Response makeResponse(Http11Request request) {
        if (request.isResource()) {
            return generateResourceResponse(request);
        }
        if (request.getUrl().startsWith("/login")) {
            return generateLoginPage(request);
        }
        return generateDefaultResponse();
    }

    private Http11Response generateLoginPage(Http11Request request){
        Map<String, String> queries = request.getQueryString();
        URL resource = getClass().getClassLoader().getResource("static/login.html" );
        Map<String, String> headers = new LinkedHashMap<>();

        if (queries.get("account") != null && queries.get("password") != null) {
            checkUserInformation(queries);
        }

        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", Long.toString(new File(resource.getFile()).length()));

        try {
            return new Http11Response("HTTP/1.1", 200, "OK", headers,
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkUserInformation(Map<String, String> queries) {
        Optional<User> account = InMemoryUserRepository.findByAccount(queries.get("account"));
        account.ifPresent(ac -> {
            if (ac.checkPassword(queries.get("password"))) {
                System.out.println("user : " + ac);
            }
        });
    }

    private Http11Response generateDefaultResponse() {
        final var responseBody = "Hello world!";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", Long.toString(responseBody.getBytes().length));

        Http11Response response = new Http11Response("HTTP/1.1", 200,
                "OK", headers, responseBody);

        return response;
    }

    private Http11Response generateResourceResponse(Http11Request request) {
        String url = request.getUrl();
        URL resource = getClass().getClassLoader()
                .getResource("static" + url);
        String extension = url.substring(url.lastIndexOf(".")+1);
        String contentType = HttpContent.extensionToContentType(extension);

        try {
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", contentType + ";charset=utf-8");
            headers.put("Content-Length", Long.toString(new File(resource.getFile()).length()));

            Http11Response response = new Http11Response("HTTP/1.1", 200, "OK",
                    headers, new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(final Socket connection){

        try (
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            OutputStream outputStream = connection.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {

            Http11Request request = makeRequest(bufferedReader);
            get(request, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Http11Request makeRequest(BufferedReader bufferedReader) throws IOException {
        String[] rawStart = bufferedReader.readLine()
                .split(" ");
        String method = rawStart[0];
        String url = rawStart[1];

        Map<String, String> headers = new HashMap<>();
        while (true) {
            String data = bufferedReader.readLine();
            if (data == null || data.equals("")) {
                break;
            }
            String[] header = data.split(" ");
            headers.put(header[0].strip(), header[1].strip());
        }

        StringBuilder bodyBuilder = new StringBuilder();
        while (true) {
            if (!bufferedReader.ready()) {
                break;
            }
            String data = bufferedReader.readLine();
            if (data == null) {
                break;
            }
            bodyBuilder.append(data);
            bodyBuilder.append("\r\n");
        }

        String body = bodyBuilder.toString();

        return new Http11Request(method, url, headers, "");
    }
}
