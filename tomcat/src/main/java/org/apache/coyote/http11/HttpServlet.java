package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.FileIOUtils;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public abstract class HttpServlet {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    protected void service(final HttpRequest req, final HttpResponse res) throws IOException, URISyntaxException {
        if (req.isSameMethod(METHOD_GET)) {
            doGet(req, res);
            if(res.isNotRedirect()){
                //FIXME: 임시로 요청을 구별하기 위한 코드
                if(!req.getPath().contains(".")){
                    res.setHttpResponseStartLine(StatusCode.OK);
                    Path path = FileIOUtils.getPath(PREFIX + req.getPath() + SUFFIX);
                    res.setResponseBody(Files.readAllBytes(path));
                    res.addHeader(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path) + "; charset=utf-8");
                }
            }
        }
        if (req.isSameMethod(METHOD_POST)) {
            doPost(req, res);
        }
    }

    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void doPost(final HttpRequest req, final HttpResponse resp) {
        throw new UnsupportedOperationException();
    }
}
