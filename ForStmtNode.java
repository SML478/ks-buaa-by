package frontend;

import java.util.List;

public class ForStmtNode extends TreeNode{
    public LValNode lValNode;
    public ExpNode expNode;
    public ForStmtNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        lValNode = null;
        expNode = null;
    }
}
