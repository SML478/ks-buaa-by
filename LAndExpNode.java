package frontend;

import java.util.List;

public class LAndExpNode extends TreeNode{
    public EqExpNode eqExpNode;
    public LAndExpNode lAndExpNode;
    public LAndExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        eqExpNode = null;
        lAndExpNode = null;
    }
}
