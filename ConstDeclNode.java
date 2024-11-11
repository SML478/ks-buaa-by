package frontend;

import java.util.ArrayList;
import java.util.List;

public class ConstDeclNode extends TreeNode{
    public BTypeNode bTypeNode;
    public ArrayList<ConstDefNode> constDefNodes;
    public ConstDeclNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        constDefNodes = new ArrayList<>();
        bTypeNode = null;
    }
}
