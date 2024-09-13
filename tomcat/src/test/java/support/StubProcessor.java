package support;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.resolver.Dispatcher;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Http11Reader;
import org.apache.coyote.http11.Http11Writer;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StubProcessor extends Http11Processor {
    public StubProcessor(Socket connection) {
        super(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var reader = new Http11Reader(connection.getInputStream());
             var writer = new Http11Writer(connection.getOutputStream())) {
            writer.flushWith(String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 12 ",
                    "",
                    "Hello world!"));
        } catch (IOException | UncheckedServletException e) {
        }
    }

}
