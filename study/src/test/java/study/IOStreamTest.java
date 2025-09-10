package study;

import java.util.Arrays;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.*;

/**
 * 자바는 스트림(Stream)으로부터 I/O를 사용한다.
 * 입출력(I/O)은 하나의 시스템에서 다른 시스템으로 데이터를 이동 시킬 때 사용한다.
 *
 * InputStream은 데이터를 읽고, OutputStream은 데이터를 쓴다.
 * FilterStream은 InputStream이나 OutputStream에 연결될 수 있다.
 * FilterStream은 읽거나 쓰는 데이터를 수정할 때 사용한다. (e.g. 암호화, 압축, 포맷 변환)
 *
 * Stream은 데이터를 바이트로 읽고 쓴다.
 * 바이트가 아닌 텍스트(문자)를 읽고 쓰려면 Reader와 Writer 클래스를 연결한다.
 * Reader, Writer는 다양한 문자 인코딩(e.g. UTF-8)을 처리할 수 있다.
 */
@DisplayName("Java I/O Stream 클래스 학습 테스트")
class IOStreamTest {

    /**
     * OutputStream 학습하기
     *
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
         * 
         * write 메서드는 데이터를 바이트로 출력하기 때문에 비효율적이다.
         * <code>write(byte[] data)</code>와 <code>write(byte b[], int off, int len)</code> 메서드는
         * 1바이트 이상을 한 번에 전송 할 수 있어 훨씬 효율적이다.
         */
        //근데, write(bytes[])도 내부적으로는 write(int b)를 호출하는데, 성능상 차이는 없는 것 아닌가?
        //뭔가, ByteArrayOutputStream과 같이 서브클래스들이 버퍼에서 처리를 하는 것이니까 효율적이라고 하는건가?
        @Test
        void OutputStream은_데이터를_바이트로_처리한다() throws IOException {
            final byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
            //내부 버퍼의 길이를 지정했음.
            final OutputStream outputStream = new ByteArrayOutputStream(bytes.length);
            /**
             * todo
             * OutputStream 객체의 write 메서드를 사용해서 테스트를 통과시킨다
             */
            //ByteArrayOutputSteam에 write(byte[] bytes)를 받는 메서드가 없음,
            //그래서 OutputStream(부모)에 있는 write(byte[] b) 메서드를 사용하게 됨.
            //그러면, 부모의 write(byte[] b)에서 사용하는 write(byte[] b, int off, int len)을 ByteArrayOutputStream에서 재구현함
            //그래서 결국 최적화된 write를 사용하게 됨.
            outputStream.write(bytes);
            final String actual = outputStream.toString();

//            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
//            byteArrayOutputStream.writeBytes(bytes);
//            final String actual = byteArrayOutputStream.toString();
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
        //버퍼링: 데이터를 임시로 버퍼에 모아두었다가 한 번에 전송하는 것
        //시스템 호출 횟수가 감소해서 성능이 향상됨.
        //BufferedOutputStream은 write를 할 때 버퍼가 꽉 차면 flush를 날리네,
        //근데, buffer를 growIfNeeded를 수행하고 있는데,
        //그러면, buffer가 늘어날대로 늘어나다가(인트 사이즈) 꽉차면 flush를 수행하는 것
        //BufferedOutputStream에서 flush 메서드가 오버라이딩 되어있는데, 이것은
        //버퍼가 꽉차지 않아도 flush할 수 있는 방법임.
        @Test
        void BufferedOutputStream을_사용하면_버퍼링이_가능하다() throws IOException {
            final OutputStream outputStream = mock(BufferedOutputStream.class);

            /**
             * todo
             * flush를 사용해서 테스트를 통과시킨다.
             * ByteArrayOutputStream과 어떤 차이가 있을까?
             */
            //버퍼에서 flush를 하면 하위 스트림에서 flush가 발생하도로 됨.
            //그래서 다른 스레드는 하위 스트림으로 버퍼를 보내는 동안 접근하지 못함
            //ByteArrayOutputStream → 사실상 아무 일도 안 함
            //BufferedOutputStream → 버퍼 내용을 하위 스트림으로 강제로 내보냄, 동시에 동기화까지 적용
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
//            outputStream = mock(OutputStream.class);
            /**
             * todo
             * try-with-resources를 사용한다.
             * java 9 이상에서는 변수를 try-with-resources로 처리할 수 있다.
             * 근데, final이 붙여야 닫힘! final을 붙이지 않으면 자동으로 close가 발생하지 않음.
             * 또는, 변수에 대한 재할당이 없는 경우에 close가 발생함 (effectively final)
             * 재할당이 있으면 컴파일 시점에 컴파일 에러가 발생함.
             */
//            outputStream.close();
            try (outputStream) {

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
    @Nested
    class InputStream_학습_테스트 {

        /**
         * read() 메서드는 매체로부터 단일 바이트를 읽는데, 0부터 255 사이의 값을 int 타입으로 반환한다.
         * int 값을 byte 타입으로 변환하면 -128부터 127 사이의 값으로 변환된다.
         * 그리고 Stream 끝에 도달하면 -1을 반환한다.
         */
        //inputStream은 기본적으로 데이터를 바이트 단위로 읽을 수 있도록 추상화됨.
        @Test
        void InputStream은_데이터를_바이트로_읽는다() throws IOException {
            byte[] bytes = {-16, -97, -92, -87};
            final InputStream inputStream = new ByteArrayInputStream(bytes);

            /**
             * todo
             * inputStream에서 바이트로 반환한 값을 문자열로 어떻게 바꿀까?
             */
            final String actual = new String(inputStream.readAllBytes());
            int eof = inputStream.read();
            inputStream.close();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual).isEqualTo("🤩");
                softAssertions.assertThat(eof).isEqualTo(-1);
            });

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
            inputStream.close();

            verify(inputStream, atLeastOnce()).close();
        }
    }

