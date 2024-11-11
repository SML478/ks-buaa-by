package frontend;

import java.util.ArrayList;
import java.util.List;

public class InitValNode extends TreeNode{
    public ArrayList<ExpNode> expNodes;
    public InitValNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        expNodes = new ArrayList<>();
    }
}
