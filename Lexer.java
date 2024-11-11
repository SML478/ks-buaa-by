package frontend;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Lexer {
    private List<String> inputLines;  // 存储源代码的每一行
    private HashMap<String, String> keywords; // 关键字表
    private HashMap<String, String> operators; // 操作符表

    public Lexer(List<String> inputLines) {
        this.inputLines = inputLines;
        initializeKeywords();
        initializeOperators();
    }

    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();
        int lineNum = 0;
        boolean inMultiLineComment = false;
        for (String inputLine : inputLines) {
            lineNum++;

            int length = inputLine.length();
            int i = 0;

            while (i < length) {
                char currentChar = inputLine.charAt(i);
                if (inMultiLineComment) { //处理多行注释的情况
                    if (currentChar == '*' && i + 1 < length && inputLine.charAt(i + 1) == '/') {
                        inMultiLineComment = false;
                        i = i + 2;
                        continue;
                    } else {
                        i++;
                        continue;
                    }
                }

                if (currentChar == '/') { //处理单行注释和多行注释开始
                    if (i + 1 < length && inputLine.charAt(i + 1) == '/') {
                        break;
                    } else if (i + 1 < length && inputLine.charAt(i + 1) == '*') {
                        inMultiLineComment = true;
                        i = i + 2;
                        continue;
                    }
                }

                if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n') {
                    i++;
                    continue;
                }

                if (Character.isLetter(currentChar) || currentChar == '_') { //处理保留字和标识符
                    int start = i;
                    while (i < length && (Character.isLetterOrDigit(inputLine.charAt(i)) || inputLine.charAt(i) == '_')) {
                        i++;
                    }
                    String word = inputLine.substring(start, i);
                    String type = keywords.getOrDefault(word, "IDENFR");
                    Token token = new Token(type, word, lineNum);
                    tokens.add(token);
                    continue;
                }

                if (Character.isDigit(currentChar)) { //处理数字
                    int start = i;
                    while (i < length && Character.isDigit(inputLine.charAt(i))) {
                        i++;
                    }
                    String number = inputLine.substring(start, i);
                    Token token = new Token("INTCON", number, lineNum);
                    tokens.add(token);
                    continue;
                }

                if (currentChar == '"') { //处理字符串
                    int start = i;
                    i++;
                    while (i < length && inputLine.charAt(i) != '"') {
                        i++;
                    }
                    i++;
                    String string = inputLine.substring(start, i);
                    Token token = new Token("STRCON", string, lineNum);
                    tokens.add(token);
                    continue;
                }

                if (currentChar == '\'') { //处理字符常量
                    int start = i;
                    i++;
                    while (i < length && inputLine.charAt(i) != '\'') {
                        if (inputLine.charAt(i) == '\\') {
                            i = i + 2;
                        } else {
                            i++;
                        }
                    }
                    i++;
                    String ch = inputLine.substring(start, i);
                    Token token = new Token("CHRCON", ch, lineNum);
                    tokens.add(token);
                    continue;
                }

                if (operators.containsKey(String.valueOf(currentChar))) {
                    String op = String.valueOf(currentChar);
                    if (currentChar == '&' || currentChar == '|') {
                        if (i + 1 < length && inputLine.charAt(i + 1) == currentChar) {
                            op = op + currentChar;
                            i++;
                        } else {
                            Token token = new Token(operators.get(String.valueOf(currentChar)), op, lineNum);
                        }
                    } else if (currentChar == '=' || currentChar == '>' || currentChar == '<' || currentChar == '!') {
                        if (i + 1 < length && inputLine.charAt(i + 1) == '=') {
                            op = op + inputLine.charAt(i + 1);
                            i++;
                        }
                    }
                    String type = operators.get(op);
                    Token token = new Token(type, op, lineNum);
                    tokens.add(token);
                    i++;
                }
            }
        }
        return tokens;
    }

    private void initializeKeywords() {
        keywords = new HashMap<>();
        keywords.put("main", "MAINTK");
        keywords.put("const", "CONSTTK");
        keywords.put("int", "INTTK");
        keywords.put("char", "CHARTK");
        keywords.put("break", "BREAKTK");
        keywords.put("continue", "CONTINUETK");
        keywords.put("if", "IFTK");
        keywords.put("else", "ELSETK");
        keywords.put("for", "FORTK");
        keywords.put("getint", "GETINTTK");
        keywords.put("getchar", "GETCHARTK");
        keywords.put("printf", "PRINTFTK");
        keywords.put("return", "RETURNTK");
        keywords.put("void", "VOIDTK");
    }

    private void initializeOperators() {
        operators = new HashMap<>();
        operators.put("!", "NOT");
        operators.put("&&", "AND");
        operators.put("||", "OR");
        operators.put("+", "PLUS");
        operators.put("-", "MINU");
        operators.put("*", "MULT");
        operators.put("/", "DIV");
        operators.put("%", "MOD");
        operators.put("<", "LSS");
        operators.put("<=", "LEQ");
        operators.put(">", "GRE");
        operators.put(">=", "GEQ");
        operators.put("==", "EQL");
        operators.put("!=", "NEQ");
        operators.put("=", "ASSIGN");
        operators.put(";", "SEMICN");
        operators.put(",", "COMMA");
        operators.put("(", "LPARENT");
        operators.put(")", "RPARENT");
        operators.put("[", "LBRACK");
        operators.put("]", "RBRACK");
        operators.put("{", "LBRACE");
        operators.put("}", "RBRACE");
        operators.put("&", "errorA1"); // 单独的 & 是非法符号
        operators.put("|", "errorA2"); // 单独的 | 是非法符号
    }
}
