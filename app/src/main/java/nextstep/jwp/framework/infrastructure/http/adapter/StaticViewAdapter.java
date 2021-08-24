package nextstep.jwp.framework.infrastructure.http.adapter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import nextstep.jwp.framework.infrastructure.http.HttpHandler;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.util.StaticFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticViewAdapter implements RequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpHandler.class);
    private static final List<Path> STATIC_FILE_PATHS =
        StaticFileLoader.loadStaticFilePaths("static");
    private static final String RESPONSE_FORMAT =
        String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: %d ", "", "%s"
        );

    private final Class<?> target;
    private final Method method;

    public StaticViewAdapter(Class<?> target, Method method) {
        method.setAccessible(true);
        this.target = target;
        this.method = method;
    }

    public static StaticViewAdapter notFound() {
        return null;
    }

    @Override
    public String execute(HttpRequest httpRequest) {
        try {
            Constructor<?> declaredConstructor = target.getConstructor();
            Object target = declaredConstructor.newInstance();
            String resourcePath = (String) method.invoke(target);
            return writeResponse(resourcePath);
        } catch (InstantiationException | InvocationTargetException
            | NoSuchMethodException | IllegalAccessException e) {
            log.error("Method Invoke or Bean Instantiation Error", e);
        }
        return null;
    }

    private String writeResponse(String resourcePath) {
        Path path = STATIC_FILE_PATHS.stream()
            .filter(filePath -> filePath.toString().endsWith(resourcePath))
            .findAny()
            .orElseGet(this::find404Path);
        try {
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            return String.format(RESPONSE_FORMAT, responseBody.getBytes().length, responseBody);
        } catch (IOException exception) {
            log.error("Exception IO", exception);
        }
        return "";
    }

    private Path find404Path() {
        return STATIC_FILE_PATHS.stream()
            .filter(path -> path.toString().endsWith("/static/404.html"))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("404 Page를 찾을 수 없습니다."));
    }
}
