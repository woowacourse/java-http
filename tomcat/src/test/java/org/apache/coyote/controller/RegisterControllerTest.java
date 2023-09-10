package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void get() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = new HttpResponse();
        HttpRequest request = new HttpRequest(HttpMethod.GET, "/register", null, null);

        registerController.service(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get("Content-Type")).isNotNull();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void post() throws Exception {
        RegisterController registerController = new RegisterController();
        String body = "account=1&password=1&email=abc@abc.com";
        HttpRequest request = new HttpRequest(HttpMethod.POST, "/register", null, body);
        HttpResponse response = new HttpResponse();

        registerController.service(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Location")).isEqualTo("/index.html");
    }


}
