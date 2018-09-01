import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List extends Elem{
    private static final long serialVersionUID = 2L;
	ArrayList<List> items;
    List focus;
    List top;

    boolean collapsed;
    boolean setsucess;

    public List(String name, List holder){
        super(name, holder);
        if(holder == null)top = this;
        else {
            top = holder.top;
            importance = holder.importance;
        }
        items = new ArrayList<List>();
    }
    public List(List list){
        this(list.name.getValue(), list.holder!=null?list.holder:null);
        items = new ArrayList<>(list.items.size());
        for(List l : list.items)
            items.add(new List(l));
        if(list.focus != null) focus = new List(list.focus);
        top = this;
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
    public int draw(Graphics2D g2, int indent, Point loc, List hovered) {
        super.draw(g2, indent, loc, hovered);
        int i = 1;
        if(!collapsed){
            for(Elem e : items){
                i += e.draw(g2, indent+1, new Point(loc.x, loc.y+(Arith.lineheight(g2)+Settings.line)*i), hovered);
            }
        }
        g2.setColor(Color.lightGray);
        if(items.size() > 0)//draw clarifying lines
            g2.drawLine(
                Settings.indent*indent+loc.x+Arith.lineheight(g2)/4, 
                loc.y+Arith.lineheight(g2)+Settings.line/2, 
                Settings.indent*indent+loc.x+Arith.lineheight(g2)/4, 
                loc.y+Arith.lineheight(g2)+(Arith.lineheight(g2)+Settings.line)*(i-1));
        return i;
    }
    public void clickrecur(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, Arith.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0){
            if(top == null){
                nofocus();
            }else{
                top.nofocus();
            }
        }else if(index == 0){
            click(g2, x, y);
        }else if(index > items.size()){
            return;
        }else{
            items.get(--index).clickrecur(g2, x-Settings.indent, (y-(countit(index))*(Arith.lineheight(g2)+Settings.line)));
            update();
        }
    }
    public void click(Graphics2D g2, int x, int y) {
        int x1 = 0;
        if(x < x1){
            if(top == null){
                nofocus();
            }else{
                top.nofocus();
            }
            return;
        }
        x1 += Arith.lineheight(g2)+Settings.linespace;
        if(x < x1) {
            check(progress < 1);
            // focus = null;
            // selected = true;
            holder.setfocus(this);
            return;
        }
        x1 += Settings.linespace+((Arith.linewidth(g2, name)>Settings.buttondist)?Arith.linewidth(g2, name):Settings.buttondist);
        if(x < x1) {
            if(focus == null) {
                collapsed = !collapsed;
                if(items.size() == 0){
                    collapsed = false;
                }
            }
            // focus = null;
            // selected = true;
            if(holder != null)holder.setfocus(this);
            return;
        }
        x1 += Arith.lineheight(g2);
        if(x < x1){
            holder.remove(this);
            return;
        }
        x1 += Arith.lineheight(g2);
        if(x < x1){
            addto();
            return;
        }else{
            if(top == null){
                nofocus();
            }else{
                top.nofocus();
            }
            return;
        }
    }
    public void check(boolean b){
        for(Elem e : items){
            e.check(b);
        }
        super.check(b);
        update();
    }
    public List hover(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, Arith.lineheight(g2)+Settings.line);
        index = index(index);
        if(index <= 0){
            return this;
        }else if(index > items.size()){
            return null;
        }else{
            return items.get(--index).hover(g2, x-Settings.indent, (y-(countit(index))*(Arith.lineheight(g2)+Settings.line)));
        }
    }
    public int index(int index2){
        int index = index2-1;
        if(index>=0){
            for(int i = 0; i < ((index2>items.size())?items.size():index2); i++){
                index -= items.get(i).countit();
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
            sum += items.get(i).countit();
        }
        return sum+1;
    }
    void setfocus(List e){
        e.selected = true;
        e.focus = null;
        setfocusrecur(e);
    }
    void setfocusrecur(List e){
        if(this != e)focus = e;
        if(holder != null) holder.setfocusrecur(this);
        else getfocus();
    }
    List getfocus(){
        if(focus == null) {selected = true; return this;}
        resetfocus();
        return focus.getfocus();
    }
    void resetfocus(){
        selected = false;
        for(List l : items){
            l.resetfocus();
        }
    }
    void nofocus(){
        focus = null;
        resetfocus();
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
    public List get(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, Arith.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0){
            return null;
        }else if(index == 0){
            return this;
        }else if(index > items.size()){
            return null;
        }else{
            return items.get(--index).get(g2, x-Settings.indent, (y-(countit(index))*(Arith.lineheight(g2)+Settings.line)));
        }
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
            items.get(--index).set(g2, x-Settings.indent, (y-(countit(index))*(Arith.lineheight(g2)+Settings.line)), l);
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
        l.selected = true;
        setfocus(l);
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
            items.get(items.indexOf(l1)).remove(l2);
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
                return holder.items.get(holder.items.indexOf(this)-1);
            }else {
                return holder;
            }
        }else{
            return getFirst();
        }
    }
    public List getNext(List l) {
        if(this == l){
            if(items.size() > 0){
                return items.get(0);
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
            if(holder.items.indexOf(this) < holder.items.size()-1){
                return holder.items.get(holder.items.indexOf(this)+1);
            }else {
                return holder.next();
            }
        }else{
            return getFirst();
        }
    }
    public List propnext(){
        if(items.size() != 0){
            return items.get(0);
        }else if(holder != null){
            if(holder.items.indexOf(this) < holder.items.size()-1){
                return holder.items.get(holder.items.indexOf(this)+1);
            }else {
                return holder.next();
            }
        }else{
            return getFirst();
        }
    }
    public List prioitysort(){
        List orig = new List(this);
        List l = new List("Sorted", null);
        for(int i = 0; i <= 9; i++){
            l.addAll(orig.find(i));
        }
        return l;
    }
    public void setpriority(int prioty){
        importance = prioty;
        items.forEach(n->n.setpriority(prioty));
    }
    public ArrayList<List> find(int prioty){
        List list = new List(this);
        return (ArrayList<List>) Stream.concat(list.items.stream().filter(n -> n.importance == prioty).map(n->{n.items = (ArrayList<List>) n.items.stream().filter(e -> e.importance == prioty).collect(Collectors.toList()); return n;}), items.stream().filter(e -> e.importance != prioty).flatMap(n->n.find(prioty).stream())).collect(Collectors.toList());
    }
}