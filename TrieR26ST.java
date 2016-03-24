import java.lang.*;
import java.util.*;


public class TrieR26ST extends TrieST<Integer>{
    private static final int R = 26;        // for english charachters search

    private Node root = new Node();
    private Node found = null;
    
    private static class Node {
        private Object val; //char letter A - Z
        private Node[] next = new Node[R];
        private String word = null; //save whole string key as word at end node 
    }
    
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }
    
/*-------------------------------------------------*/    
    // for dfs
    public int get(char key) {
        if ( this.found == null ) {
            return get(root.next[key], key);
        }
        else {
            return get(this.found.next[key], key);
        }
        
        
    }
    /*
    public int get(Node x, char key) {
        if (x == null) return -1;
        if (x.val.equals(key)) {
            return 1;
        }
        else
            return 0;
        
    }*/
    public int get(Node x, char key) {

        if (x.val.equals(key)) {
//            this.found = new Node();
            
            this.found = x;
            
            return 1;
        }
        else
            return 0;
        
    }
/*----------------------------------------------------------------------------*/
    
    private Node put(Node x, String key, Integer val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.val = val;
            x.word = key;
            return x;
        }
        // not yet end node
        char c = key.charAt(d);
        x.word = "*";
        x.next[c] = put(x.next[c], key, val, d+1);
        return x;
    }
    
    // test client
    public static void main(String[] args) {

        // build symbol table from standard input
        TrieR26ST st = new TrieR26ST();
        
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            st.put(key, i);
            StdOut.println(key + " " + st.get(key));
        }

        // print results
//        for (String key : st.keys()) {
//            StdOut.println(key + " " + st.get(key));
//        }
    }
    
}