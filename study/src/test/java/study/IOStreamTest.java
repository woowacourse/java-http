package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
            // "nextstep"을 구성하는 각 문자의 바이트 값을 배열에 저장한다.
            final byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
            // 출력한 바이트를 메모리에 저장할 ByteArrayOutputStream을 생성한다.
            final OutputStream outputStream = new ByteArrayOutputStream(bytes.length);

            /**
             * todo
             * OutputStream 객체의 write 메서드를 사용해서 테스트를 통과시킨다
             */
            /*
             * ByteArrayOutputStream은 전달받은 바이트를 다른 곳으로 전송하지 않고 내부 byte[]에 저장한다.
             * 출력 스트림 연결 구조에서는 데이터를 메모리에 모아두는 최종 저장 장소가 된다.
             * 생성자에 전달한 크기는 내부 배열의 초기 크기이며, 공간이 부족하면 배열이 자동으로 커진다.
             * 크기를 지정하지 않으면 기본 초기 크기는 32바이트이다.
             */
            // 바이트 배열 전체를 출력 스트림에 쓴다.
            outputStream.write(bytes);

            // 출력 스트림에 저장된 바이트들을 문자열로 변환한다.
            final String actual = outputStream.toString();

            assertThat(actual).isEqualTo("nextstep");
            // 일반적인 OutputStream은 파일이나 네트워크 자원의 누수를 막기 위해 사용 후 닫아야 한다.
            // ByteArrayOutputStream은 파일이나 네트워크가 아닌 메모리만 사용해 반환할 외부 자원이 없으므로
            // close()를 호출해도 실질적인 효과가 없다.
            outputStream.close();
        }

        /**
         * 효율적인 전송을 위해 스트림에서 버퍼링을 사용 할 수 있다.
         * BufferedOutputStream 필터를 연결하면 버퍼링이 가능하다.
         * <p>
         * 버퍼링을 사용하면 OutputStream을 사용할 때 flush를 사용하자.
         * flush() 메서드는 버퍼가 아직 가득 차지 않은 상황에서 강제로 버퍼의 내용을 전송한다.
         * Stream은 동기(synchronous)로 동작하기 때문에 버퍼가 찰 때까지 기다리면
         * 데드락(deadlock) 상태가 되기 때문에 flush로 해제해야 한다.
         */
        @Test
        void BufferedOutputStream을_사용하면_버퍼링이_가능하다() throws IOException {
            // flush 호출 여부를 확인하기 위한 가짜 BufferedOutputStream 객체를 만든다.
            final OutputStream outputStream = mock(BufferedOutputStream.class);

            /**
             * todo
             * flush를 사용해서 테스트를 통과시킨다.
             * ByteArrayOutputStream과 어떤 차이가 있을까?
             */
            /*
             * BufferedOutputStream은 데이터를 버퍼에 잠시 모아 출력 횟수를 줄인 뒤 연결된 OutputStream으로 보낸다.
             * 자체가 최종 저장 장소가 아니므로 전달할 OutputStream이 필요하며, ByteArrayOutputStream도 연결할 수 있다.
             * flush()는 버퍼가 가득 차지 않아도 남은 데이터를 연결된 OutputStream으로 즉시 전달한다.
             * 실제 객체의 close()는 flush()를 수행하지만, mock은 실제 로직을 실행하지 않아 flush()를 직접 호출해야 한다.
             */
            // 버퍼에 남아 있는 데이터를 기존 출력 스트림으로 내보내도록 요청한다.
            outputStream.flush();

            verify(outputStream, atLeastOnce()).flush();
            // 일반적인 BufferedOutputStream은 close()할 때 남은 데이터를 내보내고 자원을 반환한다.
            // 하지만 이 객체는 mock이므로 실제 close()와 내부 flush() 동작을 실행하지 않는다.
            outputStream.close();
        }

        /**
         * 스트림 사용이 끝나면 항상 close() 메서드를 호출하여 스트림을 닫는다.
         * 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
         */
        @Test
        void OutputStream은_사용하고_나서_close_처리를_해준다() throws IOException {
            // close()가 호출되었는지 확인하기 위한 가짜 OutputStream 객체를 만든다.
            final OutputStream outputStream = mock(OutputStream.class);

            /**
             * todo
             * try-with-resources를 사용한다.
             * java 9 이상에서는 변수를 try-with-resources로 처리할 수 있다.
             */
            /*
             * 파일이나 소켓에 연결된 OutputStream은 운영체제의 파일 디스크립터와 연결 자원을 점유한다.
             * 사용 후 닫지 않으면 자원이 계속 점유되는 누수가 발생하여 새 파일이나 소켓을 열지 못할 수 있다.
             * try-with-resources를 사용하면 정상 종료와 예외 발생 여부에 관계없이 close()가 자동 호출된다.
             * 스트림을 사용하는 코드는 닫히기 전인 try 블록 안에 작성한다.
             */
            // 스트림을 사용하는 코드는 try 블록 안에 작성하며, 블록을 벗어나면 close()가 자동 호출된다.
            // close()는 사용이 끝난 파일이나 네트워크 자원이 계속 점유되는 누수를 막는다.
            try (outputStream) {
            }

            // try-with-resources에 의해 close()가 한 번 이상 호출되었는지 검증한다.
            verify(outputStream, atLeastOnce()).close();
        }
    }

    /**
     * InputStream 학습하기
     * <p>
     * 자바의 기본 입력 클래스는 java.io.InputStream이다.
     * InputStream은 다른 매체로부터 바이트로 데이터를 읽을 때 사용한다.
     * InputStream의 read() 메서드는 기반 메서드이다.
     * <code>public abstract int read() throws IOException;</code>
     * <p>
     * InputStream의 서브 클래스(subclass)는 특정 매체에 데이터를 읽기 위해 read() 메서드를 사용한다.
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
            /*
             * ByteArrayInputStream은 생성자로 전달받은 byte[]를 읽을 매체로 사용한다.
             * 생성자는 읽을 대상을 지정할 뿐이며, 실제 데이터는 read 메서드를 호출할 때 읽는다.
             * read()는 바이트 하나를 0~255 범위의 int로 반환하고, 원본 바이트가 -1이면 255를 반환한다.
             * 반환값 -1은 데이터가 아니라 스트림의 끝을 나타내며, 끝에서 다시 read()를 호출해도 -1이 반환된다.
             * read(byte[])는 배열 크기만큼 여러 바이트를 읽을 수 있고, readAllBytes()는 끝까지 모든 바이트를 읽는다.
             * 읽은 byte[]는 toString()이 아니라 문자 인코딩을 지정한 new String()으로 문자열로 변환해야 한다.
             * FileInputStream과 소켓의 InputStream은 파일이나 네트워크 연결을 점유하므로 사용 후 close()해야 한다.
             * 닫지 않으면 해당 자원이 계속 점유되는 누수가 발생할 수 있다.
             * ByteArrayInputStream은 메모리의 byte[]만 읽기 때문에 반환할 외부 자원이 없다.
             */
            byte[] readBytes = inputStream.readAllBytes();
            final String actual = new String(readBytes, StandardCharsets.UTF_8);

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
            try(inputStream) {}

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
         * 버퍼 크기를 지정하지 않으면 버퍼의 기본 사이즈는 얼마일까?
         */
        @Test
        void 필터인_BufferedInputStream를_사용해보자() throws IOException {
            final String text = "필터에 연결해보자.";
            final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
            final InputStream bufferedInputStream = new BufferedInputStream(inputStream);

            /**
             * 필터는 기존 입출력 객체를 감싸 원래의 읽기·쓰기 기능에 버퍼링 등의 기능을 추가한다.
             * 여기서는 ByteArrayInputStream이 메모리의 byte[]를 읽는 원래 기능을 담당하고,
             * BufferedInputStream이 이를 감싸 내부 버퍼를 이용한 읽기 기능을 추가한다.
             * readAllBytes()는 InputStream의 메서드이므로 필터로 감싸지 않아도 사용할 수 있다.
             * 필터의 이점은 작은 단위의 read()가 반복될 때마다 원본에 접근하지 않고, 원본에서 여러 바이트를
             * 한꺼번에 버퍼로 가져온 뒤 버퍼에서 반환하여 파일이나 네트워크에 접근하는 횟수를 줄이는 것이다.
             * 다만 readAllBytes()처럼 처음부터 큰 단위로 읽는 경우에는 버퍼링으로 얻는 이점이 크지 않을 수 있다.
             * BufferedInputStream은 생성 시점이 아니라 처음 read()할 때 원본에서 데이터를 버퍼로 가져오며,
             * 버퍼의 데이터를 모두 사용하면 원본에서 다시 읽어 채운다.
             * 기본 버퍼 크기는 8192바이트이고, 생성자의 두 번째 인자로 다른 크기를 지정할 수 있다.
             * 버퍼 크기는 최대 저장 공간을 의미하며, 원본 데이터가 적으면 일부만 채워진다.
             * 정해진 최대 크기는 없지만 실제 크기는 JVM의 배열 제한과 사용 가능한 힙 메모리의 영향을 받는다.
             * ByteArrayInputStream은 이미 메모리에서 읽기 때문에 버퍼링의 성능 이점은 거의 없으며,
             * 이 테스트는 기존 스트림을 필터 스트림으로 감싸는 구조를 학습하기 위한 것이다.
             */
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
            final InputStream inputStream = new ByteArrayInputStream(emoji.getBytes());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final StringBuilder actual = new StringBuilder();

            /**
             * InputStream은 바이트를 읽고 Reader는 문자를 읽으며, InputStreamReader는 바이트를 문자로 변환한다.
             * 문자열을 getBytes()로 바이트로 바꾸는 과정은 인코딩이고, InputStreamReader가 바이트를 문자로
             * 바꾸는 과정은 디코딩이다. 같은 문자열을 정확히 복원하려면 두 과정에서 같은 문자 인코딩을
             * 사용해야 하므로 StandardCharsets.UTF_8처럼 인코딩을 명시하는 것이 좋다.
             * BufferedReader는 Reader를 감싸 버퍼링과 readLine() 기능을 추가한다. Reader도 read(char[])로
             * 여러 문자를 읽을 수 있지만 readLine()은 호출할 때마다 한 줄을 문자열로 반환한다.
             * readLine()은 원본의 줄바꿈 문자를 제거하므로 이 테스트에서는 원본에서 사용한 "\r\n"을 다시 붙인다.
             * "\n"은 Linux와 macOS에서 주로 사용하고 "\r\n"은 Windows와 HTTP에서 사용한다.
             * System.lineSeparator()는 현재 운영체제의 줄바꿈을 반환하므로 원본의 "\r\n"과 다를 수 있다.
             * 원본 줄바꿈을 그대로 보존해야 한다면 readLine() 대신 read(char[])를 사용해야 한다.
             * BufferedReader를 try-with-resources로 닫으면 내부의 InputStreamReader와 InputStream도 함께 닫힌다.
             */
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    actual.append(line).append("\r\n");
                }
            }

            assertThat(actual).hasToString(emoji);
        }
    }
}
