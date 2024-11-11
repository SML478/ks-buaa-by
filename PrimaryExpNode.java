package frontend;

import java.util.List;

public class PrimaryExpNode extends TreeNode{
    public ExpNode expNode;
    public LValNode lValNode;
    public NumberNode numberNode;
    public CharacterNode characterNode;
    public PrimaryExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        expNode = null;
        lValNode = null;
        numberNode = null;
        characterNode = null;
    }
}
