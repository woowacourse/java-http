package nextstep.jwp.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {
    private String testDirectory = "./src/test/resources/static/";

    @Test
    void responseOk() throws FileNotFoundException {
        // responseOk.txt 결과는 응답 body에 index.html이 포함되어 있어야 함
        HttpResponse httpResponse = new HttpResponse(createOutputStream("responseOk.txt"));
        httpResponse.forward("/index.html");

        assertThat(new File(testDirectory + "responseOk.txt")).isNotNull();
    }

    @Test
    void responseFound() throws FileNotFoundException {
        // responseFound.txt 결과는 응답 header의 Location 정보에 /index.html이 포함되어 있어야 함
        HttpResponse httpResponse = new HttpResponse(createOutputStream("responseFound.txt"));
        httpResponse.redirect("/index.html");

        assertThat(new File(testDirectory + "responseFound.txt")).isNotNull();
    }

    @Test
    void responseNotFound() throws FileNotFoundException {
        // responseNotFound.txt 결과는 응답 body에 404.html이 포함되어 있어야 함
        HttpResponse httpResponse = new HttpResponse(createOutputStream("responseNotFound.txt"));
        httpResponse.forward("/strangeUrl");

        assertThat(new File(testDirectory + "responseNotFound.txt")).isNotNull();
    }

    private OutputStream createOutputStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + fileName));
    }
}
