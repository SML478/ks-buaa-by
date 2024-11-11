package frontend;

import java.util.List;

public class ConstDefNode extends TreeNode{
    public ConstExpNode constExpNode;
    public ConstInitValNode constInitValNode;
    public ConstDefNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        constExpNode = null;
        constInitValNode = null;
    }
}
