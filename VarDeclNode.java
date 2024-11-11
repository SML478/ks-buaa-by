package frontend;

import java.util.ArrayList;
import java.util.List;

public class VarDeclNode extends TreeNode{
    public BTypeNode bTypeNode;
    public ArrayList<VarDefNode> varDefNodes;
    public VarDeclNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        bTypeNode = null;
        varDefNodes = new ArrayList<>();
    }
}
