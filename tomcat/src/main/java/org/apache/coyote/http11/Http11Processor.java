package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.servlet.DispatcherServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final DispatcherServlet dispatcherServlet;
    private final Socket connection;

    public Http11Processor(Socket connection) {
        dispatcherServlet = DispatcherServlet.getInstance();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpServletRequest httpServletRequest = parseHttpRequest(bufferedReader);
            HttpServletResponse httpServletResponse = HttpServletResponse.createEmptyResponse();

            dispatcherServlet.doDispatch(httpServletRequest, httpServletResponse);

            flushResponseMessage(httpServletResponse, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpServletRequest parseHttpRequest(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        HttpHeaders httpHeaders = readHeaders(bufferedReader);
        HttpMessageBody httpMessageBody = readMessageBody(bufferedReader, httpHeaders);

        return new HttpServletRequest(requestLine, httpHeaders, httpMessageBody);
    }

    private HttpHeaders readHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            httpHeaders.putHeader(headerLine);
        }
        return httpHeaders;
    }

    private HttpMessageBody readMessageBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        String lengthValue = httpHeaders.getHeaderValue(HttpHeaderName.CONTENT_LENGTH);
        if (lengthValue == null) {
            return HttpMessageBody.createEmptyBody();
        }

        int bodyLength = Integer.parseInt(lengthValue);
        char[] buffer = new char[bodyLength];
        bufferedReader.read(buffer, 0, bodyLength);
        String requestBody = new String(buffer);
        return new HttpMessageBody(requestBody);
    }

    private void flushResponseMessage(HttpServletResponse httpServletResponse, OutputStream outputStream)
            throws IOException {
        byte[] httpMessage = httpServletResponse.resolveHttpMessage().getBytes();
        outputStream.write(httpMessage);
        outputStream.flush();
    }
}
