package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.request.HttpRequestParser;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

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
            MyHttpRequest httpRequest =
                    HttpRequestParser.parse(readHttpRequest(new BufferedReader(new InputStreamReader(inputStream))));
            MyHttpResponse httpResponse = new MyHttpResponse();
            log.info("start request: {} {}", httpRequest.method(), httpRequest.getUri());

            if (!httpRequest.hasCookie("JSESSIONID")) {
                Session session = httpRequest.getSession(true);
                httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }

            RequestMapping requestMapping = new RequestMapping();
            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.build().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            log.info("end request: {} {}", httpRequest.method(), httpRequest.getUri());
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readHttpRequest(BufferedReader br) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String line;
        int contentLength = 0;
        while (!(line = br.readLine()).isEmpty()) {
            sb.append(line).append("\r\n");
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).strip());
            }
        }
        sb.append("\r\n");

        char[] cbuf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int count = br.read(cbuf, read, contentLength - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        sb.append(cbuf, 0, read);
        return sb.toString();
    }
}
