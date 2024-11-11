package frontend;

import java.util.List;

public class VarDefNode extends TreeNode{
    public ConstExpNode constExpNode;
    public InitValNode initValNode;
    public VarDefNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        constExpNode = null;
        initValNode = null;
    }
}
