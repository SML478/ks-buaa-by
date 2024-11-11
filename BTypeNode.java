package frontend;

import java.util.List;

public class BTypeNode extends TreeNode{
    public BTypeNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
    }
}
