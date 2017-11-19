package BPlusTree;
import java.util.*;
import java.io.*;

public class BPlusTree implements Serializable{

    private static final long serialVersionUID=1L;
    private final int degree;
    private final Node pointRoot;
    private Node root;

    BPlusTree(int m) {
        this.degree = m-1;
        this.pointRoot = new NonLeafNode();
        this.root = new LeafNode();
        this.root.isLeaf = true;
        ((NonLeafNode) this.pointRoot).rightpoint = new LeafNode();
        ((NonLeafNode) this.pointRoot).isRoot = true;
        this.root.parent = pointRoot;
    }

    private static abstract class Node implements Serializable{
        private static final long serialVersionUID=1L;
        Node parent;
        boolean isLeaf;
        boolean isRoot;

        Node() {
            this.isLeaf = false;
            this.isRoot = false;
            this.parent = null;
        }
    }

    private class NonLeafNode extends Node{
        private ArrayList<Pair<Integer, Node>> entry;
        // private NonLeafNode parent;
        private int numEntry;
        private Node rightpoint;

        NonLeafNode() {
            this.numEntry = 0;
            this.entry = new ArrayList<Pair<Integer, Node>>();
            this.rightpoint = null;
            this.isLeaf = false;
        }

        private void insert(int key, Node leftpoint, Node rightpo) {
            int a;
            if (this.isRoot) {
                if (root.isLeaf) {
                  //  System.out.println("innnn");
                    Node temp = new NonLeafNode();
                    Node temp2;
                    temp2 = leftpoint;
                    //((LeafNode)temp2).rightpoint=rightpoint;
                    Pair<Integer, Node> temp3 = new Pair<Integer, Node>(key, temp2);
                    ((NonLeafNode) temp).entry.add(temp3);
                    ((NonLeafNode) temp).numEntry++;
                    temp.parent = pointRoot;
                    ((NonLeafNode) temp).rightpoint = rightpo;
                    root = temp;
                    temp2.parent = root;
                    rightpo.parent = root;
                } else {
                   // System.out.println("innnn2");
                    Node temp = new NonLeafNode();
                    Node temp2 = new NonLeafNode();
                    temp2 = leftpoint;
                    //((NonLeafNode)temp2).rightpoint=((NonLeafNode)rightpoint).rightpoint;
                    Pair<Integer, Node> temp3 = new Pair<Integer, Node>(key, temp2);
                    ((NonLeafNode) temp).entry.add(temp3);
                    ((NonLeafNode) temp).numEntry++;
                    temp.parent = pointRoot;
                    ((NonLeafNode) temp).rightpoint = rightpo;
                    root = temp;
                    rightpo.parent = root;
                    temp2.parent = root;
                }
                return;
            }
            Pair<Integer, Node> temp2 = new Pair<Integer, Node>(key, leftpoint);
            for (a = 0; a < this.numEntry; a++) {
                int temp = this.entry.get(a).first;
                if (key < temp) {
                    this.entry.add(a, temp2);
                    this.numEntry++;
                    int tt = this.entry.get(a + 1).first;
                    Pair<Integer, Node> ttt = new Pair<Integer, Node>(tt, rightpo);
                    this.entry.remove(a + 1);
                    this.entry.add(a + 1, ttt);
                    break;
                }
                if (a + 1 == this.numEntry) {
                    this.entry.add(temp2);
                    this.numEntry++;
                    this.rightpoint = rightpo;
                    break;
                }
            }
            if (this.numEntry == degree) {
                int divindex = this.numEntry / 2;
                int next_key = this.entry.get(divindex).first;
                Node moreNode = new NonLeafNode();
                for (a = divindex + 1; a < this.numEntry; a++) {
                    ((NonLeafNode) moreNode).entry.add(this.entry.get(a));
                    this.entry.get(a).second.parent = moreNode;
                    ((NonLeafNode) moreNode).numEntry++;
                }
                ((NonLeafNode) moreNode).rightpoint = this.rightpoint;
                this.rightpoint = this.entry.get(divindex).second;
                for (a = this.numEntry - 1; a >= divindex; a--) {
                    this.entry.remove(a);
                    this.numEntry--;
                }
                moreNode.parent = this.parent;
                //System.out.println("333333");
                ((NonLeafNode) (this.parent)).insert(next_key, this, moreNode);
            }
        }

