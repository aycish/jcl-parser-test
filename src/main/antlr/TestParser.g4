parser grammar TestParser;

options {
    tokenVocab=TestLexer;
}

statements :
    statement
|   statements statement
;

statement :
    ctrlStmt
|   commentStmt
;

ctrlStmt:
    Identifier Name Operation param?
;

param:
    ParmKey
|   ParmKey ParmEqual paramValue
;

paramValue:
    ParmValue
|   ParmKey ParmEqual ParmValue
|   bracketStrings
|   QuotationString
|   instreamDatas
;

bracketStrings:
    LeftBracket (BracketString | QuotationString)? (Comma (BracketString | QuotationString)?)+ RightBracket
;

commentStmt:
    Identifier CommentString
;

instreamDatas :
    InstreamDatasetEntry InstreamData*?
;