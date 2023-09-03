package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static String handle(HttpRequest request) throws IOException {
        if (request.getUri().equals("/")) {
            return getResponse("Hello world!", ContentType.HTML);
        }

        if (isStaticFile(request.getUri())) {
            String fileUrl = "static" + request.getUri();
            File file = new File(
                RequestHandler.class
                    .getClassLoader()
                    .getResource(fileUrl)
                    .getFile()
            );
            String responseBody = new String(Files.readAllBytes(file.toPath()));
            return getResponse(responseBody, ContentType.from(file.getName()));
        }

        if (request.getUri().equals("/login")) {
            Map<String, String> queryString = request.getQueryString();
            User user = InMemoryUserRepository.findByAccount(queryString.get("account"))
                .orElseThrow(IllegalArgumentException::new);
            log.info(user.toString());
        }

        throw new IllegalArgumentException();
    }

    private static boolean isStaticFile(String target) {
        String value = target.substring(target.lastIndexOf(".") + 1);
        return Arrays.stream(ContentType.values())
            .anyMatch(it -> it.name().equalsIgnoreCase(value));
    }

    private static String getResponse(String responseBody, ContentType contentType) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: "+ contentType.value  + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
