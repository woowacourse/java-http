package study;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Java I/O Stream 클래스 학습 테스트")
class IOStreamTest {

    @Nested
    class OutputStream_학습_테스트 {

        @Test
        void OutputStream은_데이터를_바이트로_처리한다() throws IOException {
            final byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
            final OutputStream outputStream = new ByteArrayOutputStream(bytes.length);

            outputStream.write(bytes);

            final String actual = outputStream.toString();

            assertThat(actual).isEqualTo("nextstep");
            outputStream.close();
        }

        @Test
        void BufferedOutputStream을_사용하면_버퍼링이_가능하다() throws IOException {
            final OutputStream outputStream = mock(BufferedOutputStream.class);

            outputStream.flush();

            verify(outputStream, atLeastOnce()).flush();
            outputStream.close();
        }

        @Test
        void OutputStream은_사용하고_나서_close_처리를_해준다() {

            try (final OutputStream outputStream = mock(OutputStream.class)) {
                outputStream.close();
                verify(outputStream, atLeastOnce()).close();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @Nested
    class InputStream_학습_테스트 {

        @Test
        void InputStream은_데이터를_바이트로_읽는다() throws IOException {
            byte[] bytes = {-16, -97, -92, -87};
            final InputStream inputStream = new ByteArrayInputStream(bytes);

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String actual = br.readLine();

            assertThat(actual).isEqualTo("🤩");
            assertThat(inputStream.read()).isEqualTo(-1);
            inputStream.close();
        }

        @Test
        void InputStream은_사용하고_나서_close_처리를_해준다() {

            try (final InputStream inputStream = mock(InputStream.class)) {
                inputStream.close();
                verify(inputStream, atLeastOnce()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    class FilterStream_학습_테스트 {

        @Test
        void 필터인_BufferedInputStream를_사용해보자() throws IOException {
            final String text = "필터에 연결해보자.";
            final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
            final InputStream bufferedInputStream = new BufferedInputStream(inputStream);

            final byte[] actual = bufferedInputStream.readAllBytes();

            assertThat(bufferedInputStream).isInstanceOf(FilterInputStream.class);
            assertThat(actual).isEqualTo("필터에 연결해보자.".getBytes());
        }
    }

    @Nested
    class InputStreamReader_학습_테스트 {

        @Test
        void BufferedReader를_사용하여_문자열을_읽어온다() throws IOException {
            final String emoji = String.join("\r\n",
                    "😀😃😄😁😆😅😂🤣🥲☺️😊",
                    "😇🙂🙃😉😌😍🥰😘😗😙😚",
                    "😋😛😝😜🤪🤨🧐🤓😎🥸🤩",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(emoji.getBytes());

            final StringBuilder actual = new StringBuilder();
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = br.readLine()) != null) {
                actual.append(line).append("\r\n");
            }

            assertThat(actual).hasToString(emoji);
        }
    }
}
