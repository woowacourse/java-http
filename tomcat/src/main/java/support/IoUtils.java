package support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class IoUtils {

    private IoUtils() {
    }

    /**
     * application/x-www-form-urlencoded -로 encoding된 문자열을 해석한다
     * <p/>
     * 예) %40 -> @
     */
    public static String readUrlEncoded(final BufferedReader reader, final int length) {
        return URLDecoder.decode(readCertainLength(reader, length), UTF_8);

    }

    public static String readCertainLength(final BufferedReader reader, final int length) {
        char[] buffer = new char[length];
        try {
            reader.read(buffer, 0, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer);
    }

    public static String readAllLines(final BufferedReader reader) {
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            while (reader.ready()) {
                stringBuffer.append(reader.readLine());
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readLine(final BufferedReader reader) {
        try {
            if (reader.ready()) {
                return reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public static String[] readLines(final BufferedReader reader) {
        final LinkedList<String> strings = new LinkedList<>();
        try {
            while (reader.ready()) {
                final String line = reader.readLine() + System.lineSeparator();
                strings.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return strings.toArray(new String[strings.size()]);
    }

    public static String readFile(final String fileName) {
        final Path path = getPath(fileName);
        final File file = path.toFile();
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines()
                    .collect(Collectors.joining("\r\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFileByClassLoader(final String fileName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (
                InputStream inputStream = loader.getResourceAsStream(fileName)
        ) {
            final byte[] contentByte;
            contentByte = inputStream.readAllBytes();
            return new String(contentByte);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeAndFlush(final BufferedWriter bufferedWriter, final String data) {
        try {
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

/*
    public static void writeAndFlush(final Socket connection, final String data) {
        try (final OutputStream outputStream = connection.getOutputStream();
             final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))){
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/

    private static Path getPath(final String fileName) {
        return Paths
                .get("tomcat", "src", "main", "resources", "static", fileName)
                .toAbsolutePath();
    }
}
