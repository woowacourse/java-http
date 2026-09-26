package support;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.coyote.http11.HttpResponse;

public final class ResponseAssertions {
    private ResponseAssertions() {
    }

    public static void assertHtml(HttpResponse response, String path) throws IOException {
        assertHtml(serialize(response), resourceBytes(path));
    }

    public static void assertHtml(String response, byte[] expectedBody) {
        String[] parts = response.split("\r\n\r\n", 2);
        assertThat(parts).hasSize(2);
        String[] lines = parts[0].split("\r\n");
        assertThat(lines[0]).isEqualTo("HTTP/1.1 200 OK");
        assertThat(Arrays.copyOfRange(lines, 1, lines.length)).contains(
                "Content-Type: text/html;charset=utf-8", "Content-Length: " + expectedBody.length);
        assertThat(parts[1]).isEqualTo(new String(expectedBody, StandardCharsets.UTF_8));
    }

    public static void assertRedirect(HttpResponse response, String location) throws IOException {
        String[] parts = serialize(response).split("\r\n\r\n", 2);
        assertThat(parts).hasSize(2);
        String[] lines = parts[0].split("\r\n");
        assertThat(lines[0]).isEqualTo("HTTP/1.1 302 Found");
        assertThat(Arrays.copyOfRange(lines, 1, lines.length))
                .contains("Location: " + location, "Content-Length: 0");
        assertThat(parts[1]).isEmpty();
    }

    public static byte[] resourceBytes(String path) throws IOException {
        try (var input = ResponseAssertions.class.getClassLoader().getResourceAsStream("static" + path)) {
            assertThat(input).as("테스트 대상 리소스: %s", path).isNotNull();
            return input.readAllBytes();
        }
    }

    private static String serialize(HttpResponse response) throws IOException {
        var output = new ByteArrayOutputStream();
        response.writeTo(output);
        return output.toString(StandardCharsets.UTF_8);
    }
}
