package org.example;

import org.example.parser.TestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.example.JCLParserTestUtility.createParserInstance;
import static org.example.parser.TestParser.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    @DisplayName("control statement without parameters")
    void ctrlStmtTest1() {
        //given
        TestParser parser = null;
        try {
            parser = createParserInstance("parser/CtrlStmtNoParam.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //when - then
        CtrlStmtContext ctrlStmtContext = parser.ctrlStmt();
        assertEquals(3, ctrlStmtContext.children.size());

        assertNotNull(ctrlStmtContext.Identifier());
        assertNotNull(ctrlStmtContext.Name());
        assertNotNull(ctrlStmtContext.Operation());
        assertNull(ctrlStmtContext.params());
    }

    @Test
    @DisplayName("control statement with parameters")
    void ctrlStmtTest2() {
        //given
        TestParser parser = null;
        try {
            parser = createParserInstance("parser/CtrlStmt.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

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

    }
}
