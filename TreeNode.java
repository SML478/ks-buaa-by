package frontend;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    String type; // 节点类型，例如 <Stmt>
    List<TreeNode> children; // 子节点列表
    boolean isLeaf;
    Token token;

    public TreeNode(String type , boolean isLeaf , Token token, List<TreeNode> children) {
        this.type = type;
        this.children = children;
        this.isLeaf = isLeaf;
        this.token = token;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public String toString1() {
        StringBuilder str = new StringBuilder();
        for (TreeNode child : children) {
            if (child.isLeaf) {
                str.append(child.token.getValue());
            } else {
                str.append(child.toString1());
            }
        }
        return str.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        if (isLeaf) {
            sb.append(token.toString());
            return sb.toString();
        }
        for (TreeNode child : children) {
            sb.append(child.toString());
            /*if (type.equals("<AddExp>") && child.type.equals("<MulExp>") ||
                    type.equals("<MulExp>") && child.type.equals("<UnaryExp>") ||
                    type.equals("<RelExp>") && child.type.equals("<AddExp>") ||
                    type.equals("<EqExp>") && child.type.equals("<RelExp>") ||
                    type.equals("<LAndExp>") && child.type.equals("<EqExp>") ||
                    type.equals("<LOrExp>") && child.type.equals("<LAndExp>")) {
                sb.append(type).append('\n');
                k = 1;
            } */
        }
        if (!type.equals("<BlockItem>") && !type.equals("<Decl>") && !type.equals("<BType>") && k == 0) {
            sb.append(type).append('\n');
        }
        return sb.toString();
    }

    public TreeNode transNode() {
        switch (type) {
            case "<CompUnit>" -> {
                CompUnitNode compUnitNode = new CompUnitNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Decl>")) {
                        compUnitNode.declNodes.add((DeclNode) child.transNode());
                    } else if (child.type.equals("<FuncDef>")) {
                        compUnitNode.funcDefNodes.add((FuncDefNode) child.transNode());
                    } else {
                        compUnitNode.mainFuncDefNode = (MainFuncDefNode) child.transNode();
                    }
                }
                return compUnitNode;
            }
            case "<Decl>" -> {
                DeclNode declNode = new DeclNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<ConstDecl>")) {
                        declNode.constDecl = (ConstDeclNode) child.transNode();
                    } else {
                        declNode.varDecl = (VarDeclNode) child.transNode();
                    }
                }
                return declNode;
            }
            case "<ConstDecl>" -> {
                ConstDeclNode constDeclNode = new ConstDeclNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<BType>")) {
                        constDeclNode.bTypeNode = (BTypeNode) child.transNode();
                    } else if (child.type.equals("<ConstDef>")) {
                        constDeclNode.constDefNodes.add((ConstDefNode) child.transNode());
                    }
                }
                return constDeclNode;
            }
            case "<BType>" -> {
                return new BTypeNode(type, isLeaf, token, children);
            }
            case "<ConstDef>" -> {
                ConstDefNode constDefNode = new ConstDefNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<ConstExp>")) {
                        constDefNode.constExpNode = (ConstExpNode) child.transNode();
                    } else if (child.type.equals("<ConstInitVal>")) {
                        constDefNode.constInitValNode = (ConstInitValNode) child.transNode();
                    }
                }
                return constDefNode;
            }
            case "<ConstInitVal>" -> {
                ConstInitValNode constInitValNode = new ConstInitValNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<ConstExp>")) {
                        constInitValNode.constExpNodes.add((ConstExpNode) child.transNode());
                    }
                }
                return constInitValNode;
            }
            case "<VarDecl>" -> {
                VarDeclNode varDeclNode = new VarDeclNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<BType>")) {
                        varDeclNode.bTypeNode = (BTypeNode) child.transNode();
                    } else if (child.type.equals("<VarDef>")) {
                        varDeclNode.varDefNodes.add((VarDefNode) child.transNode());
                    }
                }
                return varDeclNode;
            }
            case "<VarDef>" -> {
                VarDefNode varDefNode = new VarDefNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<ConstExp>")) {
                        varDefNode.constExpNode = (ConstExpNode) child.transNode();
                    } else if (child.type.equals("<InitVal>")) {
                        varDefNode.initValNode = (InitValNode) child.transNode();
                    }
                }
                return varDefNode;
            }
            case "<InitVal>" -> {
                InitValNode initValNode = new InitValNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Exp>")) {
                        initValNode.expNodes.add((ExpNode) child.transNode());
                    }
                }
                return initValNode;
            }
            case "<FuncDef>" -> {
                FuncDefNode funcDefNode = new FuncDefNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<FuncType>")) {
                        funcDefNode.funcTypeNode = (FuncTypeNode) child.transNode();
                    } else if (child.type.equals("<FuncFParams>")) {
                        funcDefNode.funcFParamsNode = (FuncFParamsNode) child.transNode();
                    } else if (child.type.equals("<Block>")) {
                        funcDefNode.blockNode = (BlockNode) child.transNode();
                    }
                }
                return funcDefNode;
            }
            case "<MainFuncDef>" -> {
                MainFuncDefNode mainFuncDefNode = new MainFuncDefNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Block>")) {
                        mainFuncDefNode.blockNode = (BlockNode) child.transNode();
                    }
                }
                return mainFuncDefNode;
            }
            case "<FuncType>" -> {
                return new FuncTypeNode(type, isLeaf, token, children);
            }
            case "<FuncFParams>" -> {
                FuncFParamsNode funcFParamsNode = new FuncFParamsNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<FuncFParam>")) {
                        funcFParamsNode.funcFParamNodes.add((FuncFParamNode) child.transNode());
                    }
                }
                return funcFParamsNode;
            }
            case "<FuncFParam>" -> {
                FuncFParamNode funcFParamNode = new FuncFParamNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<BType>")) {
                        funcFParamNode.bTypeNode = (BTypeNode) child.transNode();
                    }
                }
                return funcFParamNode;
            }
            case "<Block>" -> {
                BlockNode blockNode = new BlockNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<BlockItem>")) {
                        blockNode.blockItem = (BlockItemNode) child.transNode();
                    }
                }
                return blockNode;
            }
            case "<BlockItem>" -> {
                BlockItemNode blockItemNode = new BlockItemNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Decl>")) {
                        blockItemNode.declNode = (DeclNode) child.transNode();
                    } else if (child.type.equals("<Stmt>")) {
                        blockItemNode.stmtNode = (StmtNode) child.transNode();
                    }
                }
                return blockItemNode;
            }
            case "<Stmt>" -> {
                StmtNode stmtNode = new StmtNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<LVal>")) {
                        stmtNode.lValNode = (LValNode) child.transNode();
                    } else if (child.type.equals("<Exp>")) {
                        stmtNode.expNodes.add((ExpNode) child.transNode());
                    } else if (child.type.equals("<Cond>")) {
                        stmtNode.condNode = (CondNode) child.transNode();
                    } else if (child.type.equals("<ForStmt>")) {
                        stmtNode.forStmtNodes.add((ForStmtNode) child.transNode());
                    } else if (child.type.equals("<Stmt>")) {
                        stmtNode.stmtNodes.add((StmtNode) child.transNode());
                    }
                }
                return stmtNode;
            }
            case "<ForStmt>" -> {
                ForStmtNode forStmtNode = new ForStmtNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<LVal>")) {
                        forStmtNode.lValNode = (LValNode) child.transNode();
                    } else if (child.type.equals("<Exp>")) {
                        forStmtNode.expNode = (ExpNode) child.transNode();
                    }
                }
                return forStmtNode;
            }
            case "<Exp>" -> {
                ExpNode expNode = new ExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<AddExp>")) {
                        expNode.addExpNode = (AddExpNode) child.transNode();
                    }
                }
                return expNode;
            }
            case "<Cond>" -> {
                CondNode condNode = new CondNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<LOrExp>")) {
                        condNode.lOrExpNode = (LOrExpNode) child.transNode();
                    }
                }
                return condNode;
            }
            case "<LVal>" -> {
                LValNode lValNode = new LValNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<LVal>")) {
                        lValNode.expNode = (ExpNode) child.transNode();
                    }
                }
                return lValNode;
            }
            case "<PrimaryExp>" -> {
                PrimaryExpNode primaryExpNode = new PrimaryExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Exp>")) {
                        primaryExpNode.expNode = (ExpNode) child.transNode();
                    } else if (child.type.equals("<LVal>")) {
                        primaryExpNode.lValNode = (LValNode) child.transNode();
                    } else if (child.type.equals("<Number>")) {
                        primaryExpNode.numberNode = (NumberNode) child.transNode();
                    } else if (child.type.equals("<Character>")) {
                        primaryExpNode.characterNode = (CharacterNode) child.transNode();
                    }
                }
                return primaryExpNode;
            }
            case "<Number>" -> {
                return new NumberNode(type, isLeaf, token, children);
            }
            case "<Character>" -> {
                return new CharacterNode(type, isLeaf, token, children);
            }
            case "<UnaryExp>" -> {
                UnaryExpNode unaryExpNode = new UnaryExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<PrimaryExp>")) {
                        unaryExpNode.primaryExpNode = (PrimaryExpNode) child.transNode();
                    } else if (child.type.equals("<FuncRParams>")) {
                        unaryExpNode.funcRParamsNode = (FuncRParamsNode) child.transNode();
                    } else if (child.type.equals("<UnaryOp>")) {
                        unaryExpNode.unaryOpNode = (UnaryOpNode) child.transNode();
                    } else if (child.type.equals("<UnaryExp>")) {
                        unaryExpNode.unaryExpNode = (UnaryExpNode) child.transNode();
                    }
                }
                return unaryExpNode;
            }
            case "<UnaryOp>" -> {
                return new UnaryOpNode(type, isLeaf, token, children);
            }
            case "<FuncRParams>" -> {
                FuncRParamsNode funcRParamsNode = new FuncRParamsNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<Exp>")) {
                        funcRParamsNode.expNodes.add((ExpNode) child.transNode());
                    }
                }
                return funcRParamsNode;
            }
            case "<MulExp>" -> {
                MulExpNode mulExpNode = new MulExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<UnaryExp>")) {
                        mulExpNode.unaryExpNode = (UnaryExpNode) child.transNode();
                    } else if (child.type.equals("<MulExp>")) {
                        mulExpNode.mulExpNode = (MulExpNode) child.transNode();
                    }
                }
                return mulExpNode;
            }
            case "<AddExp>" -> {
                AddExpNode addExpNode = new AddExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<MulExp>")) {
                        addExpNode.mulExpNode = (MulExpNode) child.transNode();
                    } else if (child.type.equals("<AddExp>")) {
                        addExpNode.addExpNode = (AddExpNode) child.transNode();
                    }
                }
                return addExpNode;
            }
            case "<RelExp>" -> {
                RelExpNode relExpNode = new RelExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<AddExp>")) {
                        relExpNode.addExpNode = (AddExpNode) child.transNode();
                    } else if (child.type.equals("<RelExp>")) {
                        relExpNode.relExpNode = (RelExpNode) child.transNode();
                    }
                }
                return relExpNode;
            }
            case "<EqExp>" -> {
                EqExpNode eqExpNode = new EqExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<RelExp>")) {
                        eqExpNode.relExpNode = (RelExpNode) child.transNode();
                    } else if (child.type.equals("<EqExp>")) {
                        eqExpNode.eqExpNode = (EqExpNode) child.transNode();
                    }
                }
                return eqExpNode;
            }
            case "<LAndExp>" -> {
                LAndExpNode lAndExpNode = new LAndExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<EqExp>")) {
                        lAndExpNode.eqExpNode = (EqExpNode) child.transNode();
                    } else if (child.type.equals("<LAndExp>")) {
                        lAndExpNode.lAndExpNode = (LAndExpNode) child.transNode();
                    }
                }
                return lAndExpNode;
            }
            case "<LOrExp>" -> {
                LOrExpNode lOrExpNode = new LOrExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<LAndExp>")) {
                        lOrExpNode.lAndExpNode = (LAndExpNode) child.transNode();
                    } else if (child.type.equals("<LOrExp>")) {
                        lOrExpNode.lOrExpNode = (LOrExpNode) child.transNode();
                    }
                }
                return lOrExpNode;
            }
            case "<ConstExp>" -> {
                ConstExpNode constExpNode = new ConstExpNode(type, isLeaf, token, children);
                for (TreeNode child : children) {
                    if (child.type.equals("<AddExp>")) {
                        constExpNode.addExpNode = (AddExpNode) child.transNode();
                    }
                }
                return constExpNode;
            }
            default -> {
                return null;
            }
        }
    }
}

