package org.example;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.parser.TestLexer;
import org.example.parser.TestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.example.JCLParserTestUtility.createParserInstance;
import static org.example.parser.TestParser.*;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    @DisplayName("control statement without parameters")
    void ctrlStmtTest1() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "CtrlStmtNoParam.txt");

        //when - then
        CtrlStmtContext ctrlStmtContext = parser.ctrlStmt();
        assertEquals(3, ctrlStmtContext.children.size());

        assertTrue(checkToken(TestLexer.Identifier, ctrlStmtContext.children.get(0)));
        assertTrue(checkToken(TestLexer.Name, ctrlStmtContext.children.get(1)));
        assertTrue(checkToken(TestLexer.Operation, ctrlStmtContext.children.get(2)));
        assertNull(ctrlStmtContext.params());
    }

    @Test
    @DisplayName("control statement with parameters")
    void ctrlStmtTest2() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "CtrlStmt.txt");

        //when - then
        CtrlStmtContext ctrlStmtContext = parser.ctrlStmt();
        assertNotNull(ctrlStmtContext);
        assertEquals(4, ctrlStmtContext.children.size());

        /* 제어문 Parse Tree 구성 노드 확인 */
        assertTrue(checkToken(TestLexer.Identifier, ctrlStmtContext.getChild(0)));
        assertTrue(checkToken(TestLexer.Name, ctrlStmtContext.getChild(1)));
        assertTrue(checkToken(TestLexer.Operation, ctrlStmtContext.getChild(2)));
        assertTrue(checkRule(TestParser.ParamsContext.class, ctrlStmtContext.getChild(3)));

        /* 파라미터 Tree 구성 노드 확인 */
        ParamsContext params = (ParamsContext) ctrlStmtContext.getChild(3);
        assertNotNull(params);
        assertEquals(5, params.children.size());
        assertTrue(checkRule(TestParser.ParamContext.class, params.getChild(0)));
        assertTrue(checkToken(TestLexer.Comma, params.getChild(1)));
        assertTrue(checkRule(TestParser.ParamContext.class, params.getChild(2)));
        assertTrue(checkToken(TestLexer.Comma, params.getChild(3)));
        assertTrue(checkRule(TestParser.ParamContext.class, params.getChild(4)));

        ParamContext firstParam = params.getChild(ParamContext.class, 0);
        assertNotNull(firstParam);
        assertTrue(checkToken(TestLexer.Param, firstParam.children.get(0)));

        /* quotation string as positional parameter */
        ParamContext secondParam = params.getChild(ParamContext.class, 1);
        assertNotNull(secondParam);
        assertTrue(checkRule(TestParser.QuotationStringContext.class, secondParam.getChild(0)));
        assertNotNull(secondParam.quotationString());

        /* validate quotation String */
        assertEquals(3, secondParam.quotationString().children.size());
        assertTrue(checkToken(TestLexer.Quotation, secondParam.quotationString().getChild(0)));
        assertTrue(checkToken(TestLexer.QuotationString, secondParam.quotationString().getChild(1)));
        assertTrue(checkToken(TestLexer.Quotation, secondParam.quotationString().getChild(2)));

        /* bracket string list as keyword parameter */
        ParamContext thirdParam = params.getChild(ParamContext.class, 2);
        assertNotNull(thirdParam);

        assertTrue(checkToken(TestLexer.KeywordParam, thirdParam.getChild(0)));
        assertTrue(checkToken(TestLexer.ParamLeftParen, thirdParam.getChild(1)));
        assertTrue(checkRule(TestParser.ParamsContext.class, thirdParam.getChild(2)));
        assertTrue(checkToken(TestLexer.ParamRightParen, thirdParam.getChild(3)));

        ParamsContext subParamOfThird = (ParamsContext) thirdParam.getChild(2);
        assertNotNull(subParamOfThird);
        assertEquals(5, subParamOfThird.children.size());

        /* 괄호안의 서브 파라미터 검증 */
        assertTrue(checkRule(TestParser.ParamContext.class, subParamOfThird.getChild(0)));
        assertTrue(checkToken(TestLexer.Comma, subParamOfThird.getChild(1)));
        assertTrue(checkRule(TestParser.ParamContext.class, subParamOfThird.getChild(2)));
        assertTrue(checkToken(TestLexer.Comma, subParamOfThird.getChild(3)));
        assertTrue(checkRule(TestParser.ParamContext.class, subParamOfThird.getChild(4)));
    }

    @Test
    @DisplayName("IF-THEN-ELSE-ENDIF statement")
    void relStmtTest() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "IF-THEN-ELSE_01.txt");

        /* validate Parser rule : relStmt */
        RelStmtContext relStmtContext = parser.relStmt();
        assertNotNull(relStmtContext);

        assertEquals(5, relStmtContext.children.size());
        assertTrue(checkToken(TestLexer.Identifier, relStmtContext.getChild(0)));
        assertTrue(checkToken(TestLexer.Name, relStmtContext.getChild(1)));
        assertTrue(checkToken(TestLexer.Operation, relStmtContext.getChild(2)));
        assertTrue(checkRule(TestParser.RelExpsContext.class, relStmtContext.getChild(3)));
        assertTrue(checkToken(TestLexer.RelExpThen, relStmtContext.getChild(4)));

        /* validate Parser rule : relExps */
        RelExpsContext relExpsContext = (RelExpsContext) relStmtContext.getChild(3);
        assertNotNull(relExpsContext);

        assertEquals(3, relExpsContext.children.size());
        assertTrue(checkToken(TestLexer.RelExpLeftParen, relExpsContext.getChild(0)));
        assertTrue(checkRule(TestParser.RelExpContext.class, relExpsContext.getChild(1)));
        assertTrue(checkToken(TestLexer.RelExpRightParen, relExpsContext.getChild(2)));

        /* validate Parser rule : relExp */
        RelExpContext relExpContext = (RelExpContext) relExpsContext.getChild(1);
        assertNotNull(relExpContext);

        assertEquals(3, relExpContext.children.size());
        assertTrue(checkToken(TestLexer.RelExpKeyword, relExpContext.getChild(0)));
        assertTrue(checkToken(TestLexer.RelExpCompOp, relExpContext.getChild(1)));
        assertTrue(checkToken(TestLexer.RelExpKeyword, relExpContext.getChild(2)));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"IF-THEN-ELSE_02.txt", "IF-THEN-ELSE_03.txt"})
    void relStmtTest2(final String fileName) {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + fileName);

        /* validate Parser rule : relStmt */
        RelStmtContext relStmtContext = parser.relStmt();
        assertNotNull(relStmtContext);

        assertEquals(5, relStmtContext.children.size());
        assertTrue(checkToken(TestLexer.Identifier, relStmtContext.getChild(0)));
        assertTrue(checkToken(TestLexer.Name, relStmtContext.getChild(1)));
        assertTrue(checkToken(TestLexer.Operation, relStmtContext.getChild(2)));
        assertTrue(checkRule(TestParser.RelExpsContext.class, relStmtContext.getChild(3)));
        assertTrue(checkToken(TestLexer.RelExpThen, relStmtContext.getChild(4)));

        /* validate Parser rule : relExps */
        RelExpsContext relExpsContext = relStmtContext.relExps();
        assertNotNull(relExpsContext);

        assertEquals(5, relExpsContext.children.size());
        assertTrue(checkToken(TestLexer.RelExpLeftParen, relExpsContext.getChild(0)));
        assertTrue(checkRule(TestParser.RelExpContext.class, relExpsContext.getChild(1)));
        assertTrue(checkToken(TestLexer.RelExpLogicalOp, relExpsContext.getChild(2)));
        assertTrue(checkRule(TestParser.RelExpContext.class, relExpsContext.getChild(3)));
        assertTrue(checkToken(TestLexer.RelExpRightParen, relExpsContext.getChild(4)));

        /* validate Parser rule : first relExp */
        RelExpContext relExpContext = (RelExpContext) relExpsContext.getChild(1);
        assertNotNull(relExpContext);

        assertEquals(3, relExpContext.children.size());
        assertTrue(checkToken(TestLexer.RelExpKeyword, relExpContext.getChild(0)));
        assertTrue(checkToken(TestLexer.RelExpCompOp, relExpContext.getChild(1)));
        assertTrue(checkToken(TestLexer.RelExpKeyword, relExpContext.getChild(2)));

        /* validate Parser rule : second relExp */
        RelExpContext SecondRelExpContext = (RelExpContext) relExpsContext.getChild(3);
        assertEquals(3, SecondRelExpContext.children.size());
        assertTrue(checkToken(TestLexer.RelExpKeyword, SecondRelExpContext.getChild(0)));
        assertTrue(checkToken(TestLexer.RelExpCompOp, SecondRelExpContext.getChild(1)));
        assertTrue(checkToken(TestLexer.RelExpKeyword, SecondRelExpContext.getChild(2)));
    }

    @Test
    @DisplayName("Comment statement")
    void commentTest() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "Comment_01.txt");

        CommentStmtContext commentStmtContext = parser.commentStmt();
        assertNotNull(commentStmtContext);

        assertEquals(2, commentStmtContext.children.size());
        assertTrue(checkToken(TestLexer.Identifier, commentStmtContext.getChild(0)));
        assertTrue(checkToken(TestLexer.CommentString, commentStmtContext.getChild(1)));
    }

    @Test
    @DisplayName("Instream data")
    void InstreamDatas() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "Instream.txt");

        StatementsContext statements = parser.statements();
        assertNotNull(statements);

        assertEquals(2, statements.children.size());
        assertTrue(checkRule(TestParser.StatementContext.class, statements.getChild(0)));
        assertTrue(checkRule(TestParser.StatementContext.class, statements.getChild(1)));

        /* validate first statement */
        StatementContext firstStatement = (StatementContext) statements.getChild(0);
        assertNotNull(firstStatement);

        assertEquals(1, firstStatement.children.size());
        assertTrue(checkRule(CtrlStmtContext.class, firstStatement.getChild(0)));

        CtrlStmtContext ddStatement = (CtrlStmtContext) firstStatement.getChild(0);
        assertNotNull(ddStatement);

        assertEquals(4, ddStatement.children.size());
        assertTrue(checkToken(TestLexer.Identifier, ddStatement.getChild(0)));
        assertTrue(checkToken(TestLexer.Name, ddStatement.getChild(1)));
        assertTrue(checkToken(TestLexer.Operation, ddStatement.getChild(2)));
        assertTrue(checkRule(TestParser.ParamsContext.class, ddStatement.getChild(3)));

        /* validate params for DD statement */
        ParamsContext params = (ParamsContext) ddStatement.getChild(3);
        assertNotNull(params);

        assertEquals(2, params.children.size());
        assertTrue(checkRule(TestParser.ParamsContext.class, params.getChild(0)));
        assertTrue(checkRule(TestParser.InstreamDatasContext.class, params.getChild(1)));

        /* validate first parameter for DD statement */
        ParamsContext asteriskParams = (ParamsContext) params.getChild(0);
        assertNotNull(asteriskParams);

        assertEquals(1, asteriskParams.children.size());
        assertTrue(checkRule(TestParser.ParamContext.class, asteriskParams.getChild(0)));

        ParamContext asterisk = (ParamContext) asteriskParams.getChild(0);
        assertNotNull(asterisk);

        assertEquals(1, asterisk.children.size());
        assertTrue(checkToken(TestLexer.Param, asterisk.getChild(0)));

        /* validate instream data */
        InstreamDatasContext instreamData = (InstreamDatasContext) params.getChild(1);
        assertNotNull(instreamData);

        assertEquals(2, instreamData.children.size());
        assertTrue(checkToken(TestLexer.InstreamDatasetEntry, instreamData.getChild(0)));
        assertTrue(checkToken(TestLexer.InstreamData, instreamData.getChild(1)));

        /* validate second statement */
        StatementContext secondStatement = (StatementContext) statements.getChild(1);
        assertNotNull(secondStatement);

        assertEquals(1, secondStatement.children.size());
        assertTrue(checkRule(NullStmtContext.class, secondStatement.getChild(0)));

        /* validate Null statement */
        NullStmtContext nullStatement = (NullStmtContext) secondStatement.getChild(0);
        assertNotNull(nullStatement);

        assertEquals(1, nullStatement.children.size());
        assertTrue(checkToken(TestLexer.Identifier, nullStatement.getChild(0)));
    }

    private boolean checkToken(int expectedTokenType, ParseTree actualToken) {
        TerminalNode targetNode = (TerminalNode) actualToken;
        if (targetNode == null) return false;
        System.out.println("check the node : " + actualToken.toString());

        if (targetNode.getSymbol() == null) return false;
        return expectedTokenType == targetNode.getSymbol().getType();
    }

    private <C extends ParserRuleContext, T extends ParseTree> boolean checkRule(Class<C> expectedRuleType, T actualRuleType) {
        if (actualRuleType == null) return false;
        System.out.println("check the rule node : " + actualRuleType.getText());
        return expectedRuleType.equals(actualRuleType.getClass());
    }
}
