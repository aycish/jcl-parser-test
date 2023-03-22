package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
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
        List<Integer> actual = getTokenTypeList(fileName);
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
                Arguments.of("parser" + File.separator + "StmtWithoutParm.txt", Arrays.asList(Identifier, Name, Operation)),
                Arguments.of("parser" + File.separator + "OnePositionParm.txt", Arrays.asList(Identifier, Name, Operation, Param)),
                Arguments.of("parser" + File.separator + "OneKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, Param)),
                Arguments.of("parser" + File.separator + "OneSubKeywordParm.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, KeywordParam, Param)),
                Arguments.of("parser" + File.separator + "Comment_01.txt", Arrays.asList(Identifier, CommentString)),
                Arguments.of("parser" + File.separator + "Comment_02.txt", Arrays.asList(Identifier, Name, Operation, KeywordParam, ParamLeftParen, Param, Comma, Param, Comma, Param, ParamRightParen, CommentString)),
                Arguments.of("parser" + File.separator + "Instream.txt", Arrays.asList(Identifier, Name, Operation, Param, InstreamDatasetEntry, InstreamData, Identifier)),
                Arguments.of("parser" + File.separator + "Continuation_01.txt", Arrays.asList(Identifier, Name, Operation, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param)),
                Arguments.of("parser" + File.separator + "Continuation_02.txt", Arrays.asList(Identifier, Name, Operation, Param, Comma, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param, Comma, KeywordParam, Param)),
                Arguments.of("parser" + File.separator + "IF-THEN-ELSE_01.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen)),
                Arguments.of("parser" + File.separator + "IF-THEN-ELSE_02.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpLogicalOp, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen)),
                Arguments.of("parser" + File.separator + "IF-THEN-ELSE_03.txt", Arrays.asList(Identifier, Name, Operation, RelExpLeftParen, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpLogicalOp, RelExpKeyword, RelExpCompOp, RelExpKeyword, RelExpRightParen, RelExpThen))
        );
    }
}
