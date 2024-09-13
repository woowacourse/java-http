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

/**
 * 자바는 스트림(Stream)으로부터 I/O를 사용한다.
 * 입출력(I/O)은 하나의 시스템에서 다른 시스템으로 데이터를 이동 시킬 때 사용한다.
 * <p>
 * InputStream은 데이터를 읽고, OutputStream은 데이터를 쓴다.
 * FilterStream은 InputStream이나 OutputStream에 연결될 수 있다.
 * FilterStream은 읽거나 쓰는 데이터를 수정할 때 사용한다. (e.g. 암호화, 압축, 포맷 변환)
 * <p>
 * Stream은 데이터를 바이트로 읽고 쓴다.
 * 바이트가 아닌 텍스트(문자)를 읽고 쓰려면 Reader와 Writer 클래스를 연결한다.
 * Reader, Writer는 다양한 문자 인코딩(e.g. UTF-8)을 처리할 수 있다.
 */
@DisplayName("Java I/O Stream 클래스 학습 테스트")
class IOStreamTest {

    /**
     * OutputStream 학습하기
     * <p>
     * 자바의 기본 출력 클래스는 java.io.OutputStream이다.
     * OutputStream의 write(int b) 메서드는 기반 메서드이다.
     * <code>public abstract void write(int b) throws IOException;</code>
     */
    @Nested
    class OutputStream_학습_테스트 {

        /**
         * OutputStream은 다른 매체에 바이트로 데이터를 쓸 때 사용한다.
         * OutputStream의 서브 클래스(subclass)는 특정 매체에 데이터를 쓰기 위해 write(int b) 메서드를 사용한다.
         * 예를 들어, FilterOutputStream은 파일로 데이터를 쓸 때,
         * 또는 DataOutputStream은 자바의 primitive type data를 다른 매체로 데이터를 쓸 때 사용한다.
         * <p>
         * write 메서드는 데이터를 바이트로 출력하기 때문에 비효율적이다.
         * <code>write(byte[] data)</code>와 <code>write(byte b[], int off, int len)</code> 메서드는
         * 1바이트 이상을 한 번에 전송 할 수 있어 훨씬 효율적이다.
         */
        @Test
        void OutputStream은_데이터를_바이트로_처리한다() throws IOException {
            final byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
            final OutputStream outputStream = new ByteArrayOutputStream(bytes.length);

            // write() 메서드 내부적으로는 자동으로 크기가 조정되는 바이트 배열에 데이터를 저장하고 있다.
            outputStream.write(bytes);
            String actual = outputStream.toString();


            assertThat(actual).isEqualTo("nextstep");
            outputStream.close();
        }

