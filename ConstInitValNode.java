package frontend;

import java.util.ArrayList;
import java.util.List;

public class ConstInitValNode extends TreeNode{
    public ArrayList<ConstExpNode> constExpNodes;
    public ConstInitValNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        constExpNodes = new ArrayList<>();
    }
}
