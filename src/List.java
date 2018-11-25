import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List implements Serializable{
    private static final long serialVersionUID = 2L;
    MutableString name = new MutableString("");

	ArrayList<List> items;
    List holder;
    
    boolean setsucess;
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
        for(List l : list.items)
            items.add(new List(l));
        progress = list.progress;
        importance = list.importance;
        persistant = list.persistant;
        id = list.id;
    }
    public void update() {
        if(id == -1) id = ID.getId();
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
        if(l1.id == id){
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
    public List getFromID(int id){
        if(this.id == id) return this;
        for (List l : items) {
            if(l.getFromID(id) != null) return l.getFromID(id);
        }
        return null;
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
        if(this.id == l.id){
            return this.next();
        }else if(items.size() > 0){
            for(List item : items){
                if(item.getNext(l) != null) return item.getNext(l);
            }
            return null;
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
}