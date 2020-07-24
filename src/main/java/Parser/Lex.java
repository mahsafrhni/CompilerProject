package Parser;

import Lexical.MySymbol;

public interface Lex {
    MySymbol currentToken();
    String nextToken();
}