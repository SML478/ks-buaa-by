package frontend;

import java.util.ArrayList;
import java.util.List;

public class FuncRParamsNode extends TreeNode{
    public ArrayList<ExpNode> expNodes;
    public FuncRParamsNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        expNodes = new ArrayList<>();
    }
}
