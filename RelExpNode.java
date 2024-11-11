package frontend;

import java.util.List;

public class RelExpNode extends TreeNode{
    public AddExpNode addExpNode;
    public RelExpNode relExpNode;
    public RelExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        addExpNode = null;
        relExpNode = null;
    }
}
