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
    public RequestReader() {
    }

    public String readHeader(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder request = new StringBuilder();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();

            if(line == null){
                return "";
            }

            if("".equals(line)){
                return request.toString();
            }

            request.append(line)
                    .append("\r\n");

        }
        return request.toString();
    }

    public String extractUri(String input){
        String[] values = input.split(" ");
        if (values.length < 2){
            return "";
        }
        return extractQueryString(input.split(" ")[1]);
    }

    private String extractQueryString(String uri){
        if (!uri.contains("?")){
            return uri;
        }
        int index = uri.indexOf("?");
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        final Map<String, String> login = new HashMap<>();
        for (String query : queries){
            String[] split = query.split("=");
            login.put(split[0], split[1]);
        }
        Optional<User> account = InMemoryUserRepository.findByAccount(login.get("account"));
        if (account.isPresent()){
            return "/index.html";
        }
        return "/401.html";
    }

    public String getResponse(String uri) throws IOException {
        if ("/".equals(uri)){
            return "Hello world!";
        }
        if (!uri.contains("html")){
            uri += ".html";
        }

        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
