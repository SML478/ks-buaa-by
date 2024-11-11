package frontend;

import java.util.List;

public class MulExpNode extends TreeNode{
    public UnaryExpNode unaryExpNode;
    public MulExpNode mulExpNode;
    public MulExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        unaryExpNode = null;
        mulExpNode = null;
    }
}
