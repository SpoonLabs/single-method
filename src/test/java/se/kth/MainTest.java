package se.kth;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class MainTest {

    private static final Set<String> testsToBeIgnored = Set.of("fair-acc_chart-fx_7a6a4e2417aa52fae1aec4ea0b4a0f87ca7d8124");

    @ParameterizedTest
    @ArgumentsSource(ResourceProvider.TrueCase.class)
    void shouldClassifyDiffsAs_ASingleMethodChange(ResourceProvider.TestResource sources)
            throws Exception {
        if (testsToBeIgnored.contains(sources.dir)) {
            assumeFalse(true, "Test is ignored");
        }
        boolean isOneMethodChange = Main.api(sources.left, sources.right);
        assertTrue(isOneMethodChange);
    }

    @ParameterizedTest
    @ArgumentsSource(ResourceProvider.FalseCase.class)
    void shouldClassifyDiffsAs_NotASingleMethodChange(ResourceProvider.TestResource sources)
            throws Exception {
        if (testsToBeIgnored.contains(sources.dir)) {
            assumeFalse(true, "Test is ignored");
        }
        boolean isOneMethodChange = Main.api(sources.left, sources.right);
        assertFalse(isOneMethodChange);
    }
}

class ResourceProvider {
    static class TestResource {
        String dir;
        File left;
        File right;

        private TestResource(String dir, File left, File right) {
            this.dir = dir;
            this.left = left;
            this.right = right;
        }

        private static TestResource fromTestDirectory(File testDir) {
            String dir = testDir.getName();
            File left = testDir.toPath().resolve("left.java").toFile();
            File right = testDir.toPath().resolve("right.java").toFile();
            return new TestResource(dir, left, right);
        }

        @Override
        public String toString() {
            return dir;
        }
    }

    static class TrueCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return getFilesInDirectory(new File("src/test/resources/true"));
        }
    }

    static class FalseCase implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return getFilesInDirectory(new File("src/test/resources/false"));
            }
    }

    private static Stream<? extends Arguments> getFilesInDirectory(File dir) {
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isDirectory)
                .map(TestResource::fromTestDirectory)
                .map(Arguments::of);
    }
}
