package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.AbstractController;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            // startline 읽기
            String startLine = reader.readLine();
            HttpRequestLine httpRequestLine = new HttpRequestLine(startLine);

            // header 읽기
            StringBuilder builder = new StringBuilder();
            String line;
            while (StringUtils.isNoneBlank(line = reader.readLine())) {
                builder.append(line).append("\n");
            }
            HttpHeaders httpHeaders = new HttpHeaders(builder.toString());

            // body 읽기
            int contentLength = httpHeaders.getContentLength();
            char[] bodyBuffer = new char[contentLength];
            reader.read(bodyBuffer, 0, contentLength);
            HttpBody httpBody = new HttpBody(new String(bodyBuffer));

            HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeaders, httpBody);
            HttpResponse httpResponse = new HttpResponse();

            AbstractController controller = RequestMapping.getController(httpRequest.getPath());
            controller.service(httpRequest, httpResponse);

            String response = httpResponse.toString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
