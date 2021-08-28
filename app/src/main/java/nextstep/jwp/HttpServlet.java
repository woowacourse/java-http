package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpServlet {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public HttpServlet(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void doProcess() throws IOException {
        HttpRequest request = new HttpRequest(inputStream);
        HttpResponse response = new HttpResponse(outputStream);
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
