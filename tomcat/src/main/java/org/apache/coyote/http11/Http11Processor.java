package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.ContentParseResult;
import org.apache.coyote.http11.parser.Http11GetProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11GetProcessor http11GetProcessor;


    public Http11Processor(final Socket connection) {
        this.http11GetProcessor = new Http11GetProcessor();
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            ParseHttpRequest httpRequests = readRequestFromReader(bufferedReader);

            ContentParseResult parseResult = http11GetProcessor.parse(httpRequests);
            byte[] parsedContent = parseResult.getParseContent();

            byte[] response = String.join(
                            "\r\n",
                            parseResult.getHttpResponseStatus(),
                            parseResult.getAdditionalResponse(),
                            "Content-Length: " + parsedContent.length + " ",
                            "",
                            new String(parsedContent)
                    )
                    .getBytes();

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ParseHttpRequest readRequestFromReader(final BufferedReader bufferedReader) throws IOException {
        String buffer = "";
        ParseHttpRequest parseHttpRequest = null;
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            String[] lineSplit = buffer.split(" ");

            if (isReqeustExist(buffer) && lineSplit.length >= 2) {
                parseHttpRequest = new ParseHttpRequest(parseMethod(buffer), parseContentPath(buffer), new HashMap<>());
                break;
            }
        }

        if (AcceptableRequest.isPost(parseHttpRequest.method())) {
            Map<String, String> requestBody = parseBody(bufferedReader);
            return parseHttpRequest.addRequestBody(requestBody);
        }
        return parseHttpRequest;
    }

    private Map<String, String> parseBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = 0;
        String buffer;
        Map<String, String> map = new HashMap<>();
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            if (buffer.split("Content-Length: ").length != 1) {
                contentLength = Integer.parseInt(buffer.split("Content-Length: ")[1]);
            }
        }

        char[] contents = new char[contentLength];
        bufferedReader.read(contents, 0, contentLength);

        String requestBody = new String(contents);
        return createRequestBody(requestBody, map);
    }

    private static Map<String, String> createRequestBody(String requestBody, Map<String, String> map) {
        String[] requestBodies = requestBody.split("&");

        for (String s : requestBodies) {
            String[] keyValues = s.split("=");
            map.put(keyValues[0], keyValues[1]);
        }

        return map;
    }

    private boolean isReqeustExist(String buffer) {
        return AcceptableRequest.isRequestExist(buffer);
    }

    private String parseContentPath(String buffer) {
        return buffer.split(" ")[1];
    }

    private String parseMethod(String buffer) {
        return buffer.split(" ")[0];
    }
}
