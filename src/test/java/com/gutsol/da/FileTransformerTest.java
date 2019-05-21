package com.gutsol.da;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class FileTransformerTest {
    @Test
    public void testCanBeInlinedSimple() throws IOException {
        testWithFile("inlined_simple.js", "var arr = [1, 2, 3];\n" +
                "var [a, b] = arr;\n");
    }

    @Test
    public void testIndexesNotFromZeroCannotInline() throws IOException {
        testWithFile("indexes_not_from_zero.js", "var arr = [1, 2, 3];\n" +
                "var a = arr[1];\n" +
                "var b = arr[2];");
    }

    @Test
    public void testDropInIndexesCannotInline() throws IOException {
        testWithFile("drop_in_indexes.js", "var arr = [1, 2, 3];\n" +
                "var a = arr[0];\n" +
                "var b = arr[2];");
    }

    @Test
    public void testStatementInBetweenCannotInline() throws IOException {
        testWithFile("statement_in_between.js",
                "var arr = [1, 2, 3];\n" +
                        "var a = arr[0];\n" +
                        "console.log('1');\n" +
                        "var b = arr[1];");
    }

    @Test
    public void testMultipleInlines() throws IOException {
        testWithFile("multiple_inlines.js", "var arr = [1, 2, 3];\n" +
                "var brr = [3, 4, 5];\n" +
                "var [a, b] = arr;\n" +
                "var [c, d, e] = brr;\n");
    }

    private void testWithFile(String fileName, String text) throws IOException {
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(fileName);
        FileTransformer.main(fileUrl.getPath());

        File outputFile = new File(FileTransformer.getOutputFileName(fileUrl.getPath()));
        String actual = new String(Files.readAllBytes(Paths.get(outputFile.toURI())));
        assertEquals(text, actual);
    }
}
