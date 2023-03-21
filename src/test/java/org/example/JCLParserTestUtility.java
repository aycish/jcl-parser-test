package org.example;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.example.parser.TestLexer;
import org.example.parser.TestParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JCLParserTestUtility {
    public static List<Integer> getTokenTypeList(final String fileName) throws FileNotFoundException {
        TestLexer testLexer = getLexerInstance(fileName);
        CommonTokenStream tokenStream = new CommonTokenStream(testLexer);
        tokenStream.fill();
        return tokenStream.getTokens().stream().map(Token::getType).collect(Collectors.toList());
    }

    public static TestParser createParserInstance(final String fileName) throws FileNotFoundException {
        TestLexer testLexer = getLexerInstance(fileName);
        CommonTokenStream tokenStream = new CommonTokenStream(testLexer);
        return new TestParser(tokenStream);
    }

    private static TestLexer getLexerInstance(String fileName) throws FileNotFoundException {
        Path filePath = Paths.get("src", "test", "resources", fileName);

        InputStream inputStream = new FileInputStream(filePath.toString());
        assertNotNull(inputStream);

        CharStream charStream = null;
        try {
            charStream = CharStreams.fromStream(inputStream);
        } catch (IOException e) {
            System.err.println("Can't open the file : " + fileName);
        }
        assertNotNull(charStream);
        return new TestLexer(charStream);
    }
}
