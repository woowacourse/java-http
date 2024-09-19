package support;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpReader;
import org.apache.coyote.http11.HttpWriter;

public class StubProcessor extends Http11Processor {
    public StubProcessor(Socket connection) {
        super(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var reader = new HttpReader(connection.getInputStream());
             var writer = new HttpWriter(connection.getOutputStream())) {
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
