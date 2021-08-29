package nextstep.jwp;

import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.PageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final PageController pageController;
    private final JwpController jwpController;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        this.pageController = new PageController();
        this.jwpController = new JwpController();
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            log.info(matchRequest(inputStream, outputStream));
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }

    }

    private String matchRequest(InputStream inputStream, OutputStream outputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String firstLine = bufferedReader.readLine();
        if (firstLine != null) {
            List<String> requestHeader = Arrays.asList(firstLine.split(" "));
            String response = matchMethod(requestHeader);
            outputStream.write(response.getBytes());
            outputStream.flush();
        }
        return firstLine;
    }

    private String matchMethod(List<String> requestHeader) {
        if (requestHeader.get(0).equals("GET")) {
            String request = requestHeader.get(1).substring(1);
            if(request.contains("?") && request.contains("=")){
                return getParam(request);
            }
            return getPage(request);
        }
        throw new IllegalArgumentException("옳지 않은 요청입니다.");
    }

    private String getParam(final String request) {
        log.info("로그인한 유저 : " + jwpController.mapResponse(request));
        Map.Entry<Integer, String> responseEntry = new ArrayList<>(pageController.mapResponse(request).entrySet()).get(0);
        return makeResponse(responseEntry);
    }

    private String getPage(final String request) {
        Map.Entry<Integer, String> responseEntry = new ArrayList<>(pageController.mapResponse(request).entrySet()).get(0);
        return makeResponse(responseEntry);
    }

    private String makeResponse(final Map.Entry<Integer, String> responseEntry) {
        Integer statusCode = responseEntry.getKey();
        String responseBody = responseEntry.getValue();

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
