package nextstep.core;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.mvc.Handler;
import nextstep.jwp.core.mvc.ModelAndView;
import nextstep.jwp.core.mvc.mapping.MethodHandlerMapping;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApplicationContextTest {

    @Test
    @DisplayName("컴포넌트 스캔")
    public void componentScan() throws Exception{
        final DefaultApplicationContext applicationContext = new DefaultApplicationContext("nextstep");
        final List<Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);
        Assertions.assertThat(beans).isNotEmpty();
    }

    @Test
    @DisplayName("핸들러 매핑")
    public void handlerMapping() throws Exception{
        //given
        final DefaultApplicationContext applicationContext = new DefaultApplicationContext("nextstep");
        final MethodHandlerMapping handlerMapping = new MethodHandlerMapping(
                applicationContext);
        final Handler handler = handlerMapping.findHandler(new HttpRequest() {
            @Override
            public HttpMethod httpMethod() {
                return HttpMethod.GET;
            }

            @Override
            public String httpUrl() {
                return "/";
            }

            @Override
            public String getAttribute(String key) {
                return null;
            }
        });

        Assertions.assertThat(handler).isNotNull();
        Assertions.assertThat(handler.matchUrl("/", HttpMethod.GET)).isTrue();
    }

//    @Test
//    @DisplayName("핸들러 매핑 실패")
//    public void handlerMapping_fail() throws Exception{
//        //given
//        final FrontHandler frontHandler = new FrontHandler("nextstep");
//        Assertions.assertThatThrownBy(() -> {
//            frontHandler.getResponse(new DefaultHttpRequest(HttpMethod.GET, "/abcabc"), new DefaultHttpResponse());
//        }).isInstanceOf(NotFoundHandlerException.class);
//    }

    @Test
    @DisplayName("modelAndView 테스트")
    public void modelAndView() throws Exception{
        //given
        final ModelAndView modelAndView = new ModelAndView("index.html", "/");

        Assertions.assertThat(modelAndView.getViewAsString())
                .containsIgnoringWhitespaces(getPage("index.html"));
    }

    private String getPage(String path) {
        try {
            final URL url = getClass().getClassLoader().getResource("static/" + path);
            byte[] body = Files.readAllBytes(new File(url.toURI()).toPath());
            return new String(body);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
