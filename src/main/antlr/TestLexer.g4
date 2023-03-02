lexer grammar TestLexer;

options { language = Java; }
@header {
    import org.example.meta.KeywordData;
    import org.example.meta.MetaData;
    import java.util.*;
}

@members {
    private int nextMode = TestLexer.DEFAULT_MODE;
    private Map<String,Integer> identifier = new HashMap<String, Integer>() {{
        put(MetaData.CTRLID.getId(), NAME_FIELD);
        put(MetaData.COMMENTID.getId(), COMMENT);
        put(MetaData.NULLID.getId(), COMMENT);
    }};
}

/* Common fragment */
fragment
NEWLINE :
    '\n'
;

fragment
WS :
    [ \r\t]
;

/* Common Token */
Comma :
    ','
;

/* Default mode lexer rules */
Identifier :
    [/\\.*]+
    {
        if (identifier.containsKey(getText())) {
            this.mode(identifier.get(getText()));
        } else {
            setType(InstreamData);
            this.mode(INSTREAM);
        }
    };

InstreamDatasetEntry :
    ~[/\\]
    {
        this.mode(INSTREAM);
    };

EndOfFile :
    EOF -> skip;

mode NAME_FIELD;

fragment
NAME_FIRST_CHAR :
    'a' .. 'z'
|   'A' .. 'Z'
|   '0' .. '9'
;

fragment
NAME_CHAR :
    NAME_FIRST_CHAR
|   [$#@]
;

Name :
    NAME_FIRST_CHAR NAME_CHAR* ('.' NAME_CHAR+)?
    {
        this.nextMode = TestLexer.OPERATION_FIELD;
    };

NameBlank :
    WS+
    {
        this.mode(this.nextMode);
    } -> skip;

NameNewLine :
    NEWLINE -> skip, mode(DEFAULT_MODE);

mode OPERATION_FIELD;

Operation :
    [A-Z]+
    {
        if (KeywordData.checkNextModeIsParm(getText())) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
        } else {
            this.nextMode = TestLexer.REGEX_FIELD;
        }
    };

OperationBlank :
    WS+
    {
        this.mode(this.nextMode);
    } -> skip;

OperationNewLine :
    NEWLINE -> skip, mode(DEFAULT_MODE);

mode PARAM_KEYWORD_FIELD;
ParmKey:
    NAME_FIRST_CHAR NAME_CHAR* ('.' NAME_CHAR+)*
|   '*'
;

ParmComma :
    ',' WS* NEWLINE?
    {
        if (getText().contains("\n")) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
            this.mode(TestLexer.CONTINUATION);
        }
    } -> type(Comma);

ParmKeywordQuotation :
    '\'' -> pushMode(QUOTATION);

ParmEqual :
    '=' -> mode(PARAM_VALUE_FIELD);

ParmKeyBlank :
    WS+ -> skip, mode(COMMENT);

ParmKeyNewLine :
    WS* NEWLINE -> skip, mode(DEFAULT_MODE);

mode PARAM_VALUE_FIELD;
ParmValue :
    [a-zA-Z0-9.&$#@/]+
    {
        if (getText().equals("KEY")) this.setType(TestLexer.ParmKey);
    };

ParmSubParmEqual :
    '=' -> type(ParmEqual);

ParmValueComma :
    ',' WS+ NEWLINE?
    {
        if (getText().contains("\n")) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
            this.mode(TestLexer.CONTINUATION);
        }
    } -> type(Comma);

ParmValBlank :
    WS+ -> skip, mode(COMMENT);

ParmValNewLine :
    WS* NEWLINE -> skip, mode(DEFAULT_MODE);

ParamValueQuotation :
    '\'' -> pushMode(QUOTATION);

ParamValueBracket :
    '(' -> pushMode(BRACKET);

/* TODO : quotation String, Parented String, SubParameter Parsing Rule 추가 필요 */
mode QUOTATION;
QuotationString :
    (~[()'\r\n])+
;

QuotationLeftBracket :
    '(' -> pushMode(BRACKET);

Quotation :
    '\'' -> popMode;

mode BRACKET;
BracketString :
    (~[()',\r\n])+
;

BracketComma :
    ',' -> type(Comma);

QuotationInBracket :
    '\'' -> pushMode(QUOTATION);

LeftBracket :
    '(' -> pushMode(BRACKET);

RightBracket :
    ')' -> popMode;

mode CONTINUATION;
ContinuationId :
    [/\\*]+
    {
        /*  Need to class to decide next mode and method to keep continuation mode */
        if (MetaData.CTRLID.getId().equals(getText()) {
            skip();
            popMode();
        } else if (MetaData.COMMENTID.getId().equals(getText()) {


        }
    };

mode REGEX_FIELD;
RegExpKeyword :
    [a-zA-Z0-9.]+
;

RegExpLogicalOp:
    'AND' | '&'
|   'OR'  | '|'
;

RegExpNotOp:
    'NOT' | '~'
;

RegExpCompOp :
  'GT' | '>'
| 'LT' | '<'
| 'NG' | '~>' | '^>'
| 'NL' | '~<' | '^<'
| 'EQ' | '='
| 'NE' | '~=' | '^='
| 'GE' | '>='
| 'LE' | '<='
;

RegExpThen :
    'THEN' WS+ NEWLINE -> mode(DEFAULT_MODE)
;

RegNewLine :
    WS+ NEWLINE -> skip, mode(CONTINUATION);

mode COMMENT;
CommentNewLine :
    WS* NEWLINE -> skip, mode(DEFAULT_MODE);

CommentString :
    ~[\n]+
;

mode INSTREAM;

InstreamData : .+? NEWLINE -> mode(DEFAULT_MODE);