        private void delete() {
            if(this.parent.isRoot == true){
                if(this.numEntry!=0) return;
                else{
                    root=this.rightpoint;
                    return;
                }
            }
            int index = 0;
            for (int a = 0; a < ((NonLeafNode) this.parent).numEntry; a++) {
                if (this == ((NonLeafNode) this.parent).entry.get(a).second) {
                    index = a;
                    break;
                }
                if((this == ((NonLeafNode)this.parent).rightpoint )
                        && (a+1 == ((NonLeafNode)this.parent).numEntry)){
                    index = a+1;
                    break;
                }
            }
            if (index != 0) {  //왼쪽에서 빌려올
                Node temp = ((NonLeafNode) this.parent).entry.get(index - 1).second;
                if (((NonLeafNode) temp).numEntry > degree / 2) {
                    int temp2 = ((NonLeafNode) temp).entry.get(((NonLeafNode) temp).numEntry - 1).first;
                    Pair<Integer, Node> tmp = new Pair<Integer, Node>(((NonLeafNode) this.parent).entry.get(index - 1).first, ((NonLeafNode) temp).rightpoint);
                    ((NonLeafNode) temp).rightpoint = ((NonLeafNode) temp).entry.get(((NonLeafNode) temp).numEntry - 1).second;
                    this.entry.add(0, tmp);
                    this.numEntry++;
                    Pair<Integer, Node> tmp2 = new Pair<Integer, Node>(temp2, temp);
                    ((NonLeafNode) this.parent).entry.remove(index-1);
                    ((NonLeafNode) this.parent).entry.add(index-1, tmp2);
                    ((NonLeafNode) temp).entry.remove(((NonLeafNode) temp).numEntry - 1);
                    ((NonLeafNode) temp).numEntry--;
                    return;
                }
            } else if (index != ((NonLeafNode) this.parent).numEntry) {  //오른쪽에서 빌려올때
                Node temp;
                if (index + 1 == ((NonLeafNode) this.parent).numEntry)
                    temp = ((NonLeafNode) this.parent).rightpoint;
                else
                    temp = ((NonLeafNode) this.parent).entry.get(index + 1).second;
                if (((NonLeafNode) temp).numEntry > degree / 2) {
                    int temp2 = ((NonLeafNode)this.parent).entry.get(index).first;
                    int temp3 = ((NonLeafNode)temp).entry.get(0).first;
                    Pair<Integer, Node> tmp = new Pair<Integer, Node>(temp2, this.rightpoint);
                    this.entry.add(tmp);
                    this.numEntry++;
                    this.rightpoint = ((NonLeafNode) temp).entry.get(0).second;
                    ((NonLeafNode) temp).entry.remove(0);
                    ((NonLeafNode)temp).numEntry--;
                    Pair<Integer, Node> tmp2 = new Pair<Integer, Node>(temp3, this);
                    ((NonLeafNode) this.parent).entry.remove(index);
                    ((NonLeafNode) this.parent).entry.add(index, tmp2);
                    return;
                }
            }
            //merge
                if (index != 0) { //왼쪽이랑 합칠
                    Node temp;
                    temp = ((NonLeafNode) this.parent).entry.get(index - 1).second;
                    if (((NonLeafNode) temp).numEntry + this.numEntry + 1 <= degree) {
                        Pair<Integer, Node> tmp = new Pair<Integer, Node>(((NonLeafNode) this.parent).entry.get(index - 1).first, ((NonLeafNode) temp).rightpoint);
                        this.entry.add(0, tmp);
                        this.numEntry++;
                        for (int i = ((NonLeafNode) temp).numEntry - 1; i >= 0; i--) {
                            this.entry.add(0, ((NonLeafNode) temp).entry.get(i));
                            this.numEntry++;
                        }
                        ((NonLeafNode)temp).numEntry=0;
                        ((NonLeafNode) this.parent).entry.remove(index - 1);
                        ((NonLeafNode)this.parent).numEntry--;
                        if(((NonLeafNode)this.parent).numEntry < degree/2)
                            ((NonLeafNode)this.parent).delete();
                        return;
                    }
                } else if (index != ((NonLeafNode) this.parent).numEntry) { // 오른쪽이랑 합칠 때
                    Node temp;
                    if (index + 1 == ((NonLeafNode) this.parent).numEntry)
                        temp = ((NonLeafNode) this.parent).rightpoint;
                    else
                        temp = ((NonLeafNode) this.parent).entry.get(index + 1).second;
                    if (((NonLeafNode) temp).numEntry + this.numEntry + 1 <= degree) {
                        Pair<Integer, Node> tmp = new Pair<Integer, Node>(((NonLeafNode) this.parent).entry.get(index).first, this.rightpoint);
                        ((NonLeafNode)temp).entry.add(0,tmp);
                        ((NonLeafNode)temp).numEntry++;
                        for (int i = this.numEntry - 1; i >= 0; i--) {
                            ((NonLeafNode) temp).entry.add(0, this.entry.get(i));
                            ((NonLeafNode) temp).numEntry++;
                        }
                        this.numEntry=0;
                        ((NonLeafNode) this.parent).entry.remove(index);
                        ((NonLeafNode)this.parent).numEntry--;
                        if(((NonLeafNode)this.parent).numEntry < degree/2)
                            ((NonLeafNode)this.parent).delete();
                        return;
                    }
                }
        }
    }

