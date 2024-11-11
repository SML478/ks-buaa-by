package frontend;

import java.util.List;

public class CharacterNode extends TreeNode{
    public CharacterNode(String type, boolean isLeaf, Token token, List<TreeNode> children) {
        super(type, isLeaf, token, children);
    }
}
