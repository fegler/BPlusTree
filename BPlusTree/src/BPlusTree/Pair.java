package BPlusTree;
import java.io.*;

public class Pair <F,S> implements Serializable{
    private static final long serialVersionUID=1L;
    public final F first;
    public final S second;

    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    static <F,S> Pair<F,S> of(F first, S second){
        return new Pair<F,S>(first, second);
    }
}

