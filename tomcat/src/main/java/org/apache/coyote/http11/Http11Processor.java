package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String defaultMessage = "Hello world!";

    private final Socket connection;
    //추후에 여러 parser를 가질 수 있음
    private final HtmlParser htmlParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        htmlParser = new HtmlParser();
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

            byte[] parsedContent = parseContent(inputStream);

            byte[] response = String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + parsedContent.length + " ",
                    "",
                    new String(parsedContent)
            ).getBytes();

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] parseContent(final InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String contentPath = parseRequestPath(bufferedReader.readLine());

        byte[] parsedContent = htmlParser.parseContent(contentPath);
        if(parsedContent != null){
            return parsedContent;
        }

        return defaultMessage.getBytes();
    }

    private String parseRequestPath(final String line) {
        if(line == null) return "";

        String[] splittedLine = line.split(" ");
        int requestPathIndex = 1;

        if(splittedLine.length >= 2){
            return splittedLine[requestPathIndex];
        }

        return "";
    }
}
