package nextstep.jwp;

import nextstep.jwp.model.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            log.info(matchRequest(inputStream, outputStream));
        } catch (IOException | IllegalArgumentException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }

    }

    private String matchRequest(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String firstLine = bufferedReader.readLine();
        if (firstLine == null) {
            throw new IllegalArgumentException("request가 존재하지 않습니다.");
        }

        String response = getResponse(bufferedReader, firstLine);

        outputStream.write(response.getBytes());
        outputStream.flush();
        return firstLine;
    }

    public String getResponse(BufferedReader bufferedReader, String firstLine) {
        List<String> requestHeader = Arrays.asList(firstLine.split(" "));
        HttpMethod httpMethod = matchMethod(requestHeader);
        String requestUrl = requestHeader.get(1).substring(1);
        return matchResponse(bufferedReader, httpMethod, requestUrl);
    }

    private String matchResponse(final BufferedReader bufferedReader, final HttpMethod httpMethod, final String requestUrl) {
        if(httpMethod.equals(HttpMethod.POST)){
            String requestBody = extractRequestBody(bufferedReader);
            return httpMethod.matches(requestUrl, requestBody);
        }
        return httpMethod.matches(requestUrl, null);
    }

    private String extractRequestBody(BufferedReader bufferedReader) {
        try {
            List<String> requestHeaders = extractRequestHeaders(bufferedReader);
            int contentLength = extractContentLength(requestHeaders);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("requestBody가 존재하지 않습니다.");
        }
    }

    private List<String> extractRequestHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        while(bufferedReader.ready()){
            String line = bufferedReader.readLine();
            if(line.isEmpty()) {
                break;
            }
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private int extractContentLength(List<String> requestHeader) {
        String content = requestHeader.stream()
                .filter(header -> header.contains("Content-Length"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("request가 올바르지 않습니다."));
        return Integer.parseInt(content.split(" ")[1]);
    }

    private HttpMethod matchMethod(List<String> requestFirstLine) {
        String requestMethod = requestFirstLine.get(0);
        return HttpMethod.matchHttpMethod(requestMethod);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
