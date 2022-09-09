package se.kth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

class MainTest {
    @ParameterizedTest
    @ArgumentsSource(ResourceProvider.Diff.class)
    void shouldCorrectlyDecideIfItIsOneMethodChange(ResourceProvider.TestResource sources)
            throws Exception {
        boolean isOneMethodChange = Main.api(sources.left, sources.right);
        assertEquals(sources.expected, isOneMethodChange);
    }
}

class ResourceProvider {
    static class TestResource {
        String dir;
        File left;
        File right;

        boolean expected;

        private TestResource(String dir, File left, File right, boolean expected) {
            this.dir = dir;
            this.left = left;
            this.right = right;
            this.expected = expected;
        }

        private static TestResource fromTestDirectory(File testDir, boolean expected) {
            String dir = testDir.getName();
            File left = testDir.toPath().resolve("left.java").toFile();
            File right = testDir.toPath().resolve("right.java").toFile();
            return new TestResource(dir, left, right, expected);
        }

        @Override
        public String toString() {
            return dir;
        }
    }

    static class Diff implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.concat(
                    getFilesInDirectory(new File("src/test/resources/true"), true),
                    getFilesInDirectory(new File("src/test/resources/false"), false));
        }

        private static Stream<? extends Arguments> getFilesInDirectory(File dir, boolean expected) {
            return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                    .filter(File::isDirectory)
                    .map(diffDir -> TestResource.fromTestDirectory(diffDir, expected))
                    .map(Arguments::of);
        }
    }
}
