package frontend;

import java.util.List;

public class AddExpNode extends TreeNode{
    public MulExpNode mulExpNode;
    public AddExpNode addExpNode;
    public AddExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        mulExpNode = null;
        addExpNode = null;
    }
}
