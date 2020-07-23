package Parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    public static String Separator = ",";
    private Lex lex;
    private CodeGen codeGen;
    private boolean debug;
    private String[] symbols;
    private TableCell[][] pt; //parse table
    private int start; //start node
    private Deque<Integer> ps = new ArrayDeque<>(); //parse stack
    private List<String> rec_state; //recovery state

    //  public Parser(Lex lex, CodeGen codeGen, String nptTablePath, boolean debug) {
    // this(lex, codeGen, nptTablePath);
    // this.debug = debug;
    // }
    public Parser(Lex lex, CodeGen codeGen, String nptTablePath) {
        this.lex = lex;
        this.codeGen = codeGen;
        this.rec_state = new ArrayList<>();
        if (!Files.exists(Paths.get(nptTablePath))) {
            throw new RuntimeException("Parser table not found: " + nptTablePath);
        }
        try {
            Scanner input = new Scanner(new FileInputStream(nptTablePath));
            String[] Arr = input.nextLine().trim().split(" ");
            int row = Integer.parseInt(Arr[0]);  //row size
            int column = Integer.parseInt(Arr[1]);  //column size
            start = Integer.parseInt(input.nextLine());
            symbols = input.nextLine().trim().split(Separator);
            pt = new TableCell[row][column];
            for (int i = 0; i < row; i++) {
                Arr = input.nextLine().trim().split(Separator);
                if (Arr.length != column) {
                    throw new RuntimeException("Invalid .npt file. File contains rows with length" +
                            " bigger than column size.");
                }
                for (int j = 0; j < column; j++) {
                    String[] cells = Arr[j].split(" ");  //cell parts
                    if (cells.length != 3) {
                        throw new RuntimeException("Invalid .npt file. File contains cells with 3 values.");
                    }
                    Action act = Action.values()[Integer.parseInt(cells[0])];
                    int target = Integer.parseInt(cells[1]);
                    List<String> allFunctions;
                    if (cells[2].equals("NoSem")) {
                        allFunctions = new ArrayList<>();
                    } else {
                        allFunctions = Arrays.stream(cells[2].substring(1).split("[;]"))
                                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
                    }
                    pt[i][j] = new TableCell(act, target, allFunctions);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid .npt file.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to load .npt file.", e);
        }
    }

    public void parse() {
        int T_ID = TokenId(); //next token ID
        int Node = start;  //current node
        boolean accepted = false;
        while (!accepted) {
            String tokenText = symbols[T_ID];
            TableCell cell = pt[Node][T_ID];
            if (debug) {
                System.out.println("Current token: text='" + symbols[T_ID] + "' id=" + T_ID);
                System.out.println("Current node: " + Node);
                System.out.println("Current cell of parser table: " +
                        "target-node=" + cell.getGoal() +
                        " action=" + cell.getAct() +
                        " function=" + cell.getFunction());
                System.out.println(String.join("", Collections.nCopies(50, "-")));
            }
            switch (cell.getAct()) {
                case ERROR:
                    updateState(Node, tokenText);
                    Error("Unable to parse input");
                case SHIFT:
                    useFuncs(cell.getFunction());
                    T_ID = TokenId();
                    Node = cell.getGoal();
                    rec_state.clear();
                    break;
                case GOTO:
                    updateState(Node, tokenText);
                    useFuncs(cell.getFunction());
                    Node = cell.getGoal();
                    break;
                case PUSH_GOTO:
                    updateState(Node, tokenText);
                    ps.push(Node);  //push to parse stack
                    Node = cell.getGoal();
                    break;
                case REDUCE:
                    if (ps.size() == 0) {
                        Error("Unable to Reduce: token=" + tokenText + " node=" + Node);
                    }
                    updateState(Node, tokenText);
                    int gToken = cell.getGoal();   //graph token
                    int lastNode = ps.pop();  //pop from parse stack ->pre node
                    useFuncs(pt[lastNode][gToken].getFunction());
                    Node = pt[lastNode][gToken].getGoal();
                    break;
                case ACCEPT:
                    accepted = true;
                    break;
            }
        }
    }

    private int TokenId() {  //next token id
        String token = lex.nextToken();
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals(token)) {
                return i;
            }
        }
        throw new RuntimeException("Undefined token: " + token);
    }

    private void Error(String message) { //generate error
        System.out.flush();
        System.out.println("Error happened while parsing ...");
        for (String state : rec_state) {
            System.out.println(state);
        }
        throw new RuntimeException(message);
    }

    private void updateState(int Node, String token) { //update recovery state
        List<String> availableTokens = new ArrayList<>();
        TableCell[] Tokens = pt[Node];  //tokens of a cell
        for (int i = 0; i < Tokens.length; i++) {
            if (Tokens[i].getAct() != Action.ERROR) {
                availableTokens.add(symbols[i]);
            }
        }
        rec_state.add("At node " + Node + ": current token is " + token + " but except: " + availableTokens);
    }

    private void useFuncs(List<String> functions) {  //do semantics
        if (functions.isEmpty()) {
            return;
        }
        if (debug) {
            System.out.println("Execute semantic codes: " + functions);
        }
        for (String function : functions) {
            codeGen.doSemantic(function);
        }
    }
}

class TableCell {
    private Action act;
    private int goal;
    private List<String> function;

    public TableCell(Action act, int goal, List<String> function) {
        this.act = act;
        this.goal = goal;
        this.function = function;
    }

    public Action getAct() {
        return act;
    }

    public int getGoal() {
        return goal;
    }

    public List<String> getFunction() {
        return function;
    }
}

enum Action {
    ERROR, SHIFT, GOTO, PUSH_GOTO, REDUCE, ACCEPT
}