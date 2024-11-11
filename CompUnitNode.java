package frontend;

import java.util.ArrayList;
import java.util.List;

public class CompUnitNode extends TreeNode{
    public ArrayList<DeclNode> declNodes;
    public ArrayList<FuncDefNode> funcDefNodes;
    public MainFuncDefNode mainFuncDefNode;

    public CompUnitNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
        declNodes = new ArrayList<>();
        funcDefNodes = new ArrayList<>();
        mainFuncDefNode = null;
    }
}
