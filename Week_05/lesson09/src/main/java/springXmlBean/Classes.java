package springXmlBean;

import java.util.List;

public class Classes {
    private List<String> names;

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void foreach(){
        for (String i:names){
            System.out.println(i);
        }
    }
}
