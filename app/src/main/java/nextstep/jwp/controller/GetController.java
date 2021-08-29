package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetController {

    private static Logger log = LoggerFactory.getLogger(GetController.class);

    private GetController() {

    }

    public static void run(String requestURI, OutputStream outputStream,
        ClassLoader classLoader) throws IOException {
        String path;
        URL resource = null;
        if (requestURI.contains(".")) {
            path = "static" + requestURI;
            resource = classLoader.getResource(path);
        }
        if (!requestURI.contains(".") && !requestURI.contains("?")) {
            path = "static" + requestURI + ".html";
            resource = classLoader.getResource(path);
        }

        if (requestURI.contains("?")) {
            int index = requestURI.indexOf("?");
            path = requestURI.substring(0, index);
            String queryString = requestURI.substring(index + 1);

            String[] queries = queryString.split("&");
            List<String> values = new ArrayList<>();
            for (String query : queries) {
                values.add(query.split("=")[1]);
            }
            User user = InMemoryUserRepository.findByAccount(values.get(0)).orElseThrow();
            log.debug("account : {}, checkPassword : {}", user.getAccount(), user.checkPassword(values.get(1)));

            if (user.checkPassword(values.get(1))) {
                final String response = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /index.html"
                );
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else {
                final String response = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html"
                );
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
            return;
        }


        final Path filePath = new File(resource.getFile()).toPath();
        final String responseBody = Files.readString(filePath);

        final String response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
