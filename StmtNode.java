package frontend;

import java.util.ArrayList;
import java.util.List;

public class StmtNode extends TreeNode{
    public LValNode lValNode;
    public ArrayList<ExpNode> expNodes;
    public BlockNode blockNode;
    public CondNode condNode;
    public ArrayList<StmtNode> stmtNodes;
    public ArrayList<ForStmtNode> forStmtNodes;
    public StmtNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        lValNode = null;
        expNodes = new ArrayList<>();
        blockNode = null;
        condNode = null;
        stmtNodes = new ArrayList<>();
        forStmtNodes = new ArrayList<>();
    }
}
