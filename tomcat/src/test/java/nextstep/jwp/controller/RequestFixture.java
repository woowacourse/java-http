package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.http11.common.HttpBody;
import org.apache.coyote.http11.common.HttpUri;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpStartLine;

public class RequestFixture {

    public static final HttpRequest POST_REQUEST = new HttpRequest(
            HttpRequestHeaders.from(List.of("")),
            new HttpStartLine(HttpMethod.POST, HttpUri.from("/test"), HttpVersion.V1_1),
            HttpBody.createEmptyHttpBody()
    );

    public static final HttpRequest GET_REQUEST = new HttpRequest(
            HttpRequestHeaders.from(List.of("")),
            new HttpStartLine(HttpMethod.GET, HttpUri.from("/test"), HttpVersion.V1_1),
            HttpBody.createEmptyHttpBody()
    );

    public static final HttpRequest REGISTER_REQUEST = new HttpRequest(
            HttpRequestHeaders.from(List.of("")),
            new HttpStartLine(HttpMethod.POST, HttpUri.from("/test"), HttpVersion.V1_1),
            HttpBody.from("account=test&email=test@email.com&password=password")
    );


}