    private class LeafNode extends Node{
        private ArrayList<Pair<Integer, Integer>> entry;
        private int numEntry;
        Node rightpoint;

        LeafNode() {
            this.entry = new ArrayList<Pair<Integer, Integer>>();
            this.numEntry = 0;
            this.rightpoint = null;
            this.isLeaf = true;
        }

        private void search_key(int key) {
            boolean isPrinted = false;
            for (int a = 0; a < numEntry; a++) {
                if (key == this.entry.get(a).first) {
                    System.out.println(this.entry.get(a).second.intValue());
                    isPrinted = true;
                    break;
                }
            }
            if (!isPrinted)
                System.out.println("NOT FOUND");
            return;
        }

        private void delete(int key) {
            int a;
            for (a = 0; a < this.numEntry; a++) {
                if (this.entry.get(a).first == key) {
                    this.entry.remove(a);
                    this.numEntry--;
                    break;
                }
            }
            if (this.numEntry < degree / 2) {
                int index = 0;
                for (int b = 0; b < ((NonLeafNode) this.parent).numEntry; b++) {
                    if (((NonLeafNode) this.parent).entry.get(b).second == this) {
                        index = b;
                        break;
                    }
                    if((b+1 == ((NonLeafNode)this.parent).numEntry) && (((NonLeafNode)this.parent).rightpoint == this))
                    {
                        index = b+1;
                        break;
                    }
                }
                if (index != 0) {   //왼쪽에서 빌려올때
                    int temp = ((LeafNode) ((NonLeafNode) this.parent).entry.get(index - 1).second).numEntry;
                    Node temp2 = ((NonLeafNode) this.parent).entry.get(index - 1).second;
                    if (temp > degree / 2) {
                        this.entry.add(0, ((LeafNode) temp2).entry.get(temp - 1));
                        this.numEntry++;
                        ((NonLeafNode) this.parent).entry.remove(index - 1);
                        Pair<Integer, Node> tmp = new Pair<Integer, Node>(((LeafNode) temp2).entry.get(temp - 1).first, temp2);
                        ((NonLeafNode) this.parent).entry.add(index - 1, tmp);
                        ((LeafNode) temp2).entry.remove(temp - 1);
                        ((LeafNode) temp2).numEntry--;
                        return;
                    }
                }
                if (index != ((NonLeafNode) this.parent).numEntry) {
                    Node temp2;
                    if (index + 1 == ((NonLeafNode) this.parent).numEntry)
                        temp2 = ((NonLeafNode) this.parent).rightpoint;
                    else
                        temp2 = ((NonLeafNode) this.parent).entry.get(index + 1).second;
                    int temp = ((LeafNode) temp2).numEntry;
                    if (temp > degree / 2) {
                        this.entry.add(((LeafNode) temp2).entry.get(0));
                        this.numEntry++;
                        Pair<Integer, Node> tmp = new Pair<Integer, Node>(((LeafNode) temp2).entry.get(0).first, this);
                        ((NonLeafNode) this.parent).entry.remove(index);
                        ((NonLeafNode) this.parent).entry.add(index,tmp);
                        ((LeafNode) temp2).entry.remove(0);
                        ((LeafNode) temp2).numEntry--;
                        return;
                    }
                }
                //merge

                    Node temp2;
                    if (index != 0) { //왼쪽이랑 합칠 때
                        temp2 = ((NonLeafNode) this.parent).entry.get(index - 1).second;
                        if (((LeafNode) temp2).numEntry + this.numEntry <= degree
                                && ((LeafNode) temp2).numEntry + this.numEntry >= degree / 2) {
                            for (int i = ((LeafNode) temp2).numEntry - 1; i >= 0; i--) {
                                this.entry.add(0, ((LeafNode) temp2).entry.get(i));
                                this.numEntry++;
                            }
                            ((LeafNode) temp2).numEntry = 0;
                            ((NonLeafNode) this.parent).entry.remove(index - 1);
                            ((NonLeafNode) this.parent).numEntry--;
                            if (((NonLeafNode) this.parent).numEntry < degree / 2) {
                                ((NonLeafNode) this.parent).delete();
                            }
                            return;
                        }
                    }
                    if (index != ((NonLeafNode) this.parent).numEntry) {
                        if (index + 1 == ((NonLeafNode) this.parent).numEntry)
                            temp2 = ((NonLeafNode) this.parent).rightpoint;
                        else
                            temp2 = ((NonLeafNode) this.parent).entry.get(index + 1).second;
                        for (int i = this.numEntry-1; i >= 0; i--) {
                            ((LeafNode) temp2).entry.add(0, this.entry.get(i));
                            ((LeafNode) temp2).numEntry++;
                        }
                        this.numEntry=0;
                        if(((NonLeafNode)this.parent).numEntry == index)
                            ((NonLeafNode)this.parent).entry.remove(index-1);
                        else
                            ((NonLeafNode) this.parent).entry.remove(index);
                        ((NonLeafNode)this.parent).numEntry--;
                        if (((NonLeafNode) this.parent).numEntry < degree / 2)
                            ((NonLeafNode) this.parent).delete();
                        return;
                    }
                }
        }

