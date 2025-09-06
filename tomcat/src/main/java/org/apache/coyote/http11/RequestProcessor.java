package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
import com.techcourse.presentation.Controller;
import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.ParsedResourcePath;
import com.techcourse.presentation.ResponseWithType;
import com.techcourse.presentation.StaticResourceController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProcessor {

    private static final Map<String, Function<String, Boolean>> controllers = new HashMap<>();
    private static final Map<String, Supplier<Controller>> controllerFactory = new HashMap<>();

    static {
        controllers.put("LoginController", LoginController::isResponsible);
        controllers.put("StaticResourceController", StaticResourceController::isResponsible);

        controllerFactory.put("LoginController", () -> new LoginController(new LoginService()));
        controllerFactory.put("StaticResourceController", StaticResourceController::new);
    }

    public String process(final String requestLine) {
        final ParsedResourcePath request = parse(requestLine);

        final String className = controllers.entrySet().stream()
                .filter(entry -> entry.getValue().apply(request.path()))
                .map(Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 경로: " + request.path()));

        final Controller controller = controllerFactory.get(className).get();
        final ResponseWithType response = controller.getResource(request);

        return createSuccessMessage(response);
    }

    private ParsedResourcePath parse(final String requestLine) {
        if (!requestLine.startsWith("GET")) {
            throw new RuntimeException("현재는 GET 요청만 응답 가능합니다.");
        }

        final String url = requestLine.split(" ")[1];
        final String path = url.split("\\?")[0];
        final Map<String, String> params = new HashMap<>();

        if (url.contains("?")) {
            final String queries = url.split("\\?")[1];

            Arrays.stream(queries.split("&"))
                    .map(query -> Map.entry(query.split("=")[0], query.split("=")[1]))
                    .forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        }

        return new ParsedResourcePath(path, params);
    }

    private String createSuccessMessage(final ResponseWithType response) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + response.contentType() + ";charset=utf-8 ",
                "Content-Length: " + response.body().getBytes().length + " ",
                "",
                response.body());
    }
}
