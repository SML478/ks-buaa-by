package frontend;

import java.util.List;

public class ConstExpNode extends TreeNode{
    public AddExpNode addExpNode;
    public ConstExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        addExpNode = null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (TreeNode child : children) {
            if (child.isLeaf) {
                str.append(child.token.getValue());
            } else {
                str.append(child.toString1());
            }
        }
        return str.toString();
    }
}
