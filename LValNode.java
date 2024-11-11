package frontend;

import java.util.List;

public class LValNode extends TreeNode{
    public ExpNode expNode;
    public LValNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        expNode = null;
    }
}
