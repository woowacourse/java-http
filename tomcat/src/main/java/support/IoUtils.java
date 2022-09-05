package support;

import jakarta.servlet.http.PushBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class IoUtils {

    private IoUtils() {
    }

    public static String readAllLines(BufferedReader bufferedReader) {
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            while (bufferedReader.ready()) {
                final String line = bufferedReader.readLine();
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuffer.toString();
    }

    public static String read(InputStream inputStream) {
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while (bufferedReader.ready()) {
                final String line = bufferedReader.readLine() + System.lineSeparator();
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuffer.toString();
    }

    /**
     * GET /index.html
     * k1: v1
     * k2: v2
     *
     * StringBuilder
     */
    public static String[] readLines(InputStream inputStream) {
        final LinkedList<String> strings = new LinkedList<>();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while (bufferedReader.ready()) {
                final String line = bufferedReader.readLine() + System.lineSeparator();
                strings.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return strings.toArray(new String[strings.size()]);
    }

    public static LinkedList<String> read(final String fileName) {
        final Path path = getPath(fileName);
        final File file = path.toFile();
        final BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getLines(bufferedReader);
    }

    public static String readLines(final String fileName) {
        return read(fileName)
                .stream()
                .collect(Collectors.joining("\r\n"));
    }

    public static void writeAndFlush(final BufferedWriter bufferedWriter, final String data) {
        try {
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static LinkedList<String> getLines(final BufferedReader bufferedReader) {
        return bufferedReader
                .lines()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static Path getPath(final String fileName) {
        return Paths
                .get("tomcat", "src", "main", "resources", "static", fileName)
                .toAbsolutePath();
    }
}
