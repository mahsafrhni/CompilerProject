import java.io.IOException;
 /*user codes */
%%
/*options and decleration */
%class Scanner
%line
%column
%unicode
%function next_token
%type MySymbol

%{
private boolean flag = false;
private HashSet<String> records = new HashSet<String>();
private MySymbol symbol(String token)
{
    System.err.println( "passed token: " + token +  " as" + " \"" + yytext() + "\"" );
    return new MySymbol(token, yytext());
}
private MySymbol symbol(String token, Object val) {
    System.err.println( "passed token: " + token +  " as" + " \"" + yytext() + "\""  );
    return new MySymbol(token, val);
}
 StringBuilder string = new StringBuilder();
%}

id = {letter}({letter}|{Digit}|"_")*
letter = [A-Za-z]
ESign = (\-)
Sign = (\+|\-)?
NoSignDecimal = [0-9]+
DecimalInt = {ESign}{NoSignDecimal}
Long = [0-9]+[L]
HexaDecimal = {Sign}[0][x][0-9a-fA-F]+
Digit = [0-9]
Num = {DoubleNumber}|{DecimalInt}|{NoSignDecimal}
FloatNumber = {Num}[fF]
DoubleNumber = {Sign}(\.{Digit}+) | {Sign}({Digit}+\.) |{Sign}({Digit}+\.{Digit}+)
ScientificNumber1 = {DoubleNumber}[e]{Sign}{NoSignDecimal}
ScientificNumber2 = {NoSignDecimal}[e]{Sign}{NoSignDecimal}
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
InputCharacter = [^\r\n]
StringCharacter = [^\t\r\n\"\'\\]
SpecialCharacter = \\ ([trn\"\'\\])
TwoLineComment = "/*"~"*/"
OneLineComment = "//" {InputCharacter}* {LineTerminator}
Comment = {TwoLineComment}|{OneLineComment}
AcooladBaste=[}]
AcooladBaz=[{]

/* define states */
%state CHARACTER
%state STRING
%state SINGLE_COMMENT
%state MULT_COMMENT

%%

/*lexical rules*/

<YYINITIAL> {
    /* SYMBOLS */
    "=="    { return symbol("==");}
    "!="    { return symbol("!=");}
    "<="    { return symbol("<=");}
    "<"     { return symbol("<");}
    ">"     { return symbol(">");}
    ">="    { return symbol(">=");}
    "="     { return symbol("=");}
    "&"     { return symbol("&");}
    "|"     { return symbol("|");}
    "^"     { return symbol("^");}
    "and"   { return symbol("and");}
    "or"    { return symbol("or");}
    "not"   { return symbol("not");}
    "xor"   { return symbol("xor");}
    "*"     { return symbol("*");}
    "+"     { return symbol("+");}
    "+="    { return symbol("+=");}
    "%="    { return symbol("%=");}
    "-="    { return symbol("-=");}
    "*="    { return symbol("*=");}
    "/="    { return symbol("/=");}
    "/"     { return symbol("/");}
    "%"     { return symbol("%");}
    "begin"    { return symbol("begin");}
    "end"     { return symbol("end");}
    "("     { return symbol("(");}
    ")"     { return symbol(")");}
    "."     { return symbol(".");}
    ","     { return symbol("va");}
    ":"     { return symbol(":");}
    ";"     { return symbol(";");}
    "["     { return symbol("[");}
    "]"     { return symbol("]");}
    "]["     { return symbol("va");}
    "++"    { return symbol("++");}
    "--"    { return symbol("--");}
    "-"     { return symbol("-");}

    /* CHARACTER */
    "'"     { yybegin(CHARACTER);string.setLength(0); string.append("'");}
    /* STRINGS */
    \"      { yybegin(STRING); string.setLength(0); string.append("\"");}
     /* SINGLE LINE COMMENTS */
    "##"    { yybegin(SINGLE_COMMENT); string.setLength(0); string.append("##");}
    /* MULTIPLE LINE COMMENTS */
    "/#"    {yybegin(MULT_COMMENT); string.setLength(0); string.append("/#");}
    /* KEYWORDS */
    "start"       { return symbol("start");}
    "function"    { return symbol("function");}
    "void"        { return symbol("void");}
    "record"      { flag = true; return symbol("record");}
    "const"       { return symbol("const");}
    "auto"        { return symbol("auto");}
    "return"      { return symbol("return");}
    "break"       { return symbol("break");}
    "continue"    { return symbol("continue");}
    "len"         { return symbol("len");}
    "if"          { return symbol("if");}
    "else"        { return symbol("else");}
    "switch"      { return symbol("switch");}
    "of"          { return symbol("of");}
    "case"        { return symbol("case");}
    "default"     { return symbol("default");}
    "for"         { return symbol("for");}
    "repeat"      { return symbol("repeat");}
    "until"       { return symbol("until");}
    "foreach"     { return symbol("foreach");}
    "in"          { return symbol("in");}
    "Sizeof"      { return symbol("Sizeof");}
    "int"         { return symbol("base_type");}
    "long"        { return symbol("base_type");}
    "double"      { return symbol("base_type");}
    "float"       { return symbol("base_type");}
    "char"        { return symbol("base_type");}
    "string"      { return symbol("base_type");}
    "bool"        { return symbol("base_type");}
    "new"         { return symbol("new");}
    "println"     { return symbol("println");}
    "input"       { return symbol("input");}
    "true"        { return symbol("true", Boolean.valueOf(yytext()));}
    "false"       { return symbol("false", Boolean.valueOf(yytext()));}
    /* VARIABLES */
    {id}                {   String temp = yytext();
                            if(records.contains(temp)){
                                flag = false;
                                return symbol("rec_id",temp);
                            } if(flag){
                                flag = false;
                                records.add(temp);
                                return symbol("rec_id",temp);
                            }
                            return symbol("id",temp);}
    /* NUMBERS */
   {NoSignDecimal} {return symbol("int_const", Integer.valueOf(yytext()));}
      {DecimalInt} {return symbol("int_const", Integer.valueOf(yytext()));}
   {Long}  {return symbol("long_const", Integer.parseInt(yytext().split("L",20)[0]));}
      {HexaDecimal}  {return symbol("hex_const",Integer.parseInt(yytext().split("x", 20)[1],16));}
      {DoubleNumber} {return symbol("real_const", Double.valueOf(yytext()));}
   {FloatNumber} {return symbol("float_const", Double.valueOf(yytext().split("F",20)[0]));}
       {ScientificNumber1} {return symbol("real_const", (Integer.parseInt(yytext().split("e",20)[0])*(Math.pow(10,Integer.parseInt(yytext().split("e",10)[1])))));}
           {ScientificNumber2} {return symbol("real_const", (Integer.parseInt(yytext().split("e",20)[0])*(Math.pow(10,Integer.parseInt(yytext().split("e",10)[1])))));}
    {WhiteSpace}        {/* skip */}
    {AcooladBaste} {return symbol("}");}
    {AcooladBaz}   {return symbol("{");}
}

<CHARACTER>{
    "'"                 {yybegin(YYINITIAL);}
    {StringCharacter}   {return symbol("char", yytext().charAt(0));}
    {SpecialCharacter}  {return symbol("char" ,yytext().charAt(0));}
}

<STRING>{
    \"                  {yybegin(YYINITIAL); string.append("\""); StringBuilder temp = string; string = new StringBuilder(); return symbol("string", temp.toString());}
    {StringCharacter}+  {string.append(yytext());}
    {SpecialCharacter}+ {string.append(yytext());}
}
{Comment}  {yybegin(YYINITIAL);}

[^]        { throw new RuntimeException("Illegal character \""+yytext()+
                                        "\" at line "+yyline+", column "+yycolumn); }
<<EOF>>    {return symbol("$");}

