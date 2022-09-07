package support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class IoUtils {

    private IoUtils() {
    }

    /**
     * application/x-www-form-urlencoded
     * -로 encoding된 문자열을 해석한다
     * <p />
     * 예) %40 -> @
     */
    public static String readUrlEncoded(final BufferedReader reader, final int length) {
        try {
            return URLDecoder.decode(readCertainLength(reader, length), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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

    public static void writeAndFlush(final BufferedWriter bufferedWriter, final String data) {
        try {
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPath(final String fileName) {
        return Paths
                .get("tomcat", "src", "main", "resources", "static", fileName)
                .toAbsolutePath();
    }
}
