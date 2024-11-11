package frontend;

import java.util.List;

public class MainFuncDefNode extends TreeNode{
    public BlockNode blockNode;
    public MainFuncDefNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        blockNode = null;
    }
}