        private void insert(int key, int value) {
            int a;
            Integer tmp1 = new Integer(key);
            Integer tmp2 = new Integer(value);
            Pair<Integer, Integer> tmp3 = new Pair<Integer, Integer>(tmp1, tmp2);
            boolean isInserted = false;
            for (a = 0; a < this.numEntry; a++) {
                int temp = this.entry.get(a).first;
                if (key < temp) {
                    this.entry.add(a, tmp3);
                    this.numEntry++;
                    isInserted = true;
                    break;
                }
            }
            if (!isInserted) {
                this.entry.add(tmp3);
                this.numEntry++;
                isInserted = true;
            }
            if (this.numEntry == degree) {
                int divindex = numEntry / 2;
                int next_key = this.entry.get(divindex).first;
                Node moreNode = new LeafNode();
                for (int i = divindex; i < numEntry; i++) {
                    ((LeafNode) moreNode).entry.add(this.entry.get(i));
                    ((LeafNode) moreNode).numEntry++;
                }
                moreNode.parent = this.parent;
                for (int j = numEntry - 1; j >= divindex; j--) {
                    this.entry.remove(j);
                    this.numEntry--;
                }
                ((LeafNode) moreNode).rightpoint = this.rightpoint;
                this.rightpoint = moreNode;
                ((NonLeafNode) (this.parent)).insert(next_key, this, moreNode);
            }
        }
    }

