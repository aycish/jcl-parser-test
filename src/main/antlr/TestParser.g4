parser grammar TestParser;

options {
    tokenVocab=TestLexer;
}

ctrlStmt:
    CtrlStmtId Name Operation param?
 ;

param:
    ParmKey
|   ParmKey ParmEqual paramValue
;

paramValue:
    ParmValue
 |  ParmKeywordOperand ParmEqual ParmValue
 |  bracketStrings
 |  QuotationString
 ;

 bracketStrings:
     LeftBracket (BracketString | QuotationString)? (Comma (BracketString | QuotationString)?)+ RightBracket
 ;

commentStmt:
    CommentStmtId CommentString
;