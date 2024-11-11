package frontend;
import java.util.*;

public class SyntaxAnalyzer {
    private final List<Token> tokens;      // 存储词法分析结果
    private int currentTokenIndex;   // 当前分析的词法单元索引
    private final List<Token> errors;      // 存储错误信息
    private final TreeNode root;            // 语法树根节点
    private ArrayList<Symbol> print = new ArrayList<>();

    public Stack<SymbolTable> stack = new Stack<>();
    int maxId = 1;
    int id = 1;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.errors = new ArrayList<>();
        this.root = new TreeNode("<CompUnit>", false, new Token("", "", 0), new ArrayList<>());  // 初始化语法树根节点
    }

    public TreeNode analyze() {
        CompUnit();  // 开始分析编译单元
        return root;  // 返回构建的语法树
    }

    public ArrayList<Symbol> getPrint() {
        return print;
    }

    private void CompUnit() {
        stack.add(new SymbolTable(1, 0));
        while (currentTokenIndex < tokens.size()) {
            if (tokens.get(currentTokenIndex).getType().equals("CONSTTK")) {
                root.addChild(Decl());
            } else if (tokens.get(currentTokenIndex).getType().equals("VOIDTK")) {
                root.addChild(FuncDef());
            } else {
                if (tokens.get(currentTokenIndex).getType().equals("INTTK")) {
                    if (tokens.get(currentTokenIndex + 1).getType().equals("MAINTK")) {
                        root.addChild(MainFuncDef());
                    } else if (tokens.get(currentTokenIndex + 1).getType().equals("IDENFR")){
                        if (tokens.get(currentTokenIndex + 2).getType().equals("LPARENT")) {
                            root.addChild(FuncDef());
                        } else {
                            root.addChild(Decl());
                        }
                        //不包含的错误/////////////////////////////////////
                    }
                    //不包含的错误/////////////////////////////////////
                } else if (tokens.get(currentTokenIndex).getType().equals("CHARTK")) {
                    if (tokens.get(currentTokenIndex + 1).getType().equals("IDENFR")){
                        if (tokens.get(currentTokenIndex + 2).getType().equals("LPARENT")) {
                            root.addChild(FuncDef());
                        } else{
                            root.addChild(Decl());
                        }
                        //不包含的错误/////////////////////////////////////
                    }
                    //不包含的错误/////////////////////////////////////
                }
                //不包含的错误/////////////////////////////////////
            }
        }
        stack.pop();
    }

    private TreeNode Decl() {
        TreeNode declNode = new TreeNode("<Decl>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("CONSTTK")) {
            declNode.addChild(ConstDecl());
        } else {
            declNode.addChild(VarDecl());
        }
        return declNode;
    }

    private TreeNode ConstDecl() {
        TreeNode constDeclNode = new TreeNode("<ConstDecl>", false, new Token("", "", 0), new ArrayList<>());
        TreeNode constNode = new TreeNode("CONSTTK", true, tokens.get(currentTokenIndex), new ArrayList<>());
        constDeclNode.addChild(constNode);
        currentTokenIndex++;

        String BType = tokens.get(currentTokenIndex).getValue();

        constDeclNode.addChild(BType());
        constDeclNode.addChild(ConstDef(BType));
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
            TreeNode commaNode = new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>());
            constDeclNode.addChild(commaNode);
            currentTokenIndex++;
            constDeclNode.addChild(ConstDef(BType));
        }
        if (tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
            TreeNode semicNode = new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>());
            constDeclNode.addChild(semicNode);
            currentTokenIndex++;
        } else {
            errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
        }
        return constDeclNode;
    }

    private TreeNode BType() {
        TreeNode bTypeNode = new TreeNode("<BType>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("INTTK") || tokens.get(currentTokenIndex).getType().equals("CHARTK")) {
            bTypeNode.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        }
        return bTypeNode;
    }

    private TreeNode ConstDef(String BType) {
        TreeNode constDefNode = new TreeNode("<ConstDef>", false, new Token("", "", 0), new ArrayList<>());

        Token identToken = tokens.get(currentTokenIndex);
        String name = identToken.getValue();
        int lineNum = identToken.getLineNum();
        Symbol symbol = getSymbol(BType, name, lineNum);
        boolean add = stack.peek().addSymbol(symbol, stack);
        print.add(symbol);
        if (!add) {
            errors.add(new Token("b", " ", lineNum));
        }


        constDefNode.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        if (tokens.get(currentTokenIndex).getType().equals("LBRACK")) {
            TreeNode lbrackNode = new TreeNode("LBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>());
            constDefNode.addChild(lbrackNode);
            currentTokenIndex++;
            constDefNode.addChild(ConstExp());
            if (tokens.get(currentTokenIndex).getType().equals("RBRACK")) {
                TreeNode rbrackNode = new TreeNode("RBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>());
                constDefNode.addChild(rbrackNode);
                currentTokenIndex++;
            } else {
                errors.add(new Token("k", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        }
        constDefNode.addChild(new TreeNode("ASSIGN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        constDefNode.addChild(ConstInitVal());
        return constDefNode;
    }

    private Symbol getSymbol(String BType, String name, int lineNum) {
        Symbol symbol;
        if (tokens.get(currentTokenIndex + 1).getType().equals("LBRACK")) {
            if (BType.equals("int")) {
                symbol = new Symbol(id, name, "ConstIntArray", lineNum, 1);
            } else {
                symbol = new Symbol(id, name, "ConstCharArray", lineNum, 1);
            }
        } else {
            if (BType.equals("int")) {
                symbol = new Symbol(id, name, "ConstInt", lineNum, 0);
            } else {
                symbol = new Symbol(id, name, "ConstChar", lineNum, 0);
            }
        }
        return symbol;
    }

    private TreeNode ConstInitVal() {
        TreeNode constInitValNode = new TreeNode("<ConstInitVal>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("STRCON")) {
            constInitValNode.addChild(new TreeNode("STRCON", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        } else if (tokens.get(currentTokenIndex).getType().equals("LBRACE")) {
            constInitValNode.addChild(new TreeNode("LBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("RBRACE")) {
                constInitValNode.addChild(ConstExp());
                while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
                    constInitValNode.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                    currentTokenIndex++;
                    constInitValNode.addChild(ConstExp());
                }
            }
            constInitValNode.addChild(new TreeNode("RBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        } else {
            constInitValNode.addChild(ConstExp());
        }
        return constInitValNode;
    }

    private TreeNode VarDecl() {
        TreeNode varDeclNode = new TreeNode("<VarDecl>", false, new Token("", "", 0), new ArrayList<>());
        String BType = tokens.get(currentTokenIndex).getValue();
        varDeclNode.addChild(BType());
        varDeclNode.addChild(VarDef(BType));
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
            varDeclNode.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            varDeclNode.addChild(VarDef(BType));
        }
        if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
            errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
        } else {
            varDeclNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        }
        return varDeclNode;
    }

    private TreeNode VarDef(String BType) {
        TreeNode varDefNode = new TreeNode("<VarDef>" , false, new Token("", "", 0), new ArrayList<>());

        addVar(BType);

        varDefNode.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        if (tokens.get(currentTokenIndex).getType().equals("LBRACK")) {
            varDefNode.addChild(new TreeNode("LBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;  // consume '['
            varDefNode.addChild(ConstExp());
            if (tokens.get(currentTokenIndex).getType().equals("RBRACK")) {
                varDefNode.addChild(new TreeNode("RBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            } else {
                errors.add(new Token("k", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        }
        if (tokens.get(currentTokenIndex).getType().equals("ASSIGN")) {
            varDefNode.addChild(new TreeNode("ASSIGN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            varDefNode.addChild(InitVal());
        }
        return varDefNode;
    }

    private Symbol getSymbol1(String BType, String name, int lineNum) {
        Symbol symbol;
        if(tokens.get(currentTokenIndex + 1).getType().equals("LBRACK")) {
            if (BType.equals("int")) {
                symbol = new Symbol(id, name, "IntArray", lineNum, 1);
            } else {
                symbol = new Symbol(id, name, "CharArray", lineNum, 1);
            }
        } else {
            if (BType.equals("int")) {
                symbol = new Symbol(id, name, "Int", lineNum, 0);
            } else {
                symbol = new Symbol(id, name, "Char", lineNum, 0);
            }
        }
        return symbol;
    }

    private TreeNode InitVal() {
        TreeNode initValNode = new TreeNode("<InitVal>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("STRCON")) {
            initValNode.addChild(new TreeNode("STRCON", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        } else if (tokens.get(currentTokenIndex).getType().equals("LBRACE")) {
            initValNode.addChild(new TreeNode("LBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("RBRACE")) {
                initValNode.addChild(Exp());
                while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
                    initValNode.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                    currentTokenIndex++;
                    initValNode.addChild(Exp());
                }
            }
            initValNode.addChild(new TreeNode("RBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        } else {
            initValNode.addChild(Exp());
        }
        return initValNode;
    }

    private TreeNode FuncDef() {
        TreeNode funcDefNode = new TreeNode("<FuncDef>", false, new Token("", "", 0), new ArrayList<>());
        String funcType = tokens.get(currentTokenIndex).getType();
        funcDefNode.addChild(FuncType());


        Token identToken = tokens.get(currentTokenIndex);
        String name = identToken.getValue();
        int lineNum = identToken.getLineNum();
        Symbol symbol;
        if (funcType.equals("INTTK")) {
            symbol = new Symbol(id, name, "IntFunc", lineNum, 0);
        } else if (funcType.equals("CHARTK")) {
            symbol = new Symbol(id, name, "CharFunc", lineNum, 0);
        } else {
            symbol = new Symbol(id, name, "VoidFunc", lineNum, 0);
        }
        boolean add = stack.peek().addSymbol(symbol, stack);
        print.add(symbol);
        if (!add) {
            errors.add(new Token("b", " ", lineNum));
        }

        int id1 = id;
        funcDefNode.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;

        stack.add(new SymbolTable(maxId + 1, id));
        id = maxId + 1;
        maxId++;

        funcDefNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
            if (tokens.get(currentTokenIndex).getType().equals("LBRACE")) {
                errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                funcDefNode.addChild(FuncFParams(symbol));
                if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                    errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                } else {
                    funcDefNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                    currentTokenIndex++;
                }
            }
        } else {
            funcDefNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        }
        TreeNode blockNode = Block(true, symbol.type, false);
        funcDefNode.addChild(blockNode);

        if (!Objects.equals(symbol.type, "VoidFunc")) {
            if (blockNode.children.size() == 2) {
                errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                TreeNode blockItemNode = blockNode.children.get(blockNode.children.size() - 2);
                if (blockItemNode.children.get(0).type.equals("<Decl>")) {
                    errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                } else {
                    TreeNode stmtNode = blockItemNode.children.get(0);
                    if (!stmtNode.children.get(0).token.getType().equals("RETURNTK")) {
                        errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                    }
                }
            }
        }

        return funcDefNode;
    }

    private TreeNode MainFuncDef() {
        TreeNode mainFuncNode = new TreeNode("<MainFuncDef>", false, new Token("", "", 0), new ArrayList<>());
        mainFuncNode.addChild(new TreeNode("INTTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        mainFuncNode.addChild(new TreeNode("MAINTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        dealRp(mainFuncNode);
        TreeNode blockNode = Block(false, " ", false);
        mainFuncNode.addChild(blockNode);

        if (blockNode.children.size() == 2) {
            errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
        } else {
            TreeNode blockItemNode = blockNode.children.get(blockNode.children.size() - 2);
            if (blockItemNode.children.get(0).type.equals("<Decl>")) {
                errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                TreeNode stmtNode = blockItemNode.children.get(0);
                if (!stmtNode.children.get(0).token.getType().equals("RETURNTK")) {
                    errors.add(new Token("g", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                }
            }
        }

        return mainFuncNode;
    }

    private TreeNode FuncType() {
        TreeNode funcTypeNode = new TreeNode("<FuncType>", false, new Token("", "", 0), new ArrayList<>());
        funcTypeNode.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        return funcTypeNode;
    }

    private TreeNode FuncFParams(Symbol symbol) {
        TreeNode funcFParams = new TreeNode("<FuncFParams>", false, new Token("", "", 0), new ArrayList<>());
        funcFParams.addChild(FuncFParam(symbol));
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
            funcFParams.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            funcFParams.addChild(FuncFParam(symbol));
        }
        return funcFParams;
    }

    private TreeNode FuncFParam(Symbol symbol) {
        TreeNode funcFParam = new TreeNode("<FuncFParam>", false, new Token("", "", 0), new ArrayList<>());
        String BType = tokens.get(currentTokenIndex).getValue();
        funcFParam.addChild(BType());

        Symbol symbol1 = addVar(BType);
        symbol.addParam(symbol1.type);


        funcFParam.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        if (tokens.get(currentTokenIndex).getType().equals("LBRACK")) {
            funcFParam.addChild(new TreeNode("LBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (tokens.get(currentTokenIndex).getType().equals("RBRACK")) {
                funcFParam.addChild(new TreeNode("RBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            } else {
                errors.add(new Token("k", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        }
        return funcFParam;
    }

    private Symbol addVar(String BType) {
        Token identToken = tokens.get(currentTokenIndex);
        String name = identToken.getValue();
        int lineNum = identToken.getLineNum();
        Symbol symbol1 = getSymbol1(BType, name, lineNum);
        print.add(symbol1);
        boolean add = stack.peek().addSymbol(symbol1, stack);
        if (!add) {
            errors.add(new Token("b", " ", lineNum));
        }
        return symbol1;
    }

    private TreeNode Block(boolean isFun, String type, boolean isFor) {
        TreeNode blockNode = new TreeNode("<Block>", false, new Token("", "", 0), new ArrayList<>());
        blockNode.children.add(new TreeNode("LBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;

        if (!isFun) {
            stack.add(new SymbolTable(maxId + 1, id));
            id = maxId + 1;
            maxId++;
        }

        while (!tokens.get(currentTokenIndex).getType().equals("RBRACE")) {
            blockNode.addChild(BlockItem(isFun, type, isFor));
        }
        blockNode.addChild(new TreeNode("RBRACE", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;

        id = stack.peek().fatherId;
        stack.pop();

        return blockNode;
    }

    private TreeNode BlockItem(boolean isFun, String type, boolean isFor) {
        TreeNode blockItemNode = new TreeNode("<BlockItem>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("CONSTTK") ||
                tokens.get(currentTokenIndex).getType().equals("INTTK") ||
                tokens.get(currentTokenIndex).getType().equals("CHARTK")) {
            blockItemNode.addChild(Decl());
        } else {
            blockItemNode.addChild(Stmt(isFun, type, isFor));
        }
        return blockItemNode;
    }

    private TreeNode Stmt(boolean isFun, String type, boolean isFor) {
        TreeNode stmtNode = new TreeNode("<Stmt>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("LBRACE")) {
            stmtNode.addChild(Block(false, type, isFor));
        } else if (tokens.get(currentTokenIndex).getType().equals("IFTK")) {
            stmtNode.addChild(new TreeNode("IFTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            stmtNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            stmtNode.addChild(Cond());
            if (tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                stmtNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            } else {
                errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
            stmtNode.addChild(Stmt(isFun, type, isFor));
            if (tokens.get(currentTokenIndex).getType().equals("ELSETK")) {
                stmtNode.addChild(new TreeNode("ELSETK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
                stmtNode.addChild(Stmt(isFun, type, isFor));
            }
        } else if (tokens.get(currentTokenIndex).getType().equals("FORTK")) {
            stmtNode.addChild(new TreeNode("FORTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            stmtNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                stmtNode.addChild(ForStmt());
            }
            stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                stmtNode.addChild(Cond());
            }
            stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                stmtNode.addChild(ForStmt());
            }
            stmtNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            stmtNode.addChild(Stmt(isFun, type, true));
        } else if (tokens.get(currentTokenIndex).getType().equals("BREAKTK") ||
                tokens.get(currentTokenIndex).getType().equals("CONTINUETK")) {
            if (!isFor) {
                errors.add(new Token("m", " ", tokens.get(currentTokenIndex).getLineNum()));
            }
            stmtNode.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            }
        } else if (tokens.get(currentTokenIndex).getType().equals("RETURNTK")) {
            stmtNode.addChild(new TreeNode("RETURNTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                if (tokens.get(currentTokenIndex).getType().equals("PLUS") || tokens.get(currentTokenIndex).getType().equals("MINU") ||
                        tokens.get(currentTokenIndex).getType().equals("NOT") || tokens.get(currentTokenIndex).getType().equals("LPARENT") ||
                        tokens.get(currentTokenIndex).getType().equals("IDENFR") || tokens.get(currentTokenIndex).getType().equals("INTCON") ||
                        tokens.get(currentTokenIndex).getType().equals("CHRCON")) {
                    if (type.equals("VoidFunc")) {
                        errors.add(new Token("f", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                    }
                    stmtNode.addChild(Exp());
                    if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                        errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                    } else {
                        stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                        currentTokenIndex++;
                    }
                } else {
                    errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                }
            } else {
                stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            }
        } else if (tokens.get(currentTokenIndex).getType().equals("PRINTFTK")) {
            int currentTokenIndex1 = currentTokenIndex;
            stmtNode.addChild(new TreeNode("PRINTFTK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            stmtNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;

            String string = tokens.get(currentTokenIndex).getValue();
            int k = 0;
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '%' && i + 1 < string.length() && (string.charAt(i + 1) == 'c' || string.charAt(i + 1) == 'd')) {
                    k++;
                }
            }
            int m = 0;

            stmtNode.addChild(new TreeNode("STRCON", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
                m++;
                stmtNode.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
                stmtNode.addChild(Exp());
            }
            if (k != m) {
                errors.add(new Token("l", " ", tokens.get(currentTokenIndex1).getLineNum()));
            }
            if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                stmtNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            }
            if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            } else {
                stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            }
        } else if (tokens.get(currentTokenIndex).getType().equals("IDENFR")) {
            int currentTokenIndex1 = currentTokenIndex;
            TreeNode lvalNode = LVal(false);
            if (tokens.get(currentTokenIndex).getType().equals("ASSIGN")) {

                if (tokens.get(currentTokenIndex1).getType().equals("IDENFR")) {
                    for (int i = stack.size() - 1; i >= 0; i--) {
                        SymbolTable symbolTable = stack.get(i);
                        if (symbolTable.symbols.containsKey(tokens.get(currentTokenIndex1).getValue())) {
                            if (symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type.equals("ConstChar") ||
                                    symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type.equals("ConstInt") ||
                                    symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type.equals("ConstCharArray") ||
                                    symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type.equals("ConstIntArray")) {
                                errors.add(new Token("h", " ", tokens.get(currentTokenIndex1).getLineNum()));
                            }
                            break;
                        }
                    }
                }

                currentTokenIndex = currentTokenIndex1;
                stmtNode.addChild(LVal(true));
                stmtNode.addChild(new TreeNode("ASSIGN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
                if (tokens.get(currentTokenIndex).getType().equals("GETINTTK") ||
                        tokens.get(currentTokenIndex).getType().equals("GETCHARTK")){
                    stmtNode.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
                    currentTokenIndex++;
                    dealRp(stmtNode);
                } else {
                    stmtNode.addChild(Exp());
                }
                if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
                    errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                } else {
                    stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                    currentTokenIndex++;
                }
            } else {
                currentTokenIndex = currentTokenIndex1;
                dealExp(stmtNode);
            }
        } else {
            dealExp(stmtNode);
        }
        return stmtNode;
    }

    private void dealExp(TreeNode stmtNode) {
        if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
            stmtNode.addChild(Exp());
        }
        if (!tokens.get(currentTokenIndex).getType().equals("SEMICN")) {
            errors.add(new Token("i", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
        } else {
            stmtNode.addChild(new TreeNode("SEMICN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        }
    }

    private void dealRp(TreeNode stmtNode) {
        stmtNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
            errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
        } else {
            stmtNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
        }
    }

    private TreeNode ForStmt() {
        TreeNode forStmtNode = new TreeNode("<ForStmt>", false, new Token("", "", 0), new ArrayList<>());

        if (tokens.get(currentTokenIndex).getType().equals("IDENFR")) {
            for (int i = stack.size() - 1; i >= 0; i--) {
                SymbolTable symbolTable = stack.get(i);
                if (symbolTable.symbols.containsKey(tokens.get(currentTokenIndex).getValue())) {
                    if (symbolTable.symbols.get(tokens.get(currentTokenIndex).getValue()).type.equals("ConstChar") ||
                            symbolTable.symbols.get(tokens.get(currentTokenIndex).getValue()).type.equals("ConstInt") ||
                            symbolTable.symbols.get(tokens.get(currentTokenIndex).getValue()).type.equals("ConstCharArray") ||
                            symbolTable.symbols.get(tokens.get(currentTokenIndex).getValue()).type.equals("ConstIntArray")) {
                        errors.add(new Token("h", " ", tokens.get(currentTokenIndex).getLineNum()));
                    }
                    break;
                }
            }
        }

        forStmtNode.addChild(LVal(true));
        forStmtNode.addChild(new TreeNode("ASSIGN", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        forStmtNode.addChild(Exp());
        return forStmtNode;
    }

    private TreeNode Exp() {
        TreeNode expNode = new TreeNode("<Exp>", false, new Token("", "", 0), new ArrayList<>());
        expNode.addChild(AddExp());
        return expNode;
    }

    private TreeNode Cond() {
        TreeNode condNode = new TreeNode("<Cond>", false, new Token("", "", 0), new ArrayList<>());
        condNode.addChild(LOrExp());
        return condNode;
    }

    private TreeNode LVal(boolean isTrue) {
        TreeNode lValNode = new TreeNode("<LVal>", false, new Token("", "", 0), new ArrayList<>());
        String ident = tokens.get(currentTokenIndex).getValue();
        lValNode.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;

        if (isTrue) {
            int k = 0;
            for (SymbolTable symbolTable : stack) {
                if (symbolTable.symbols.containsKey(ident)) {
                    k = 1;
                    break;
                }
            }
            if (k == 0) {
                errors.add(new Token("c", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        }

        if (tokens.get(currentTokenIndex).getType().equals("LBRACK")) {
            lValNode.addChild(new TreeNode("LBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            lValNode.addChild(Exp());
            if (tokens.get(currentTokenIndex).getType().equals("RBRACK")) {
                lValNode.addChild(new TreeNode("RBRACK", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            } else if (isTrue){
                errors.add(new Token("k", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        }
        return lValNode;
    }

    private TreeNode PrimaryExp() {
        TreeNode primaryExpNode = new TreeNode("<PrimaryExp>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("LPARENT")) {
            primaryExpNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            primaryExpNode.addChild(Exp());
            if (tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                primaryExpNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            } else {
                errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
        } else if (tokens.get(currentTokenIndex).getType().equals("IDENFR")) {
            primaryExpNode.addChild(LVal(true));
        } else if (tokens.get(currentTokenIndex).getType().equals("INTCON")) {
            primaryExpNode.addChild(Number());
        } else {
            primaryExpNode.addChild(Character());
        }
        return primaryExpNode;
    }

    private TreeNode Number() {
        TreeNode numberNode = new TreeNode("<Number>", false, new Token("", "", 0), new ArrayList<>());
        numberNode.addChild(new TreeNode("INTCON", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        return numberNode;
    }

    private TreeNode Character() {
        TreeNode characterNode = new TreeNode("<Character>", false, new Token("", "", 0), new ArrayList<>());
        characterNode.addChild(new TreeNode("CHRCON", true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        return characterNode;
    }

    private TreeNode UnaryExp() {
        TreeNode unaryExpNode = new TreeNode("<UnaryExp>", false, new Token("", "", 0), new ArrayList<>());
        if (tokens.get(currentTokenIndex).getType().equals("PLUS") ||
                tokens.get(currentTokenIndex).getType().equals("MINU") ||
                tokens.get(currentTokenIndex).getType().equals("NOT")){
            unaryExpNode.addChild(UnaryOp());
            unaryExpNode.addChild(UnaryExp());
        } else if (tokens.get(currentTokenIndex).getType().equals("IDENFR") &&
                tokens.get(currentTokenIndex + 1).getType().equals("LPARENT")) {
            String ident = tokens.get(currentTokenIndex).getValue();
            int lineNum = tokens.get(currentTokenIndex).getLineNum();
            unaryExpNode.addChild(new TreeNode("IDENFR", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            Symbol symbol = null;
            int k = 0;
            for (SymbolTable symbolTable : stack) {
                if (symbolTable.symbols.containsKey(ident)) {
                    k = 1;
                    symbol = symbolTable.symbols.get(ident);
                    break;
                }
            }
            if (k == 0) {
                errors.add(new Token("c", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
            }
            k = 0;
            unaryExpNode.addChild(new TreeNode("LPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                if (tokens.get(currentTokenIndex).getType().equals("PLUS") || tokens.get(currentTokenIndex).getType().equals("MINU") ||
                        tokens.get(currentTokenIndex).getType().equals("NOT") || tokens.get(currentTokenIndex).getType().equals("LPARENT") ||
                        tokens.get(currentTokenIndex).getType().equals("IDENFR") || tokens.get(currentTokenIndex).getType().equals("INTCON") ||
                        tokens.get(currentTokenIndex).getType().equals("CHRCON")) {
                    unaryExpNode.addChild(FuncRParams(symbol, lineNum));
                    k = 1;
                } else {
                    errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                }
            }
            if (!tokens.get(currentTokenIndex).getType().equals("RPARENT")) {
                if (k == 1) {
                    errors.add(new Token("j", " ", tokens.get(currentTokenIndex - 1).getLineNum()));
                }
            } else {
                unaryExpNode.addChild(new TreeNode("RPARENT", true, tokens.get(currentTokenIndex), new ArrayList<>()));
                currentTokenIndex++;
            }
            if (k == 0 && symbol != null) {
                if (symbol.paramNum != 0) {
                    errors.add(new Token("d", " ", lineNum));
                }
            }
        } else {
            unaryExpNode.addChild(PrimaryExp());
        }
        return unaryExpNode;
    }

    private TreeNode UnaryOp() {
        TreeNode opNode = new TreeNode("<UnaryOp>", false, new Token("", "", 0), new ArrayList<>());
        opNode.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
        currentTokenIndex++;
        return opNode;
    }

    private TreeNode FuncRParams(Symbol symbol, int lineNum) {
        TreeNode funcRParamsNode = new TreeNode("<FuncRParams>", false, new Token("", "", 0), new ArrayList<>());

        int k = 1;
        boolean isError = false;
        Token errorToken = null;
        if (symbol != null && !symbol.paramsType.isEmpty()) {
            int currentTokenIndex1 = currentTokenIndex;
            while (tokens.get(currentTokenIndex1).getType().equals("LPARENT")) {
                currentTokenIndex1++;
            }
            if (tokens.get(currentTokenIndex1).getType().equals("NOT") || tokens.get(currentTokenIndex1).getType().equals("PLUS") || tokens.get(currentTokenIndex1).getType().equals("MINU")) {
                if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                    errorToken = new Token("e", " ", lineNum);
                    isError = true;
                }
            }
            if (tokens.get(currentTokenIndex1).getType().equals("INTCON") || tokens.get(currentTokenIndex1).getType().equals("CHRCON")) {
                if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                    errorToken = new Token("e", " ", lineNum);
                    isError = true;
                }
            }
            if (tokens.get(currentTokenIndex1).getType().equals("IDENFR") && tokens.get(currentTokenIndex1 + 1).getType().equals("LPARENT")) {
                if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                    errorToken = new Token("e", " ", lineNum);
                    isError = true;
                }
            } else if (tokens.get(currentTokenIndex1).getType().equals("IDENFR")) {
                String type = null;
                for (int i = stack.size() - 1; i >= 0; i--) {
                    SymbolTable symbolTable = stack.get(i);
                    if (symbolTable.symbols.containsKey(tokens.get(currentTokenIndex1).getValue())) {
                        type = symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type;
                        break;
                    }
                }
                if (type != null && (type.equals("ConstCharArray") || type.equals("CharArray") || type.equals("ConstIntArray") || type.equals("IntArray"))) {
                    if (tokens.get(currentTokenIndex1 + 1).getType().equals("LBRACK")) {
                        if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                            errorToken = new Token("e", " ", lineNum);
                            isError = true;
                        }
                    } else if (type.equals("ConstCharArray") || type.equals("CharArray")){
                        if (!Objects.equals(symbol.paramsType.get(k - 1), "CharArray")) {
                            errorToken = new Token("e", " ", lineNum);
                            isError = true;
                        }
                    } else {
                        if (!Objects.equals(symbol.paramsType.get(k - 1), "IntArray")) {
                            errorToken = new Token("e", " ", lineNum);
                            isError = true;
                        }
                    }
                } else if (type != null && (type.equals("ConstChar") || type.equals("Char") || type.equals("ConstInt") || type.equals("Int"))) {
                    if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                        errorToken = new Token("e", " ", lineNum);
                        isError = true;
                    }
                }
            }
        }

        funcRParamsNode.addChild(Exp());
        k = 2;
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType().equals("COMMA")) {
            funcRParamsNode.addChild(new TreeNode("COMMA", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;

            if (symbol != null && !symbol.paramsType.isEmpty() && symbol.paramsType.size() >= k && !isError) {
                int currentTokenIndex1 = currentTokenIndex;
                while (tokens.get(currentTokenIndex1).getType().equals("LPARENT")) {
                    currentTokenIndex1++;
                }
                if (tokens.get(currentTokenIndex1).getType().equals("NOT") || tokens.get(currentTokenIndex1).getType().equals("PLUS") || tokens.get(currentTokenIndex1).getType().equals("MINU")) {
                    if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                        errorToken = new Token("e", " ", lineNum);
                        isError = true;
                    }
                }
                if (tokens.get(currentTokenIndex1).getType().equals("INTCON") || tokens.get(currentTokenIndex1).getType().equals("CHRCON")) {
                    if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                        errorToken = new Token("e", " ", lineNum);
                        isError = true;
                    }
                }
                if (tokens.get(currentTokenIndex1).getType().equals("IDENFR") && tokens.get(currentTokenIndex1 + 1).getType().equals("LPARENT")) {
                    if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                        errorToken = new Token("e", " ", lineNum);
                        isError = true;
                    }
                } else if (tokens.get(currentTokenIndex1).getType().equals("IDENFR")) {
                    String type = null;
                    for (int i = stack.size() - 1; i >= 0; i--) {
                        SymbolTable symbolTable = stack.get(i);
                        if (symbolTable.symbols.containsKey(tokens.get(currentTokenIndex1).getValue())) {
                            type = symbolTable.symbols.get(tokens.get(currentTokenIndex1).getValue()).type;
                            break;
                        }
                    }
                    if (type != null && (type.equals("ConstCharArray") || type.equals("CharArray") || type.equals("ConstIntArray") || type.equals("IntArray"))) {
                        if (tokens.get(currentTokenIndex1 + 1).getType().equals("LBRACK")) {
                            if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                                errorToken = new Token("e", " ", lineNum);
                                isError = true;
                            }
                        } else if (type.equals("ConstCharArray") || type.equals("CharArray")){
                            if (!Objects.equals(symbol.paramsType.get(k - 1), "CharArray")) {
                                errorToken = new Token("e", " ", lineNum);
                                isError = true;
                            }
                        } else {
                            if (!Objects.equals(symbol.paramsType.get(k - 1), "IntArray")) {
                                errorToken = new Token("e", " ", lineNum);
                                isError = true;
                            }
                        }
                    } else if (type != null && (type.equals("ConstChar") || type.equals("Char") || type.equals("ConstInt") || type.equals("Int"))) {
                        if (!Objects.equals(symbol.paramsType.get(k - 1), "Char") && !Objects.equals(symbol.paramsType.get(k - 1), "Int")) {
                            errorToken = new Token("e", " ", lineNum);
                            isError = true;
                        }
                    }
                }
            }
            funcRParamsNode.addChild(Exp());
            k++;
        }
        if (symbol != null && symbol.paramsType != null) {
            if (k - 1 != symbol.paramNum) {
                errors.add(new Token("d", " ", lineNum));
            } else if (errorToken != null) {
                errors.add(errorToken);
            }
        } else if (errorToken != null) {
            errors.add(errorToken);
        }
        return funcRParamsNode;
    }

    private TreeNode MulExp() {
        TreeNode multiExpNode = new TreeNode("<MulExp>", false, new Token("", "", 0), new ArrayList<>());
        multiExpNode.addChild(UnaryExp());
        while (tokens.get(currentTokenIndex).getType().equals("MULT") ||
                tokens.get(currentTokenIndex).getType().equals("DIV") ||
                tokens.get(currentTokenIndex).getType().equals("MOD")){
            TreeNode mulExpNode2 = new TreeNode("<MulExp>", false, new Token("", "", 0), new ArrayList<>());
            mulExpNode2.addChild(multiExpNode);
            mulExpNode2.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            mulExpNode2.addChild(UnaryExp());
            multiExpNode = mulExpNode2;
        }
        return multiExpNode;
    }

    private TreeNode AddExp() {
        TreeNode addExpNode = new TreeNode("<AddExp>", false, new Token("", "", 0), new ArrayList<>());
        addExpNode.addChild(MulExp());
        while (tokens.get(currentTokenIndex).getType().equals("PLUS") ||
                tokens.get(currentTokenIndex).getType().equals("MINU")){
            TreeNode addExpNode2 = new TreeNode("<AddExp>", false, new Token("", "", 0), new ArrayList<>());
            addExpNode2.addChild(addExpNode);
            addExpNode2.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            addExpNode2.addChild(MulExp());
            addExpNode = addExpNode2;
        }
        return addExpNode;
    }

    private TreeNode RelExp() {
        TreeNode relExpNode = new TreeNode("<RelExp>", false, new Token("", "", 0), new ArrayList<>());
        relExpNode.addChild(AddExp());
        while (tokens.get(currentTokenIndex).getType().equals("LSS") ||
                tokens.get(currentTokenIndex).getType().equals("GRE") ||
                tokens.get(currentTokenIndex).getType().equals("GEQ") ||
                tokens.get(currentTokenIndex).getType().equals("LEQ")){
            TreeNode relExpNode2 = new TreeNode("<RelExp>", false, new Token("", "", 0), new ArrayList<>());
            relExpNode2.addChild(relExpNode);
            relExpNode2.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            relExpNode2.addChild(AddExp());
            relExpNode = relExpNode2;
        }
        return relExpNode;
    }

    private TreeNode EqExp() {
        TreeNode eqExpNode = new TreeNode("<EqExp>", false, new Token("", "", 0), new ArrayList<>());
        eqExpNode.addChild(RelExp());
        while (tokens.get(currentTokenIndex).getType().equals("EQL") ||
                tokens.get(currentTokenIndex).getType().equals("NEQ")){
            TreeNode eqExpNode2 = new TreeNode("<EqExp>", false, new Token("", "", 0), new ArrayList<>());
            eqExpNode2.addChild(eqExpNode);
            eqExpNode2.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            eqExpNode2.addChild(RelExp());
            eqExpNode = eqExpNode2;
        }
        return eqExpNode;
    }

    private TreeNode LAndExp() {
        TreeNode andExpNode = new TreeNode("<LAndExp>", false, new Token("", "", 0), new ArrayList<>());
        andExpNode.addChild(EqExp());
        while (tokens.get(currentTokenIndex).getType().equals("AND") ||
                tokens.get(currentTokenIndex).getType().equals("errorA1")){
            if (!tokens.get(currentTokenIndex).getType().equals("AND")) {
                errors.add(new Token("a", " ", tokens.get(currentTokenIndex).getLineNum()));
            }
            TreeNode andExpNode2 = new TreeNode("<LAndExp>", false, new Token("", "", 0), new ArrayList<>());
            andExpNode2.addChild(andExpNode);
            andExpNode2.addChild(new TreeNode("AND", true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            andExpNode2.addChild(EqExp());
            andExpNode = andExpNode2;
        }
        return andExpNode;
    }

    private TreeNode LOrExp() {
        TreeNode orExpNode = new TreeNode("<LOrExp>", false, new Token("", "", 0), new ArrayList<>());
        orExpNode.addChild(LAndExp());
        while (tokens.get(currentTokenIndex).getType().equals("OR") ||
                tokens.get(currentTokenIndex).getType().equals("errorA2")){
            if (!tokens.get(currentTokenIndex).getType().equals("OR")) {
                errors.add(new Token("a", " ", tokens.get(currentTokenIndex).getLineNum()));
            }
            TreeNode orExpNode2 = new TreeNode("<LOrExp>", false, new Token("", "", 0), new ArrayList<>());
            orExpNode2.addChild(orExpNode);
            orExpNode2.addChild(new TreeNode(tokens.get(currentTokenIndex).getType(), true, tokens.get(currentTokenIndex), new ArrayList<>()));
            currentTokenIndex++;
            orExpNode2.addChild(LAndExp());
            orExpNode = orExpNode2;
        }
        return orExpNode;
    }

    private TreeNode ConstExp() {
        TreeNode constExpNode = new TreeNode("<ConstExp>", false, new Token("", "", 0), new ArrayList<>());
        constExpNode.addChild(AddExp());
        return constExpNode;
    }

    public List<Token> getErrors() {
        return errors;
    }

}