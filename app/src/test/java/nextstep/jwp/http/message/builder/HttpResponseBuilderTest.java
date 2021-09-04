package nextstep.jwp.http.message.builder;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.common.MediaType;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertContainsBodyString;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertEmptyBody;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;
import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("지정한 path 로 포워드 하여 HttpResponse 생성")
    @Test
    void forward() {
        String path = "/test.html";
        HttpResponseMessage httpResponse = HttpResponseBuilder.forward(path).build();
        String body = httpResponse.getBody().asString();
        assertStatusCode(httpResponse, HttpStatusCode.OK);
        assertThat(body).contains("test body");
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
