package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void callsDoGetForGetRequest() throws Exception {
        //given
        RecordingController controller = new RecordingController();

        //when
        controller.service(request("GET"), new HttpResponse(new ByteArrayOutputStream()));

        //then
        assertThat(controller.calledMethod).isEqualTo("GET");
    }

    @Test
    void callsDoPostForPostRequest() throws Exception {
        //given
        RecordingController controller = new RecordingController();

        //when
        controller.service(request("POST"), new HttpResponse(new ByteArrayOutputStream()));

        //then
        assertThat(controller.calledMethod).isEqualTo("POST");
    }

    private HttpRequest request(String method) {
        return new HttpRequest(new RequestLine(method, "/login", "", "HTTP/1.1"), Map.of(), "");
    }

    private static class RecordingController extends AbstractController {
        private String calledMethod;

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            calledMethod = "GET";
        }

        @Override
        protected void doPost(HttpRequest request, HttpResponse response) {
            calledMethod = "POST";
        }
    }
}
