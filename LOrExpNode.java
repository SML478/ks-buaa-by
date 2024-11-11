package frontend;

import java.util.List;

public class LOrExpNode extends TreeNode{
    public LAndExpNode lAndExpNode;
    public LOrExpNode lOrExpNode;
    public LOrExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        lAndExpNode = null;
        lOrExpNode = null;
    }
}
