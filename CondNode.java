package frontend;

import java.util.List;

public class CondNode extends TreeNode{
    public LOrExpNode lOrExpNode;
    public CondNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        lOrExpNode = null;
    }
}
