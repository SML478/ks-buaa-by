package frontend;

import java.util.List;

public class BlockItemNode extends TreeNode{
    public DeclNode declNode;
    public StmtNode stmtNode;
    public BlockItemNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        declNode = null;
        stmtNode = null;
    }
}
