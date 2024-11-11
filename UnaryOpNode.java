package frontend;

import java.util.List;

public class UnaryOpNode extends TreeNode{
    public UnaryOpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
    }
}
