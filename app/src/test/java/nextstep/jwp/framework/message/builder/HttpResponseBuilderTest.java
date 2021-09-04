package nextstep.jwp.framework.message.builder;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.common.MediaType;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertContainsBodyString;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertEmptyBody;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class HttpResponseBuilderTest {

    @DisplayName("상태 코드로 HttpResponse 생성")
    @Test
    void status() {
        HttpResponseMessage httpResponse = HttpResponseBuilder.status(HttpStatusCode.CREATED).build();
        assertStatusCode(httpResponse, HttpStatusCode.CREATED);
        assertEmptyBody(httpResponse);
    }

    @DisplayName("OK 상태 코드의 body 없는 HttpResponse 생성")
    @Test
    void ok() {
        HttpResponseMessage httpResponse = HttpResponseBuilder.ok().build();
        assertStatusCode(httpResponse, HttpStatusCode.OK);
        assertEmptyBody(httpResponse);
    }

    @DisplayName("OK 상태 코드의 body 있는 HttpResponse 생성")
    @Test
    void okWithBody() {
        String body = "hell world";
        HttpResponseMessage httpResponse = HttpResponseBuilder.ok(body).build();
        assertStatusCode(httpResponse, HttpStatusCode.OK);
        assertContainsBodyString(httpResponse, body);
    }

    @DisplayName("FOUND 상태 코드의 HttpResponse 생성")
    @Test
    void redirectTemporarily() {
        String redirectUri = "/index.html";
        HttpResponseMessage httpResponse = HttpResponseBuilder.redirectTemporarily(redirectUri).build();
        assertStatusCode(httpResponse, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponse, "Location", redirectUri);
    }

    @DisplayName("지정한 html 로 포워드 하여 HttpResponse 생성")
    @Test
    void staticHtmlResource() {
        String path = "/test.html";
        HttpResponseMessage httpResponse = HttpResponseBuilder.staticResource(path).build();

        assertStatusCode(httpResponse, HttpStatusCode.OK);
        assertHeaderIncludes(httpResponse, "Content-Type", MediaType.TEXT_HTML_CHARSET_UTF8.getValue());
        assertContainsBodyString(httpResponse, "test body");
    }

    @DisplayName("정적 리소스로 HttpResponse 생성")
    @Test
    void staticResource() {
        String path = "/test.js";
        HttpResponseMessage httpResponse = HttpResponseBuilder.staticResource(path).build();

        assertStatusCode(httpResponse, HttpStatusCode.OK);
        assertHeaderIncludes(httpResponse, "Content-Type", MediaType.TEXT_JS_CHARSET_UTF8.getValue());
        assertContainsBodyString(httpResponse, "test javascript");
    }

    @DisplayName("Header 를 추가하여 HttpResponse 생성")
    @Test
    void header() {
        HttpResponseMessage httpResponse = HttpResponseBuilder.ok()
                .header("hello", "world")
                .header("id", "ggyool")
                .build();
        assertHeaderIncludes(httpResponse, "hello", "world");
        assertHeaderIncludes(httpResponse, "id", "ggyool");
    }

    @DisplayName("특정 Header 를 추가하여 HttpResponse 생성")
    @Test
    void specificHeader() {
        HttpResponseMessage httpResponse = HttpResponseBuilder.ok()
                .contentType(MediaType.TEXT_HTML_CHARSET_UTF8)
                .contentLength(100)
                .build();
        assertHeaderIncludes(httpResponse, "Content-Type", MediaType.TEXT_HTML_CHARSET_UTF8.getValue());
        assertHeaderIncludes(httpResponse, "Content-Length", "100");
    }
}
