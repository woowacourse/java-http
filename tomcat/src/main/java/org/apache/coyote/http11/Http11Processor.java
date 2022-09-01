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

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
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
        return generateDefaultResponse();
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
        URL resource = getClass().getClassLoader().getResource("static" + url);

        try {
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "text/html;charset=utf-8");
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

        try (InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();) {
            Http11Request request = makeRequest(connection);
            get(request, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Http11Request makeRequest(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String[] rawStart = bufferedReader.readLine().split(" ");
            String method = rawStart[0];
            String url = rawStart[1];

            Map<String, String> headers = new HashMap<>();
            String data;
            while (!(data = bufferedReader.readLine()).equals("")) {
                String[] header = data.split(" ");
                headers.put(header[0].strip(), header[1].strip());
            }

            StringBuilder bodyBuilder = new StringBuilder();
            while ((data = bufferedReader.readLine()) != null) {
                bodyBuilder.append(data);
                bodyBuilder.append("\r\n");
            }
            String body = bodyBuilder.toString();

            return new Http11Request(method, url, headers, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
