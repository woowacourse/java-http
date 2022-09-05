package support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class IoUtils {

    private IoUtils() {
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

    public static String readLines(final String fileName) {
        return String.join("\r\n", readFromFile(fileName));
    }

    public static LinkedList<String> readFromFile(final String fileName) {
        final Path path = getPath(fileName);
        final File file = path.toFile();
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
            return getLines(bufferedReader);
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
