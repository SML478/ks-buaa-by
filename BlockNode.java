package frontend;

import java.util.List;

public class BlockNode extends TreeNode{
    public BlockItemNode blockItem;
    public BlockNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        blockItem = null;
    }
}
