package frontend;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    public int tabled;      //当前单词所在符号表编号
    public String name;    //当前单词所对应字符串

    public String type;
    

    public int dim;         //存维数

    public int paramNum;    //参数数量
    public List<String> paramsType = new ArrayList<String>();
    public int lineNum;
    private Object initValue;

    public Symbol(int tabled, String name, String type, int lineNum, int dim) {
        this.name = name;
        this.type = type;
        this.lineNum = lineNum;
        this.tabled = tabled;
        this.dim = dim;
        this.paramNum = 0;
        this.initValue = null;
    }

    public int getTabled() {
        return tabled;
    }

    public void addParam(String type) {
        this.paramsType.add(type);
        paramNum++;
    }

    public String toString() {
        return tabled + " " + name + " " + type+ '\n';
    }

    public void setInitValue(Object initValue) {
        this.initValue = initValue;
    }

    public Object getInitValue() {
        return initValue;
    }
}
