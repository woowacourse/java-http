package nextstep.jwp.view;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class ViewTest {

    @DisplayName("String의 replace으로 원하는 값으로 수정한다.")
    @Test
    void parserWithReplaceAll() {
        String content = "<p class=\"error_message\">${error_message}</p>,"
                + "<p class=\"error_message\">${error_message}</p>";

        content = content.replace("${error_message}", "hi");

        assertThat(content).isEqualTo("<p class=\"error_message\">hi</p>,"
                + "<p class=\"error_message\">hi</p>");
    }

    @DisplayName("View에 model을 랜더링 할 수 있다.")
    @Test
    void render() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/400.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        Model model = new Model();
        model.addAttribute("errorMessage", "bad request");
        ModelAndView modelAndView = new ModelAndView(model, "/400.html", HttpStatus.BAD_REQUEST);

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpStatus(HttpStatus.BAD_REQUEST);

        View view = View.of("400.html", content);
        view.render(modelAndView, httpResponse);

        assertResponse(httpResponse.responseAsString(), HttpStatus.BAD_REQUEST,
                content.replace("${errorMessage}", "bad request"));
    }

    private static void assertResponse(String output, HttpStatus httpStatus, String body) {
        String expected = "HTTP/1.1 " + httpStatus.code() + " " + httpStatus.status() + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;
        assertThat(output).isEqualTo(expected);
    }
}
