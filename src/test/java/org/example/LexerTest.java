package org.example;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.example.parser.TestLexer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.parser.TestLexer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LexerTest {

    @ParameterizedTest(name = "{0} Test")
    @MethodSource("testData")
    void ControlStatementLexerTest(final String fileName, List<Integer> expected) {
        List<Integer> actual = getTokenTypeList(fileName);
        validate(expected, actual);
    }

    List<Integer> getTokenTypeList(final String fileName) {
        Path filePath = Paths.get(fileName);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath.toString());
        assertNotNull(inputStream);

        CharStream charStream = null;
        try {
            charStream = CharStreams.fromStream(inputStream);
        } catch (IOException e) {
            System.err.println("Can't open the file : " + fileName);
        }
        assertNotNull(charStream);

        TestLexer testLexer = new TestLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(testLexer);
        tokenStream.fill();

        return tokenStream.getTokens().stream().map(Token::getType).collect(Collectors.toList());
    }

    private static void validate(List<Integer> expected, List<Integer> actual) {
        actual.removeIf(elem -> elem.equals(EOF));
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                //Arguments.of("StmtWithoutParm.txt", Arrays.asList(Identifier, Name, Operation)),
                //Arguments.of("OnePositionParm.txt", Arrays.asList(Identifier, Name, Operation, ParmKey)),
                //Arguments.of("OneKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, ParmKey, ParmEqual, ParmValue)),
                //Arguments.of("OneSubKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, ParmKey, ParmEqual, ParmKey, ParmEqual, ParmValue)),
                //Arguments.of("Comment.txt", Arrays.asList(Identifier, CommentString)),
                Arguments.of("Instream.txt", Arrays.asList(Identifier, Name, Operation, ParmKey, InstreamDatasetEntry, InstreamData, Identifier))
        );
    }
}
