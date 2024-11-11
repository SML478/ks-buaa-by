package frontend;

import java.util.List;

public class EqExpNode extends TreeNode{
    public RelExpNode relExpNode;
    public EqExpNode eqExpNode;
    public EqExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        relExpNode = null;
        eqExpNode = null;
    }
}
