package com.gutsol.da;

import com.google.common.annotations.VisibleForTesting;
import com.gutsol.da.parser.JavaScriptLexer;
import com.gutsol.da.parser.JavaScriptParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTransformer {

    public static void main(String... args) {
        if (args.length < 1) {
            System.out.println("Please, set filename");
            return;
        }
        String inputFileName = args[0];
        CharStream charStream;
        try (FileInputStream fileInputStream = new FileInputStream(new File(inputFileName))
        ) {
            charStream = CharStreams.fromStream(fileInputStream);
        } catch (IOException e) {
            System.out.println("Error while reading input file");
            return;
        }
        JavaScriptLexer lexer = new JavaScriptLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JavaScriptParser parser = new JavaScriptParser(tokenStream);
        parser.setBuildParseTree(true);

        ParseTreeWalker walker = new ParseTreeWalker();
        RepeatingAssignmentListener assignmentListener = new RepeatingAssignmentListener(tokenStream);
        JavaScriptParser.ProgramContext tree = parser.program();
        walker.walk(assignmentListener, tree);

        InlineDictionary dictionary = assignmentListener.getInlineDictionary();
        TextConverterListener converterListener = new TextConverterListener(dictionary);
        walker.walk(converterListener, tree);
        writeText(getOutputFileName(inputFileName), converterListener.getText());
    }

    @VisibleForTesting
    public static String getOutputFileName(String inputFileName) {
        return inputFileName.substring(0, inputFileName.length() - 3) + ".out.js";
    }

    private static void writeText(String fileName, String text) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            System.out.println("Error while writing output");
        }
    }
}
