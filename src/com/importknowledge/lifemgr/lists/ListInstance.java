package com.importknowledge.lifemgr.lists;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListInstance extends AbsList {
    private static final long serialVersionUID = 1L;
    public List orig;

    boolean ordered = true;

    public ListInstance(AbsList list, List orig) {
        super(list);
        this.orig = orig;
    }

    public ListInstance(List list, List orig) {
        super(list);
        items = new ArrayList<>(list.items.size());
        list.items.stream().map(n -> new ListInstance((List) n, orig)).forEach(items::add);
        this.orig = orig;
        if (list.holder != null)
            holder = top().getFromID(list.holder.id);
    }

    public ListInstance(AbsList list) {
        super(list);
    }

    public ListInstance(List list) {
        this((AbsList) list, list);
        items = new ArrayList<>(list.items.stream().map(n -> new ListInstance((List) n)).collect(Collectors.toList()));
    }

    public ListInstance(AbsList list, ListInstance holder) {
        this(list, holder.correct());
        this.holder = holder;
        items = new ArrayList<>(list.items.stream().map(n -> new ListInstance(n, this)).collect(Collectors.toList()));
    }

    public ListInstance(List list, ListInstance holder) {
        this((AbsList) list, holder);
        this.holder = holder;
        items = new ArrayList<>(
                list.items.stream().map(n -> new ListInstance((List) n, this)).collect(Collectors.toList()));
    }

    public ListInstance(ListInstance list) {
        super(list);
    }

    public int index(int index2) {
        int index = index2 - 1;
        if (index >= 0) {
            for (int i = 0; i < Math.min(index2, items.size()); i++) {
                index -= get(i).countit();
                if (index < 0) {
                    index = i;
                    break;
                }
            }
        }
        return index + 1;
    }

    ListInstance unorder() {
        ordered = false;
        for (AbsList l : items)
            ((ListInstance) l).unorder();
        return this;
    }

    @Override
    public void clear() {
        items.forEach(n -> n.clear());
        if (correct().progress == 1) {
            if (correct().persistant)
                check(false);
            else
                ((ListInstance) holder).correct().remove(correct());
        }
    }

    @Override
    void check(boolean b) {
        correct().check(b);
    }

    @Override
    AbsList get(AbsList l1) {
        return correct().get(l1);
    }

    @Override
    ListInstance getFromID(int id) {
        if (this.id == id)
            return this;
        return items.stream().map(n -> (ListInstance) n).map(n -> n.getFromID(id)).filter(n -> n != null).findAny()
                .orElse(null);
    }

    @Override
    public void remove(AbsList e) {
        correct().remove(((ListInstance) e).correct());
        items.remove(e);
        update();
    }

    @Override
    public void add(int i, AbsList e) {
        correct().add(i, e);
    }

    @Override
    public void add(AbsList e) {
        correct().add(e);
    }

    @Override
    AbsList getFirst() {
        if (items.size() > 0) {
            return items.stream().map(n -> n.getFirst()).filter(n -> n != null).findFirst().orElse(null);
        } else if (correct().progress == 0) {
            return this;
        } else {
            return null;
        }
    }

    @Override
    AbsList getNext(AbsList l) {
        if (this.id == l.id) {
            return this.next();
        } else if (items.size() > 0) {
            return items.stream().map(n -> n.getNext(l)).filter(n -> n != null).findAny().orElse(null);
        }
        return null;
    }

    @Override
    boolean set(AbsList l1, AbsList l2) {
        return correct().set(l1, l2);
    }

    @Override
    public void update() {
        correct().update();
    }

    public List correct() {
        return ((List) orig.top().getFromID(id));
    }
}