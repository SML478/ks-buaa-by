package frontend;

public class Token {
    private String type;
    private String value;
    private int lineNum;

    public Token(String type, String value, int lineNum) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return type + " " + value + '\n';
    }

    public String toError() {
        return lineNum + " " + type;
    }
}
