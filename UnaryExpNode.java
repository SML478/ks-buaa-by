package frontend;

import java.util.List;

public class UnaryExpNode extends TreeNode{
    public PrimaryExpNode primaryExpNode;
    public FuncRParamsNode funcRParamsNode;
    public UnaryOpNode unaryOpNode;
    public UnaryExpNode unaryExpNode;
    public UnaryExpNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        primaryExpNode = null;
        funcRParamsNode = null;
        unaryOpNode = null;
        unaryExpNode = null;
    }
}
