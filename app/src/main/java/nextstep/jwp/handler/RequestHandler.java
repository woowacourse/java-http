package nextstep.jwp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.view.HtmlViewResolver;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final List<ViewResolver> viewResolvers;

    public RequestHandler(Socket connection) {
        this(connection, List.of(new HtmlViewResolver()));
    }

    public RequestHandler(Socket connection,
                          List<ViewResolver> viewResolvers) {
        this.connection = Objects.requireNonNull(connection);
        this.viewResolvers = viewResolvers;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.of(inputStream);
            View view = findView(httpRequest.filepath());
            view.write(outputStream);

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private View findView(String filepath) throws IOException {
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver.isSuitable(filepath)) {
                return viewResolver.getView(filepath);
            }
        }
        throw new RuntimeException(String.format("해당 형식에 알맞은 view를 찾을 수 없습니다. -> %s", filepath));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
