package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            // TODO : 요청 uri, 헤더 사용해서 컨트롤러 서치

            // TODO : 컨트롤러에서 해당 메서드 호출 -> index.html 반환

            // TODO : responseBody에 파일 내용 문자열로 넣기

            List<String> request = Arrays.asList(bufferedReader.readLine().split(" "));
            String httpMethod = request.get(0);
            String uri = request.get(1);
            String protocol = request.get(2);

            List<String> headers = new ArrayList<>();

            String header = bufferedReader.readLine();
            while (!header.isEmpty()) {
                headers.add(header);
                header = bufferedReader.readLine();
            }

            URL resource = getClass().getClassLoader().getResource("static" + uri);
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
