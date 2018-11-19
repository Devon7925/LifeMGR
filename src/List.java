import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List implements Serializable{
    private static final long serialVersionUID = 2L;
    MutableString name = new MutableString("");

	ArrayList<List> items;
    List holder;
    
    boolean collapsed;
    boolean setsucess;
    double progress;
    int importance;
    
    boolean persistant = false;


    public List(String name, List holder){
        this.name = new MutableString(name);
        this.holder = holder;
        progress = 0;
        if(holder != null)
            importance = holder.importance;
        items = new ArrayList<List>();
    }
    public List(List list){
        this(list.name.getValue(), list.holder!=null?list.holder:null);
        items = new ArrayList<>(list.items.size());
        for(List l : list.items)
            items.add(new List(l));
        progress = list.progress;
        importance = list.importance;
        collapsed = list.collapsed;
        persistant = list.persistant;
    }
    public void update() {
        if(items.size() == 0) return;
        progress = 0;
        for(List e : items){
            e.holder = this;
            e.update();
            progress += e.progress;
        }
        progress /= items.size();
        if(items.size() == 0) progress = 1;
    }
    public void check(boolean b){
        for(List e : items){
            e.check(b);
        }
        progress = b?1:0;
        update();
    }
    public int index(int index2){
        int index = index2-1;
        if(index>=0){
            for(int i = 0; i < ((index2>items.size())?items.size():index2); i++){
                index -= get(i).countit();
                if(index < 0){
                    index = i;
                    break;
                }
            }
        }
        return index+1;
    }
    public int countit() {
        int sum = 0;
        if(!collapsed) for(List e : items){
            sum += e.countit();
        }
        return sum+1;
    }
    public int countit(int index) {
        int sum = 0;
        for(int i = 0; i < index; i++){
            sum += get(i).countit();
        }
        return sum+1;
    }
    void clear(){
        ArrayList<List> toremove = new ArrayList<List>();
        for(List e : items){
            e.clear();
            if(e.progress == 1){
                if(e.persistant) e.progress = 0;
                else toremove.add(e);
            }
        }
        items.removeAll(toremove);
    }
    void setpersistant(){
        persistant = true;
        if(holder != null)holder.setpersistant();
    }
    public int level(){
        if(holder != null){
            return holder.level()+1;
        }else{
            return 0;
        }
    }
    public List get(List l1){
        if(l1.name.getValue().equals(name.getValue())){
            return this;
        }else {
            for (List l : items) {
                if(l.get(l1) != null) return l.get(l1);
            }
            return null;
        }
    }
    public List get(int index){
        return items.get(index);
    }
    public void set(Graphics2D g2, int x, int y, List l) {
        int index = Math.floorDiv(y, Arith.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0){
            return;
        }else if(index == 0){
            if(holder.items.indexOf(this) != -1) holder.items.set(holder.items.indexOf(this), l);
        }else if(index > items.size()){
            return;
        }else{
            get(--index).set(g2, x-Settings.indent, (y-(countit(index))*(Arith.lineheight(g2)+Settings.line)), l);
        }
    }
    public boolean set(List l1, List l2){
        if(items.contains(l1)){
            items.set(items.indexOf(l1), l2);
            return true;
        }else {
            setsucess = false;
            items.forEach(n -> {if(n.set(l1, l2)) {setsucess = true;} });
            return setsucess;
        }
    }
    public void addAll(ArrayList<List> e) {
        items.addAll(e);
        items.forEach(n->n.holder = this);
    }
    public void add(List e) {
        items.add(e);
        e.holder = this;
    }
    public void add(int i, List e) {
        items.add(i, e);
        e.holder = this;
    }
    public void addto() {
        List l = new List("", this);
        add(l);
    }
    public boolean addTo(List l1, List l2){
        if(this == l1){
            add(l2);
            l2.holder = this;
            return true;
        }else {
            setsucess = false;
            items.forEach(n -> {if(n.addTo(l1, l2)) {setsucess = true;} });
            return setsucess;
        }
    }
    public boolean addTo(List l1, int i, List l2){
        if(this == l1){
            add(i, l2);
            l2.holder = this;
            return true;
        }else {
            setsucess = false;
            items.forEach(n -> {if(n.addTo(l1, l2)) {setsucess = true;} });
            return setsucess;
        }
    }
    public void remove(List e) {
        items.remove(e);
        update();
    }
    public boolean remFrom(List l1, List l2){
        if(items.contains(l1)){
            get(items.indexOf(l1)).remove(l2);
            return true;
        }else {
            setsucess = false;
            items.forEach(n -> {if(n.remFrom(l1, l2)) {setsucess = true;} });
            return setsucess;
        }
    }
    public List getFirst() {
        if(items.size() > 0){
            for(List l : items){
                if(l.getFirst() != null) return l.getFirst();
            }
            return null;
        }else if(progress == 0){
            return this;
        }else{
            return null;
        }
    }
    public List prev(){
        if(holder != null){
            if(holder.items.indexOf(this) > 0){
                return holder.get(holder.items.indexOf(this)-1);
            }else {
                return holder;
            }
        }else{
            return getFirst();
        }
    }
    public List getNext(List l) {
        if(this.name.getValue().equals(l.name.getValue())){
            if(items.size() > 0){
                return get(0);
            }else{
                return this.next();
            }
        }else if(items.size() > 0){
            for(List item : items){
                if(item.getNext(l) != null) return item.getNext(l);
            }
            return null;
        }
        return null;
    }
    public List next(){
        if(holder != null){
            if(holder.items.indexOf(holder.get(this)) < holder.items.size()-1){
                return holder.get(holder.items.indexOf(holder.get(this))+1);
            }else {
                return holder.next();
            }
        }else{
            return getFirst();
        }
    }
    public List propnext(){
        if(items.size() != 0){
            return get(0);
        }else if(holder != null){
            if(holder.items.indexOf(this) < holder.items.size()-1){
                return holder.get(holder.items.indexOf(this)+1);
            }else {
                return holder.next();
            }
        }else{
            return getFirst();
        }
    }
    public void setpriority(int prioty){
        importance = prioty;
        items.forEach(n->n.setpriority(prioty));
    }
    public ArrayList<List> find(int prioty){
        List list = new List(this);
        return (ArrayList<List>) Stream.concat(list.items.stream().filter(n -> n.importance == prioty).map(n->{n.items = (ArrayList<List>) n.items.stream().filter(e -> e.importance == prioty).collect(Collectors.toList()); return n;}), items.stream().filter(e -> e.importance != prioty).flatMap(n->n.find(prioty).stream())).collect(Collectors.toList());
    }
    public List top(){
        if(holder == null)return this;
        return holder.top();
    }
}