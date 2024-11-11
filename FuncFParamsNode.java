package frontend;

import java.util.ArrayList;
import java.util.List;

public class FuncFParamsNode extends TreeNode{
    public ArrayList<FuncFParamNode> funcFParamNodes;
    public FuncFParamsNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        funcFParamNodes = new ArrayList<>();
    }
}
