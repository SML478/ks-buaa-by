package frontend;

import java.util.List;

public class NumberNode extends TreeNode{
    public NumberNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
    }
}
