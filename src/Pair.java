import java.util.ArrayList;

public class Pair {
    public Pair(String label, ArrayList<String> info) {
        this.label = label;
        this.info = info;
    }
    public String label;
    public ArrayList<String> info;

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    public ArrayList<String> getInfo() {
        return info;
    }
    public void setInfo(ArrayList<String> info) {
        this.info = info;
    }
}
