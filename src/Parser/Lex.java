package Parser;

import Scanner.Symbol;

public interface Lex {
    Symbol currentToken();
    String nextToken();
}