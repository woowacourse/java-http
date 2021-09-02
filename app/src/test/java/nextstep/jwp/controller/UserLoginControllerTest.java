package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.HttpHeaders;
import nextstep.jwp.httpmessage.HttpSession;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httprequest.Parameters;
import nextstep.jwp.httpmessage.httprequest.RequestLine;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("UserLoginControllerTest 테스트")
class UserLoginControllerTest {

    @Test
    void 유저_재로그인_테스트() {
        //given
        final RequestLine mockRequestLine = mock(RequestLine.class);
        final HttpHeaders mockHttpHeaders = mock(HttpHeaders.class);
        final Parameters mockParameters = mock(Parameters.class);

        final HttpSession httpSession = new HttpSession("test-session");
        final UserLoginController userLoginController = new UserLoginController();

        //when
        httpSession.setAttribute("user", new User("gump", "gumpgump", "gump@guamp.com"));
        final HttpRequest httpRequest = new HttpRequest(mockRequestLine, mockHttpHeaders, mockParameters, httpSession);
        final HttpResponse httpResponse = new HttpResponse(httpRequest);
        final ModelView modelView = userLoginController.doGet(httpRequest, httpResponse);
        //then
        Assertions.assertThat(modelView.getViewName()).isEqualTo("/index.html");
    }
}
