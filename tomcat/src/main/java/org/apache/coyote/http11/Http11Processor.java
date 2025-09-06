package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.ResponseBodyManager;
import org.apache.coyote.http11.request.ResponseHeaderManager;
import org.apache.coyote.http11.request.ResponseManager;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            RequestManager requestManager = new RequestManager(new RequestHeaderManager(), new RequestBodyManager());
            requestManager.read(bufferedReader);

            if (requestManager.equalsPath("/index.html")) {
                InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static/index.html");
                if (resourceAsStream == null) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
                StringBuilder st = new StringBuilder();
                String line2;
                while ((line2 = reader.readLine()) != null) {
                    st.append(line2).append("\r\n");
                }

                ResponseManager responseManager = new ResponseManager(new ResponseHeaderManager(),
                        new ResponseBodyManager(st.toString()));
                outputStream.write(responseManager.getContents().getBytes());
                outputStream.flush();
                return;
            }

            final var responseBody = "Hello world!";

            ResponseManager responseManager = new ResponseManager(new ResponseHeaderManager(),
                    new ResponseBodyManager(responseBody));

            outputStream.write(responseManager.getContents().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static class RequestBodyManager {

        List<String> body;

        public void read(BufferedReader br) throws IOException {
            String line;
            while ((line = br.readLine()) != null) {
                body.add(line);
            }
        }

        public List<String> getBody() {
            return body;
        }
    }

    public static class RequestHeaderManager {

        private String method;
        private String path;
        private String version;
        private final Map<String, String> headers = new HashMap<>();

        public void read(BufferedReader br) throws IOException {
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                throw new IllegalArgumentException("request is Empty");
            }

            String[] parts = requestLine.split(" ", 3);
            if (parts.length < 3) {
                throw new IllegalArgumentException();
            }

            method = parts[0];
            path = parts[1];
            version = parts[2];

            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(':');
                if (idx <= 0) {
                    throw new IllegalArgumentException();
                }
                String name = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                headers.put(name, value);
            }
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getVersion() {
            return version;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getHeader(String name) {
            return headers.get(name);
        }

        public boolean equalsPath(String path) {
            return this.path.equals(path);
        }
    }

    public static class RequestManager {

        private final RequestHeaderManager requestHeaderManager;
        private RequestBodyManager requestBodyManager;

        public RequestManager(RequestHeaderManager requestHeaderManager, RequestBodyManager requestBodyManager) {
            this.requestHeaderManager = requestHeaderManager;
            this.requestBodyManager = requestBodyManager;
        }

        public void read(BufferedReader br) throws IOException {
            requestHeaderManager.read(br);
            if (hasBodyByHeader(requestHeaderManager)) {
                requestBodyManager.read(br);
            }
        }

        private static boolean hasBodyByHeader(RequestHeaderManager requestHeaderManager) {
            String te = requestHeaderManager.getHeader("Transfer-Encoding");
            if (te != null && te.equalsIgnoreCase("chunked")) {
                return true;
            }

            String cl = requestHeaderManager.getHeader("Content-Length");
            if (cl == null) {
                return false;
            }
            try {
                return Long.parseLong(cl) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public boolean equalsPath(String path) {
            return requestHeaderManager.equalsPath(path);
        }
    }
}
