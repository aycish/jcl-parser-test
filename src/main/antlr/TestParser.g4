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
    Identifier Name Operation params?
;

params:
    param (Comma param)*
;

param:
    quotationString
|   Param
|   KeywordParam Param
|   KeywordParam param
|   KeywordParam ParamLeftParen params ParamRightParen
|   instreamDatas
;

quotationString:
    Quotation QuotationString Quotation
;

commentStmt:
    Identifier CommentString
;

instreamDatas :
    InstreamDatasetEntry InstreamData*?
;
