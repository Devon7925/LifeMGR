import java.io.Serializable;

class MutableString implements Serializable {
    private static final long serialVersionUID = 3L;
	private String value;
    MutableString(String s){
        value = s;
    }
    public void setValue(String val){value = val;}
    public String getValue(){return value;}
    public void append(String val){value+=val;}
  }