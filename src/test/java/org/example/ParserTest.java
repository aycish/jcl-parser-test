package org.example;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.parser.TestLexer;
import org.example.parser.TestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

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

        assertTrue(isSameToken(TestLexer.Identifier, ctrlStmtContext.children.get(0)));
        assertTrue(isSameToken(TestLexer.Name, ctrlStmtContext.children.get(1)));
        assertTrue(isSameToken(TestLexer.Operation, ctrlStmtContext.children.get(2)));
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

        assertNotNull(ctrlStmtContext.Identifier());
        assertNotNull(ctrlStmtContext.Name());
        assertNotNull(ctrlStmtContext.Operation());
        assertNotNull(ctrlStmtContext.params());

        ParamsContext params = ctrlStmtContext.params();
        assertNotNull(params);

        List<ParamContext> paramList = params.param();
        assertFalse(paramList.isEmpty());
        assertEquals(3, paramList.size());

        ParamContext firstParam = paramList.get(0);
        assertNotNull(firstParam);
        assertNotNull(firstParam.Param());

        /* quotation string as positional parameter */
        ParamContext secondParam = paramList.get(1);
        assertNotNull(secondParam);
        assertNotNull(secondParam.quotationString());
        assertEquals(3, secondParam.quotationString().children.size());
        assertNotNull(secondParam.quotationString().Quotation());
        assertNotNull(secondParam.quotationString().QuotationString());

        /* bracket string list as keyword parameter */
        ParamContext thirdParam = paramList.get(2);
        assertNotNull(thirdParam);
        assertNotNull(thirdParam.KeywordParam());
        assertNotNull(thirdParam.ParamLeftParen());
        assertNotNull(thirdParam.params());
        assertNotNull(thirdParam.ParamRightParen());

        assertNotNull(thirdParam.params().param());

        ParamsContext bracketList = thirdParam.params();
        assertEquals(3, bracketList.param().size());
    }

    @Test
    @DisplayName("IF-THEN-ELSE-ENDIF statement")
    void relStmtTest() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "IF-THEN-ELSE_01.txt");

        /* validate Parser rule : relStmt */
        RelStmtContext relStmtContext = parser.relStmt();
        assertNotNull(relStmtContext);
        assertNotNull(relStmtContext.Identifier());
        assertNotNull(relStmtContext.Name());
        assertNotNull(relStmtContext.Operation());
        assertNotNull(relStmtContext.RelExpThen());

        /* validate Parser rule : relExps */
        RelExpsContext relExpsContext = relStmtContext.relExps();
        assertNotNull(relExpsContext);
        assertEquals(3, relExpsContext.children.size());

        /* validate Parser rule : relExp */
        RelExpContext relExpContext = relExpsContext.relExp().get(0);
        assertNotNull(relExpContext);
        assertEquals(3, relExpContext.children.size());
        assertEquals(2, relExpContext.RelExpKeyword().size());
        assertNotNull(relExpContext.RelExpCompOp());
    }

    @Test
    @DisplayName("IF-THEN-ELSE-ENDIF statement with logical op")
    void relStmtTest2() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "IF-THEN-ELSE_02.txt");

        /* validate Parser rule : relStmt */
        RelStmtContext relStmtContext = parser.relStmt();
        assertNotNull(relStmtContext);
        assertNotNull(relStmtContext.Identifier());
        assertNotNull(relStmtContext.Name());
        assertNotNull(relStmtContext.Operation());
        assertNotNull(relStmtContext.RelExpThen());

        /* validate Parser rule : relExps */
        RelExpsContext relExpsContext = relStmtContext.relExps();
        assertNotNull(relExpsContext);
        assertEquals(5, relExpsContext.children.size());
        assertEquals(2, relExpsContext.relExp().size());
        assertNotNull(relExpsContext.RelExpLeftParen());
        assertNotNull(relExpsContext.RelExpRightParen());
        assertNotNull(relExpsContext.RelExpLogicalOp());
        assertEquals(1, relExpsContext.RelExpLogicalOp().size());

        /* validate Parser rule : relExp */
        RelExpContext relExpContext = relExpsContext.relExp().get(0);
        assertNotNull(relExpContext);
        assertEquals(3, relExpContext.children.size());
        assertEquals(2, relExpContext.RelExpKeyword().size());
        assertNotNull(relExpContext.RelExpCompOp());

        /* validate Parser rule : relExp */
        RelExpContext SecondRelExpContext = relExpsContext.relExp().get(0);
        assertNotNull(SecondRelExpContext);
        assertEquals(3, SecondRelExpContext.children.size());
        assertEquals(2, SecondRelExpContext.RelExpKeyword().size());
        assertNotNull(SecondRelExpContext.RelExpCompOp());
    }

    @Test
    @DisplayName("IF-THEN-ELSE-ENDIF statement with continuation")
    void relStmtTest3() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "IF-THEN-ELSE_03.txt");

        //when - then
        /* validate Parser rule : relStmt */
        RelStmtContext relStmtContext = parser.relStmt();
        assertNotNull(relStmtContext);
        assertNotNull(relStmtContext.Identifier());
        assertNotNull(relStmtContext.Name());
        assertNotNull(relStmtContext.Operation());
        assertNotNull(relStmtContext.RelExpThen());

        /* validate Parser rule : relExps */
        RelExpsContext relExpsContext = relStmtContext.relExps();
        assertNotNull(relExpsContext);
        assertEquals(5, relExpsContext.children.size());
        assertEquals(2, relExpsContext.relExp().size());

        assertNotNull(relExpsContext.RelExpRightParen());
        assertNotNull(relExpsContext.RelExpLogicalOp());
        assertEquals(1, relExpsContext.RelExpLogicalOp().size());

        /* validate Parser rule : relExp */
        RelExpContext relExpContext = relExpsContext.relExp().get(0);
        assertNotNull(relExpContext);
        assertEquals(3, relExpContext.children.size());
        assertEquals(2, relExpContext.RelExpKeyword().size());
        assertNotNull(relExpContext.RelExpCompOp());

        /* validate Parser rule : relExp */
        RelExpContext SecondRelExpContext = relExpsContext.relExp().get(0);
        assertNotNull(SecondRelExpContext);
        assertEquals(3, SecondRelExpContext.children.size());
        assertEquals(2, SecondRelExpContext.RelExpKeyword().size());
        assertNotNull(SecondRelExpContext.RelExpCompOp());
    }

    @Test
    @DisplayName("Comment statement")
    void commentTest() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "Comment_01.txt");

        CommentStmtContext commentStmtContext = parser.commentStmt();
        assertNotNull(commentStmtContext);
        assertNotNull(commentStmtContext.Identifier());
        assertNotNull(commentStmtContext.CommentString());
        assertEquals(2, commentStmtContext.children.size());
    }

    @Test
    @DisplayName("Instream data")
    void InstreamDatas() {
        //given
        TestParser parser = createParserInstance("parser" + File.separator + "Instream.txt");

        StatementContext statement = parser.statement();
        assertEquals(2, statement.children.size());
        assertEquals(TestParser.CtrlStmtContext.class, statement.children.get(0).getClass());

    }

    private boolean isSameToken(int tokenType, ParseTree tNode) {
        System.out.println("check the node : " + tNode.toString());
        TerminalNode targetNode = (TerminalNode) tNode;
        assertNotNull(targetNode);
        assertNotNull(targetNode.getSymbol());
        assertEquals(tokenType, targetNode.getSymbol().getType());
        return true;
    }
}