    /**
     * FilterStream 학습하기
     *
     * 필터는 필터 스트림, reader, writer로 나뉜다.
     * 필터는 바이트를 다른 데이터 형식으로 변환 할 때 사용한다.
     * reader, writer는 UTF-8, ISO 8859-1 같은 형식으로 인코딩된 텍스트를 처리하는 데 사용된다.
     */
    @Nested
    class FilterStream_학습_테스트 {

        /**
         * BufferedInputStream은 데이터 처리 속도를 높이기 위해 데이터를 버퍼에 저장한다.
         * InputStream 객체를 생성하고 필터 생성자에 전달하면 필터에 연결된다.
         * 버퍼 크기를 지정하지 않으면 버퍼의 기본 사이즈는 얼마일까?
         */
        //버퍼 크기를 지정하지 않으면 8192의 기본 버퍼 사이즈를 가진다. -> 8KB
        //아, 그래서 8KB 단위로 데이터를 미리 읽어와서 메모리에 담아두니까, 필요할 때 조금씩 꺼내 쓰니까
        //그래서 I/O 성능이 올라가는거구나

        @Test
        void 필터인_BufferedInputStream를_사용해보자() throws IOException {
            final String text = "필터에 연결해보자.";
            //필터에 연결해보자.의 데이터를 바이트로 읽기 위함
            final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
            final InputStream bufferedInputStream = new BufferedInputStream(inputStream);

            final byte[] actual = bufferedInputStream.readAllBytes();

            assertThat(bufferedInputStream).isInstanceOf(FilterInputStream.class);
            assertThat(actual).isEqualTo("필터에 연결해보자.".getBytes());
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
        void BufferedReader를_사용하여_문자열을_읽어온다() throws IOException {
            final String emoji = String.join("\r\n",
                    "😀😃😄😁😆😅😂🤣🥲☺️😊",
                    "😇🙂🙃😉😌😍🥰😘😗😙😚",
                    "😋😛😝😜🤪🤨🧐🤓😎🥸🤩",
                    "");
            //제공이 되어 있네, 그러면 일단 inputStream은 바이트 단위로 해당 emoji를 읽어왔고.
            final InputStream inputStream = new ByteArrayInputStream(emoji.getBytes());
            //바이트 단위를 char로 읽어와서
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            final StringBuilder actual = new StringBuilder();
            int code;
            while ((code = inputStreamReader.read()) != -1) {
                actual.append((char)code);
            }
//            라인별로 읽기 위해, inputstreamReader는 하나씩밖에 못 읽으니까
//            final BufferedReader br = new BufferedReader(inputStreamReader);
//            String line;
//            while ((line = br.readLine()) != null) {
//                actual.append(line).append("\r\n");
//            }

            assertThat(actual).hasToString(emoji);
        }
    }
}
