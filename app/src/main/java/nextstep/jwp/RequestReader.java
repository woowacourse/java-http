package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RequestReader {

    private final Map<String, String> statusCode = new HashMap<>();

    public RequestReader() {
        statusCode.put("200", "OK");
        statusCode.put("302", "Found");
        statusCode.put("401", "Unauthorized");
    }

    public String readHeader(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder request = new StringBuilder();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();

            if (line == null) {
                return "";
            }

            if ("".equals(line)) {
                return request.toString();
            }

            request.append(line)
                    .append("\r\n");

        }
        return request.toString();
    }

    public String extractUri(String input) {
        String[] values = input.split(" ");
        if (values.length < 2) {
            return "";
        }
        return input.split(" ")[1];
    }

    public Boolean isQueryString(String uri) {
        return uri.contains("?");
    }

    private Map<String, String> extractQueryString(String uri) {
        int index = uri.indexOf("?");
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        final Map<String, String> login = new HashMap<>();
        for (String query : queries) {
            String[] split = query.split("=");
            login.put(split[0], split[1]);
        }
        return login;
    }

    private Boolean isAuthorized(Map<String, String> params) {
        Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));
        return account.isPresent();
    }

    public String getResponse(String uri) throws IOException {
        if ("/".equals(uri)) {
            return response("200", "Hello world!");
        }
        if (isQueryString(uri)) {
            Map<String, String> params = extractQueryString(uri);
            if (isAuthorized(params)) {
                return responseWithResource("302", "/index.html");
            }
            return responseWithResource("401", "/401.html");
        }

        if (!uri.contains("html")) {
            uri += ".html";
        }

        return responseWithResource("200", uri);
    }

    private String responseWithResource(String status, String uri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        String string = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return response(status, string);
    }

    private String response(String status, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " " + statusCode.get(status) + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
