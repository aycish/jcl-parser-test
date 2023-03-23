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
|   '\r\n'
;

fragment
WS :
    [ \t]
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
    NAME_FIRST_CHAR NAME_CHAR* ('.' NAME_CHAR+)?;

NameBlank :
    WS+ -> skip, mode(OPERATION_FIELD);

NameNewLine :
    NEWLINE -> skip, mode(DEFAULT_MODE);

mode OPERATION_FIELD;
Operation :
    [A-Z]+
    {
        if (KeywordData.checkNextModeIsParm(getText())) {
            this.nextMode = TestLexer.PARAM;
        } else {
            this.nextMode = TestLexer.RELEXP_FIELD;
        }
    };

OperationBlank :
    WS+
    {
        this.mode(this.nextMode);
    } -> skip;

OperationNewLine :
    NEWLINE -> skip, mode(DEFAULT_MODE);

mode PARAM;
KeywordParam :
    NAME_FIRST_CHAR NAME_CHAR* ('.' NAME_FIRST_CHAR NAME_CHAR*)? '='
    {
        this.nextMode = TestLexer.DEFAULT_MODE;
    };

Param:
    ~[ '=(),\r\n]+
;

ParamComma :
    ',' NEWLINE?
    {
        this.nextMode = TestLexer.PARAM;
        if (getText().contains("\n")) {
            pushMode(TestLexer.CONTINUATION);
        }
    } -> type(Comma);

ParamBlank :
    WS+
    {
        /* 이전에 Comma가 나왔는지를 체크하여 Comment 이후의 모드를 결정한다.
        *  Comma가 나왔었다면, nextMode는 TestLexer.PARAM의 값을 가지고 있다.
        */
        if (nextMode == TestLexer.PARAM) {
            this.nextMode = TestLexer.CONTINUATION;
        } else {
            this.nextMode = TestLexer.DEFAULT_MODE;
        }

        pushMode(TestLexer.COMMENT);
    }-> skip;

ParamNewline :
    NEWLINE -> skip, mode(DEFAULT_MODE);

ParamQuotation :
    '\'' -> type(Quotation), pushMode(QUOTATION);

ParamLeftParen :
    '('
 ;

ParamRightParen :
    ')'
;

mode QUOTATION;
QuotationString :
    (~['\r\n])+
;

QuotationNewLine :
    NEWLINE
    {
        this.nextMode = TestLexer.QUOTATION;
    } -> skip, pushMode(CONTINUATION);

Quotation :
    '\'' -> popMode;

mode CONTINUATION;
ContinuationId :
    [/\\*]+ WS*
    {
        if (getText().trim().equals(MetaData.CTRLID.getId())) {
            popMode();
        }
    } -> skip;

mode RELEXP_FIELD;
RelExpThen :
    'THEN' WS* {
        this.nextMode = TestLexer.DEFAULT_MODE;
        if (getText().contains(" ")) {
            mode(COMMENT);
        }
    }
;

RelExpKeyword :
    [a-zA-Z0-9.]+
    {
        this.nextMode = TestLexer.RELEXP_FIELD;
    }
;

RelExpLeftParen :
    '('
 ;

RelExpRightParen :
    ')'
;

RelExpLogicalOp:
    'AND' | '&'
|   'OR'  | '|'
;

RelExpNotOp:
    'NOT' | '~'
;

RelExpCompOp :
  'GT' | '>'
| 'LT' | '<'
| 'NG' | '~>' | '^>'
| 'NL' | '~<' | '^<'
| 'EQ' | '='
| 'NE' | '~=' | '^='
| 'GE' | '>='
| 'LE' | '<='
;

RelExpBlank :
    WS+ -> skip;

RelNewLine :
    NEWLINE
    {
        /* 이전에 THEN이 나온 경우 */
        if (this.nextMode == TestLexer.DEFAULT_MODE) {
            mode(DEFAULT_MODE);
        /* 이전에 THEN이 나오지 않은 경우 */
        } else {
            this.nextMode = TestLexer.RELEXP_FIELD;
            pushMode(CONTINUATION);
        }
    } -> skip;

mode COMMENT;
CommentNewLine :
    WS* NEWLINE {
        mode(this.nextMode);
    } -> skip
;

CommentString :
    ~[\n]+
;

mode INSTREAM;

InstreamData : ~[\r\n]+;
InstreamDataDelimiter : NEWLINE -> skip, mode(DEFAULT_MODE);