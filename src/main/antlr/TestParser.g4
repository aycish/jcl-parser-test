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
|   relStmt
|   commentStmt
;

ctrlStmt:
    Identifier Name Operation params?
;

relStmt:
    Identifier Name Operation relExps RelExpThen
;

relExps:
    RelExpNotOp? RelExpLeftParen? relExp (RelExpLogicalOp relExp)* RelExpRightParen?
;

relExp:
    RelExpKeyword RelExpCompOp RelExpKeyword
|   RelExpNotOp RelExpLeftParen RelExpKeyword RelExpCompOp RelExpKeyword RelExpRightParen
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
