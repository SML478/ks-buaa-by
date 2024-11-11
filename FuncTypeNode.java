package frontend;

import java.util.List;

public class FuncTypeNode extends TreeNode{
    public FuncTypeNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
    }
}
