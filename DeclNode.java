package frontend;

import java.util.List;

public class DeclNode extends TreeNode{
    public ConstDeclNode constDecl;
    public VarDeclNode varDecl;
    public DeclNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        constDecl = null;
        varDecl = null;
    }
}
