package nextstep.jwp.controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import nextstep.jwp.infrastructure.functionalinterface.CheckedBiConsumer;
import nextstep.jwp.infrastructure.http.FileResolver;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE_DELIMITER = ";";
    private static final String CHARSET_KEY = "charset=";
    private static final FileResolver FILE_RESOLVER = new FileResolver("static");
    private final Map<Method, CheckedBiConsumer<HttpRequest, HttpResponse>> methodMap;

    protected AbstractController() {
        methodMap = new HashMap<>();
        methodMap.put(Method.GET, this::doGet);
        methodMap.put(Method.POST, this::doPost);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final Method method = request.getRequestLine().getMethod();
        methodMap.get(method).accept(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void respondByFile(final String filePath, final HttpResponse response) {
        final String contentType = FILE_RESOLVER.contentType(filePath);
        final String charSet = Charset.defaultCharset().displayName().toLowerCase(Locale.ROOT);
        final String responseBody = FILE_RESOLVER.read(filePath);

        response.addHeader(CONTENT_TYPE,
            String.join(
                CONTENT_TYPE_DELIMITER,
                contentType,
                CHARSET_KEY + charSet
            )
        );
        response.addHeader(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        response.setMessageBody(responseBody);
    }
}
