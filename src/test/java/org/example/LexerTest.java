package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.example.JCLParserTestUtility.getTokenTypeList;
import static org.example.parser.TestLexer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {

    @ParameterizedTest(name = "{0} Test")
    @MethodSource("testData")
    void ControlStatementLexerTest(final String fileName, List<Integer> expected) {
        List<Integer> actual = null;
        try {
            actual = getTokenTypeList(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        validate(expected, actual);
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
                Arguments.of("lexer" + File.separator + "lexer/StmtWithoutParm.txt", Arrays.asList(Identifier, Name, Operation)),
                Arguments.of("lexer" + File.separator + "lexer/OnePositionParm.txt", Arrays.asList(Identifier, Name, Operation, Param)),
                Arguments.of("lexer" + File.separator + "lexer/OneKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, Param)),
                Arguments.of("lexer" + File.separator + "lexer/OneSubKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, KeywordParam, Param)),
                Arguments.of("lexer" + File.separator + "lexer/Comment_01.txt", Arrays.asList(Identifier, CommentString)),
                Arguments.of("lexer" + File.separator + "lexer/Comment_02.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, ParamLeftParen, Param, Comma, Param, Comma, Param, ParamRightParen, CommentString)),
                Arguments.of("lexer" + File.separator + "lexer/Instream.txt", Arrays.asList(Identifier, Name, Operation, Param, InstreamDatasetEntry, InstreamData, Identifier)),
                Arguments.of("lexer" + File.separator + "lexer/Continuation_01.txt", Arrays.asList(Identifier, Name, Operation, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param)),
                Arguments.of("lexer" + File.separator + "lexer/Continuation_02.txt", Arrays.asList(Identifier, Name, Operation, Param, Comma, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param)),
                Arguments.of("lexer" + File.separator + "lexer/IF-THEN-ELSE_01.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen)),
                Arguments.of("lexer" + File.separator + "lexer/IF-THEN-ELSE_02.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpLogicalOp, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen)),
                Arguments.of("lexer" + File.separator + "lexer/IF-THEN-ELSE_03.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpLogicalOp, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen))
        );
    }
}
