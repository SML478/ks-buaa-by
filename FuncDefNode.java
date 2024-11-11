package frontend;

import java.util.List;

public class FuncDefNode extends TreeNode{
    public FuncTypeNode funcTypeNode;
    public FuncFParamsNode funcFParamsNode;
    public BlockNode blockNode;
    public FuncDefNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        funcTypeNode = null;
        funcFParamsNode = null;
        blockNode = null;
    }
}
