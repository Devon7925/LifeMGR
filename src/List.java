import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List implements Serializable{
    private static final long serialVersionUID = 2L;
    MutableString name = new MutableString("");

	ArrayList<List> items;
    List holder;
    
    double progress;
    int importance;
    
    boolean persistant = false;
    int id = -1;

    public List(String name, List holder){
        this.name = new MutableString(name);
        this.holder = holder;
        progress = 0;
        id = ID.getId();
        if(holder != null)
            importance = holder.importance;
        items = new ArrayList<List>();
    }

    public List(List list){
        this(list.name.getValue(), list.holder!=null?list.holder:null);
        items = new ArrayList<>(list.items.size());
        list.items.forEach(n->items.add(new List(n)));
        progress = list.progress;
        importance = list.importance;
        persistant = list.persistant;
        id = list.id;
    }

    public void update() {
        if(id == -1) id = ID.getId();
        if(items.size() == 0) return;
        progress = 0;
        items.forEach(n->n.update());
        progress = items.stream().collect(Collectors.averagingDouble(n -> n.progress));
        if(items.size() == 0) progress = 1;
    }
    public void check(boolean b){
        items.forEach(n->n.check(b));
        progress = b?1:0;
        update();
    }
    void clear(){
        items.forEach(n->n.clear());
        if(progress == 1){
            if(persistant) progress = 0;
            else holder.remove(this);
        }
    }
    void setpersistant(){
        persistant = true;
        if(holder != null)holder.setpersistant();
    }
    public int level(){
        return holder == null?0:(holder.level()+1);
    }
    public List get(List l1){
        if(l1.id == id){
            return this;
        }else {
            return items.stream().map(n -> n.get(l1)).filter(n -> n != null).findAny().orElse(null);
        }
    }
    public List get(int index){
        return items.get(index);
    }
    public List getFromID(int id){
        if(this.id == id) return this;
        return items.stream().map(n->n.getFromID(id)).filter(n->n!=null).findAny().orElse(null);
    }
    public boolean set(List l1, List l2){
        if(items.contains(l1)){
            items.set(items.indexOf(l1), l2);
            return true;
        }else {
            return items.stream().anyMatch(n->n.set(l1, l2));
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
            return items.stream().anyMatch(n -> n.addTo(l1, l2));
        }
    }
    public boolean addTo(List l1, int i, List l2){
        if(this == l1){
            add(i, l2);
            l2.holder = this;
            return true;
        }else {
            return items.stream().anyMatch(n -> n.addTo(l1, i, l2));
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
            return items.stream().anyMatch(n -> n.remFrom(l1, l2));
        }
    }
    public List getFirst() {
        if(items.size() > 0){
            return items.stream().map(n -> n.getFirst()).filter(n -> n != null).findAny().orElse(null);
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
        if(this.id == l.id){
            return this.next();
        }else if(items.size() > 0){
            return items.stream().map(n -> n.getNext(l)).filter(n -> n != null).findAny().orElse(null);
        }
        return null;
    }
    public List next(){
        if(items.size() > 0){
            return get(0);
        }else{
            return upnext();
        }
    }
    public List upnext(){
        if(holder != null){
            if(holder.items.indexOf(this) < holder.items.size()-1){
                return holder.get(holder.items.indexOf(this)+1);
            }else{
                return holder.upnext();
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
        return (ArrayList<List>) Stream.concat(
            list.items.stream().filter(n -> n.importance == prioty).map(n->{n.items = n.find(prioty); return n;}),
            items.stream().filter(e -> e.importance != prioty).filter(e -> e.find(prioty).size() != 0).map(n->{n.items = n.find(prioty); n.importance = -prioty; return n;})
        ).collect(Collectors.toList());
    }
    public List top(){
        if(holder == null)return this;
        return holder.top();
    }
    public int countit() {
        return items.stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }
    public int countit(int index) {
        return items.subList(0, index).stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }
}