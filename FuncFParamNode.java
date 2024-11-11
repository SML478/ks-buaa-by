package frontend;

import java.util.List;

public class FuncFParamNode extends TreeNode{
    public BTypeNode bTypeNode;
    public FuncFParamNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        bTypeNode = null;
    }
}