        /**
         * 효율적인 전송을 위해 스트림에서 버퍼링을 사용 할 수 있다.
         * BufferedOutputStream 필터를 연결하면 버퍼링이 가능하다.
         *
         * 버퍼링을 사용하면 OutputStream을 사용할 때 flush를 사용하자.
         * flush() 메서드는 버퍼가 아직 가득 차지 않은 상황에서 강제로 버퍼의 내용을 전송한다.
         * Stream은 동기(synchronous)로 동작하기 때문에 버퍼가 찰 때까지 기다리면
         * 데드락(deadlock) 상태가 되기 때문에 flush로 해제해야 한다.
         */
        /**
         * ByteArrayOutputStream과 어떤 차이가 있을까?
         *
         * `ByteArrayOutputStream`은 데이터를 바이트 배열로 저장하기 위한 출력 스트림이다.
         * 이 스트림에 쓰여진 데이터는 메모리 내에서 `byte[]` 형태로 누적된다.
         * 내부적으로는 자동으로 크기가 조정되는 바이트 배열을 사용하여 데이터를 저장한다.
         * 주로, 임시로 데이터를 저장하거나 다른 출력 스트림으로 전달하기 전 데이터를 준비하는 용도로 쓴다.
         *
         * `BufferedOutputStream`은 출력 성능을 향상시키기 위해 버퍼링을 제공하는 출력 스트림이다.
         * 주로 다른 출력 스트림(예: `FileOutputStream` 등)에 버퍼링 기능을 추가할 때 사용된다.
         * 기본적으로 데이터를 한 번에 출력하지 않고, 내부 버퍼에 데이터를 모아 두었다가, 버퍼가 가득 차거나 스트림이 닫힐 때 데이터를 한꺼번에 출력한다.
         * 디스크나 네트워크와 같은 느린 I/O 장치에 데이터를 출력할 때, 성능을 최적화하기 위해 사용된다.
         * 자주 사용되는 작은 단위의 쓰기 작업을 버퍼링하여 불필요한 I/O 호출을 줄이고 성능을 향상시킬 수 있다.
         */
        /**
         * 버퍼링이란?
         * <p>
         * 버퍼링은 데이터를 임시로 메모리에 저장한 다음, 일정량이 쌓였을 때 한 번에 출력 장치로 전송하는 방법이다.
         * 이는 I/O 작업의 효율성을 높이고 성능을 개선하기 위해 사용된다.
         * 특히, 파일이나 네트워크와 같은 외부 장치와의 데이터 전송에서 유용하다.
         */
        @Test
        void BufferedOutputStream을_사용하면_버퍼링이_가능하다() throws IOException {
            final OutputStream outputStream = mock(BufferedOutputStream.class);

            outputStream.flush();
            verify(outputStream, atLeastOnce()).flush();
            outputStream.close();
        }

        /**
         * 스트림 사용이 끝나면 항상 close() 메서드를 호출하여 스트림을 닫는다.
         * 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
         */
        @Test
        void OutputStream은_사용하고_나서_close_처리를_해준다() throws IOException {
            final OutputStream outputStream = mock(OutputStream.class);

            /**
             * todo
             * try-with-resources를 사용한다.
             * java 9 이상에서는 변수를 try-with-resources로 처리할 수 있다.
             */

            try (outputStream) {

            } catch (IOException e) {
            }
            verify(outputStream, atLeastOnce()).close();
        }
    }

    /**
     * InputStream 학습하기
     *
     * 자바의 기본 입력 클래스는 java.io.InputStream이다.
     * InputStream은 다른 매체로부터 바이트로 데이터를 읽을 때 사용한다.
     * InputStream의 read() 메서드는 기반 메서드이다.
     * <code>public abstract int read() throws IOException;</code>
     *
     * InputStream의 서브 클래스(subclass)는 특정 매체에 데이터를 읽기 위해 read() 메서드를 사용한다.
     */
    /**
     * InputStream.read()는 왜 int 타입으로 반환할까?
     * <p>
     * 1. 바이트 값을 정수형으로 반환한다.
     * byte 타입은 8비트로 표현되며 -128부터 127까지의 범위를 가지므로, 바이트 데이터를 부호 없는 형태로 처리할 수 었다.
     * 반면 int 타입은 32비트 정수형으로 부호 없는 바이트 값을 0부터 255까지 정확하게 표현할 수 있다.
     * <p>
     * 2. 스트림의 끝을 나타내기 위해서이다.
     * read() 메서드는 스트림의 끝에 도달했을 때 -1을 반환한다.
     * byte 타입으로는 -1을 직접 표현할 수 없기 때문에 int 타입으로 스트림의 끝을 명확하게 표현할 수 있다.
     */
    @Nested
    class InputStream_학습_테스트 {

        /**
         * read() 메서드는 매체로부터 단일 바이트를 읽는데, 0부터 255 사이의 값을 int 타입으로 반환한다.
         * int 값을 byte 타입으로 변환하면 -128부터 127 사이의 값으로 변환된다.
         * 그리고 Stream 끝에 도달하면 -1을 반환한다.
         */
        @Test
        void InputStream은_데이터를_바이트로_읽는다() throws IOException {
            byte[] bytes = {-16, -97, -92, -87};
            final InputStream inputStream = new ByteArrayInputStream(bytes);

            /**
             * todo
             * inputStream에서 바이트로 반환한 값을 문자열로 어떻게 바꿀까?
             */
            byte[] result = inputStream.readAllBytes();
            final String actual = new String(result, StandardCharsets.UTF_8);

            assertThat(actual).isEqualTo("🤩");
            assertThat(inputStream.read()).isEqualTo(-1);
            inputStream.close();
        }