    public void insert(int key, int value) {
        if (root.isLeaf == true)
            ((LeafNode) root).insert(key, value);
        else {
            Node start = root;
            while (true) {
                if (start.isLeaf == true) {
                    break;
                } else {
                    for (int i = 0; i < ((NonLeafNode) start).numEntry; i++) {
                        if (key < ((NonLeafNode) start).entry.get(i).first) {
                            start = ((NonLeafNode) start).entry.get(i).second;
                            break;
                        }
                        if (i + 1 == ((NonLeafNode) start).numEntry) {
                            start = ((NonLeafNode) start).rightpoint;
                            break;
                        }
                    }
                }
            }
            if (start instanceof LeafNode)
                ((LeafNode) start).insert(key, value);
        }
    }

    public void search_key(int key) {
        if (root.isLeaf == true)
            ((LeafNode) root).search_key(key);
        else {
            Node start = root;
            while (true) {
                if (start.isLeaf == true) break;
                else {
                    for (int a = 0; a < ((NonLeafNode) start).numEntry; a++) {
                        if (key < ((NonLeafNode) start).entry.get(a).first) {
                            System.out.println(((NonLeafNode) start).entry.get(a).first+" 1");
                            start = ((NonLeafNode) start).entry.get(a).second;
                            break;
                        }
                        if (a + 1 == ((NonLeafNode) start).numEntry) {
                            System.out.println(((NonLeafNode) start).entry.get(a).first+" 2");
                            start = ((NonLeafNode) start).rightpoint;
                            break;
                        }
                    }
                }
            }
            ((LeafNode) start).search_key(key);
        }
    }

    public void ranged_search(int left, int right) {
        Node start = root;
        if (root.isLeaf == true) {
            for (int a = 0; a < ((LeafNode) root).numEntry; a++) {
                if (((LeafNode) root).entry.get(a).first >= left
                        && ((LeafNode) root).entry.get(a).first <= right)
                    System.out.println(((LeafNode) root).entry.get(a).first + "," + ((LeafNode) root).entry.get(a).second);
            }
        } else {
            while (true) {
                if (start.isLeaf == true) break;
                else {
                    start = ((NonLeafNode) start).entry.get(0).second;
                }
            }
            int i = 0;
            while (((LeafNode) start).numEntry != i) {
                if (((LeafNode) start).entry.get(i).first >= left && ((LeafNode) start).entry.get(i).first <= right) {
                    System.out.println(((LeafNode) start).entry.get(i).first + "," + ((LeafNode) start).entry.get(i).second);
                }
                i++;
                if (i == ((LeafNode) start).numEntry) {
                    start = ((LeafNode) start).rightpoint;
                    if (start == null) return;
                    i = 0;
                }
            }
        }
    }

    public void delete(int key) {
        if (root.isLeaf == true) {
            for (int a = 0; a < ((LeafNode) root).numEntry; a++) {
                if (((LeafNode) root).entry.get(a).first == key) {
                    ((LeafNode) root).entry.remove(a);
                    ((LeafNode) root).numEntry--;
                }
            }
        } else {
            Node start = root;
            while (true) {
                if (start.isLeaf == true) break;
                else {
                    for (int a = 0; a < ((NonLeafNode) start).numEntry; a++) {
                        if (key < ((NonLeafNode) start).entry.get(a).first) {
                            start = ((NonLeafNode) start).entry.get(a).second;
                            break;
                        }
                        if (a + 1 == ((NonLeafNode) start).numEntry) {
                            start = ((NonLeafNode) start).rightpoint;
                            break;
                        }
                    }
                }
            }
            ((LeafNode) start).delete(key);
        }
    }
    private void printroot(){
        if(root.isLeaf){
            for(int a=0; a<((LeafNode)root).numEntry; a++) {
                System.out.println(((LeafNode) root).entry.get(a).first);
            }
        }
        else{
            for(int a=0; a<((LeafNode)root).numEntry; a++) {
                System.out.println(((LeafNode) root).entry.get(a).first);
            }
        }
    }
}

