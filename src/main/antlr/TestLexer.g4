lexer grammar TestLexer;

options { language = Java; }
@header {
    import org.example.meta.KeywordData;
    import org.example.meta.MetaData;
    import java.util.*;
}

@members {
    private int nextMode = TestLexer.DEFAULT_MODE;
    private final enum metaData;
    private Map<String,Integer> identifier = new HashMap<String, Integer> {{
        put(metaData.CTRLID.getId(), NAME_FIELD);
        put(metaData.COMMENTID.getId(), COMMENT);
        put(metaData.NULLID.getId(), COMMENT);
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

EndOfFile :
    EOF -> skip;

/* MVS Identifier */
Identifier : [/\\.]+
    {
        if (identifier.containsKey(getText())) {
            this.mode(identifier.get(getText());
        } else {
            setType(InstreamData);
            this.mode(INSTREAM);
        }
    };

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
    }
;

NameBlank : WS+
    {
        this.mode(this.nextMode);
    } -> skip;

NameNewLine :
    NEWLINE -> skip, mode(DEFAULT_MODE);

mode OPERATION_FIELD;

Operation : [A-Z]+
    {
        if (KeywordData.checkNextModeIsParm(getText())) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
        } else {
            this.nextMode = TestLexer.REGEX_FIELD;
        }
    }
;

OperationBlank : WS+
    {
        this.mode(this.nextMode);
    } -> skip;

OperationNewLine : NEWLINE -> skip, mode(DEFAULT_MODE);

mode PARAM_KEYWORD_FIELD;
ParmKey: NAME_FIRST_CHAR NAME_CHAR* ('.' NAME_CHAR+)*;

ParmComma : ',' WS* NEWLINE?
    {
        if (getText().contains("\n")) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
            this.mode(TestLexer.CONTINUATION);
        }
    } -> type(Comma);

ParmKeywordQuotation :
    '\'' -> pushMode(QUOTATION);

ParmEqual :
    '=' { System.out.println("Check it changed mode"); }-> mode(PARAM_VALUE_FIELD);

ParmBlank :
    WS+ NEWLINE? { System.out.println("Check blank"); } -> skip, mode(DEFAULT_MODE);

mode PARAM_VALUE_FIELD;
ParmValue : [a-zA-Z0-9.&$#@/]+ {
        System.out.println("Does it work?");
        if (getText().equals("KEY")) this.setType(TestLexer.ParmKey);
    };

ParmSubParmEqual :
    '=' -> type(ParmEqual);

ParmValueComma : ',' WS+ NEWLINE?
    {
        if (getText().contains("\n")) {
            this.nextMode = TestLexer.PARAM_KEYWORD_FIELD;
            this.mode(TestLexer.CONTINUATION);
        }
    } -> type(Comma);

ParmValBlank :
    WS+ NEWLINE? { System.out.println("Check ParmValBlank"); } -> skip, mode(DEFAULT_MODE);

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
    '(' -> pushMode(BRACKET)
;

Quotation :
    '\'' -> popMode
;

mode BRACKET;
BracketString : (~[()',\r\n])+;
BracketComma : ',' -> type(Comma);
QuotationInBracket : '\'' -> pushMode(QUOTATION);
LeftBracket : '(' -> pushMode(BRACKET);
RightBracket : ')' -> popMode;

mode CONTINUATION;
ContinuationId : [/\\*]+
    {
    /*  Need to class to decide next mode and method to keep continuation mode */

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
    ~[\n]+;