        /**
         * 스트림 사용이 끝나면 항상 close() 메서드를 호출하여 스트림을 닫는다.
         * 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
         */
        @Test
        void InputStream은_사용하고_나서_close_처리를_해준다() throws IOException {
            final InputStream inputStream = mock(InputStream.class);

            /**
             * todo
             * try-with-resources를 사용한다.
             * java 9 이상에서는 변수를 try-with-resources로 처리할 수 있다.
             */
            try (inputStream) {

            } catch (IOException e) {
            }
            verify(inputStream, atLeastOnce()).close();
        }
    }

    /**
     * FilterStream 학습하기
     * <p>
     * 필터는 필터 스트림, reader, writer로 나뉜다.
     * 필터는 바이트를 다른 데이터 형식으로 변환 할 때 사용한다.
     * reader, writer는 UTF-8, ISO 8859-1 같은 형식으로 인코딩된 텍스트를 처리하는 데 사용된다.
     */
    @Nested
    class FilterStream_학습_테스트 {

        /**
         * BufferedInputStream은 데이터 처리 속도를 높이기 위해 데이터를 버퍼에 저장한다.
         * InputStream 객체를 생성하고 필터 생성자에 전달하면 필터에 연결된다.
         * 버퍼 크기를 지정하지 않으면 버퍼의 기본 사이즈는 얼마일까? -> 8192
         */
        @Test
        void 필터인_BufferedInputStream를_사용해보자() throws IOException {
            final String text = "필터에 연결해보자.";
            try (
                    final InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
                    final InputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ) {
                final byte[] actual = bufferedInputStream.readAllBytes();
                assertThat(bufferedInputStream).isInstanceOf(FilterInputStream.class);
                assertThat(actual).isEqualTo("필터에 연결해보자.".getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * 자바의 기본 문자열은 UTF-16 유니코드 인코딩을 사용한다.
     * 문자열이 아닌 바이트 단위로 처리하려니 불편하다.
     * 그리고 바이트를 문자(char)로 처리하려면 인코딩을 신경 써야 한다.
     * reader, writer를 사용하면 입출력 스트림을 바이트가 아닌 문자 단위로 데이터를 처리하게 된다.
     * 그리고 InputStreamReader를 사용하면 지정된 인코딩에 따라 유니코드 문자로 변환할 수 있다.
     */
    @Nested
    class InputStreamReader_학습_테스트 {

        /**
         * InputStreamReader를 사용해서 바이트를 문자(char)로 읽어온다.
         * 읽어온 문자(char)를 문자열(String)로 처리하자.
         * 필터인 BufferedReader를 사용하면 readLine 메서드를 사용해서 문자열(String)을 한 줄 씩 읽어올 수 있다.
         */
        @Test
        void BufferedReader를_사용하여_문자열을_읽어온다() {
            final String emoji = String.join(System.lineSeparator(),
                    "😀😃😄😁😆😅😂🤣🥲☺️😊",
                    "😇🙂🙃😉😌😍🥰😘😗😙😚",
                    "😋😛😝😜🤪🤨🧐🤓😎🥸🤩",
                    "");
            try (
                    final InputStream inputStream = new ByteArrayInputStream(emoji.getBytes(StandardCharsets.UTF_8));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            ) {
                final StringBuilder actual = new StringBuilder();
                reader.lines().forEach(line -> actual.append(line).append(System.lineSeparator()));
                assertThat(actual).hasToString(emoji);
            } catch (IOException e) {
            }
        }
    }
}
