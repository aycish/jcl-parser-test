parser grammar TestParser;

options {
    tokenVocab=TestLexer;
}

statements :
    statement+
;

statement :
    ctrlStmt
|   relStmt
|   commentStmt
|   nullStmt
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
    Comma* param (Comma param?)*
|   params instreamDatas
|   Comma
;

param:
    quotationString
|   Param
|   KeywordParam Param
|   KeywordParam param
|   ParamLeftParen params ParamRightParen
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

nullStmt:
    Identifier
;
