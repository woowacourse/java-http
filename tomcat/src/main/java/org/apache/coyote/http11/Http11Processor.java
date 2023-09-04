package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            String[] requests = requestLine.split(" ");

            String httpMethod = requests[0];
            String httpUrl = requests[1];
            String fileName = httpUrl.substring(httpUrl.lastIndexOf('/') + 1);
            String response = null;

            Map<String, String> header = parseToHeader(bufferedReader);

            HttpResponse httpResponse = new HttpResponse();

            if (httpMethod.equals("POST") && httpUrl.equals("/login")) {
                int contentLength = Integer.parseInt(header.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                log.info("requestBody: {}", requestBody);

                Map<String, String> queryParms = parseToQueryParms(requestBody);

                try {
                    User user = InMemoryUserRepository.findByAccount(queryParms.get("account"))
                            .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

                    if (!user.checkPassword(queryParms.get("password"))) {
                        throw new IllegalArgumentException("비밀번호 불일치");
                    }
                    log.info("user: {}", user);

                    Session session = new Session(UUID.randomUUID().toString());
                    session.setAttribute("user", user);
                    sessionManager.add(session);
                    httpResponse.setStatus("302 Found");
                    httpResponse.setRedirectUrl("/index.html");
                    httpResponse.setCookie("JSESSIONID=" + session.getId());

                    response = httpResponse.createResponse();
                } catch (IllegalArgumentException e) {
                    log.error("error : {}", e);
                    response = createRedirectResponse("/401.html");
                }
            }

            if (httpMethod.equals("POST") && httpUrl.equals("/register")) {

                int contentLength = Integer.parseInt(header.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                log.info("reqeustBody: {}", requestBody);

                Map<String, String> queryParms = parseToQueryParms(requestBody.toString());

                User user = new User(queryParms.get("account"), queryParms.get("password"),
                        queryParms.get("email"));
                InMemoryUserRepository.save(user);

                response = createRedirectResponse("/index.html");
                log.info(response);
            }

            if (httpMethod.equals("GET") && httpUrl.equals("/login")) {
                HttpCookie cookie = new HttpCookie(header.get("Cookie"));

                String sessionId = cookie.findValue("JSESSIONID");
                if (sessionManager.isExist(sessionId)) {
                    response = createRedirectResponse("/index.html");
                } else {
                    response = createResponse("text/html", readFile("static", "login.html"));
                }
            }

            if (httpMethod.equals("GET") && httpUrl.equals("/register")) {
                response = createResponse("text/html", readFile("static", "register.html"));
            }

            if (httpMethod.equals("GET") && httpUrl.equals("/")) {
                response = createResponse("text/plain;", "Hello world!");
            }

            if (httpMethod.equals("GET") && fileName.endsWith(".html")) {
                response = createResponse("text/html", readFile("static", fileName));
            }

            if (httpMethod.equals("GET") && fileName.equals("styles.css")) {
                response = createResponse("text/css", readFile("static/css", fileName));
            }

            if (httpMethod.equals("GET") && fileName.endsWith(".js") && !fileName.equals("scripts.js")) {
                response = createResponse("text/javascript", readFile("static/assets", fileName));
            }

            if (httpMethod.equals("GET") && fileName.equals("scripts.js")) {
                response = createResponse("text/javascript", readFile("static/js", fileName));
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseToQueryParms(String queryString) {
        String[] keyValues = queryString.split("&");

        Map<String, String> queryParms = new HashMap<>();

        for (String keyValue : keyValues) {
            String[] queryParm = keyValue.split("=");
            String key = queryParm[0];
            String value = queryParm[1];
            queryParms.put(key, value);
        }
        return queryParms;
    }

    private Map<String, String> parseToHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] keyValues = line.split(":");
            String key = keyValues[0].trim();
            String value = keyValues[1].trim();
            header.put(key, value);
        }
        return header;
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createRedirectResponse(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectUrl);
    }

    private String readFile(String directory, String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(directory + "/" + fileName);
        Path path = Path.of(resource.getPath());
        return Files.readString(path);
    }

}
