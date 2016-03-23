import java.lang.*;
import java.security.acl.LastOwnerException;
import java.util.*;


public class BoggleSolver
{
    private boolean test = true;
    private boolean trietest = true;
    
    private static boolean[][]  mark;
//    private SET<String> dict;
    
    //my own TrieR26 class
    private TrieR26ST dict;
    private BoggleBoard board;
//    private Node node; 
    private static char[] word;
    private static int count;
    private static int M;
    private static int N;
    private static character[][] node;
    private static char foundLetter;
    
    private static Queue<String> wordsQueue;
//    private Bag<character>[] adj;
//    private static Bag<Edge>[] adj;
//    private static Bag<DirectedEdge>[] adj;

    
    /*======inner class Trie26 =====================================================================*/
    private class TrieR26ST extends TrieST<Integer>{
        private final int R = 26;        // for english charachters search

        private Node root = new Node();
//        public Node found = null;
        public Node found = root;  // this node indicates where current search reaches
        
        private class Node {
            private Integer val = null; 
//            private Node[] next = new Node[R];
            private Node[] next = null;
            private String word = null; //save whole string key as word at end node 
        }
        
//        private Node get(Node x, String key, int d) {
//            if (x == null) return null;
//            if (d == key.length()) return x;
//            char c = key.charAt(d);
//            return get(x.next[c], key, d+1);
//        }
        
    /*-------------------------------------------------*/    
        // for dfs
        private Node getNode(char key) {
            
            if ( found == null ) {
                
                if (test) StdOut.println(" just start: ");
                
                if (root.next == null) {
                    if (test) StdOut.println(" root has no next! ");
//                    return -1;
                    return null;
                }
                else { //  root.next != null
                    if (test) StdOut.println(" root.next != null " + key);
//                    return get(root.next[key], key);
                    
                    if ( root.next[key] == null ) {
                        if (test) StdOut.println(" root.next[ " + key + " ] == null! " );
                        return null;
                    }
                    else { //found letter key at root.next[key]
                        if (test) StdOut.println(" root.next[ " + key + " ] != null! " );
                        found = root.next[key];
                        return found;
                    }
                }
            }
            else { // found != null
                if (test) StdOut.println(" already started, continue: ");
                
                if ( found.next == null ) {
                    if (test) StdOut.println(" found has no child: found.next[ " + key + " ] == null! ");
//                    return -1;
                    return null;
                }
                else {
                    if (test) StdOut.println(" found has child: " + key);
                    found = found.next[key];
                    return found;
                }
            }

        }
        
     // back.a) this node path is not in dict:
        
        private int getInt(Node x, char key) {
            //found node will initialized at root node, cannot be null!
            int index = key -65;
            //will not let found point to a null node x!
            if (x == null || x.next == null) {
                if (test) StdOut.println(" node x has no child exists !");
                
                return -2;
            }
            
            //case back.a)   x is not null but x.next[key] is null! 
            if (x.next[index] == null) {
                if (test) StdOut.println(" node x.next[ " + key + " ] not exists !");
                
                return -2; // because node.val won't be -2, this indicates no such node!
            }
            else { //if (x.next[key] != null)  
                
                if (test) StdOut.println(" node x.next[ " + key + " ] exists !");
                
                if (x.next[index].val == -1) {
                    //word not end here, surely still has child node, can further search forward
                    if (test) StdOut.println(" words not ends here! ");

                    found =   x.next[index];
                    return x.next[index].val;
                }
                else { //if (x.next[key].val > -1 ) {//word end here, may or may not have child node
                    if (test) StdOut.println(" this word:"+ x.next[index].word +" ends at x.next["+key+"], with value: " + x.next[index].val);
                    
                    //case back.b) 
                    if (x.next[index].next == null) {
                        // when no child node, will x.next == null ? otherwise x.next[R] must be set only if child node exist!
                        // if no child node, found stays at node x, for searching other child node from x, instead of from x.next[key], because you will find null!
                        if (test) StdOut.println(" stop search at letter " + key );
                        
                        return x.next[index].val;
                    }
                    else {
                        // if x.next[key] has child node, going down to forward
                        if (test) StdOut.println(" node x.next[ " + key + " ] has further child node, will search from " + key + " !");
                        
                        found = x.next[index];
                        return x.next[index].val;
                    }
                }
            }     
        }
                
                

    /*----------------------------------------------------------------------------*/
        /**
         * Inserts the key-value pair into the symbol table, overwriting the old value
         * with the new value if the key is already in the symbol table.
         * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
         * @param key the key
         * @param val the value
         * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
         */
        public void put(String key, Integer val) {
            if (val == null) delete(key);
            else {
                if (trietest) StdOut.println(" put(): start to save word: " + key );
                root = put(root, key, val, 0);
            }
        }
        
        private Node put(Node x, String key, Integer val, int d) {
            /*
             if (x == root && root != null) {
                if (trietest) StdOut.println("node of root:" + root + " exist!");
                
                if (root.next == null) { // this is the first time to create child node under root
                    root.next = new Node[R];
                    root.next[key.charAt(d)-65] = new Node();
                    Node x_child = root.next[key.charAt(d)-65];
                    
                    if (trietest) StdOut.println("create new node(): " + x_child +" for root.next[" + key.charAt(d) + "]");
//                    if (trietest) StdOut.println("x: "+key.charAt(d)+" is at: "+ x);
                }
                else if ( root.next[key.charAt(d)-65] == null) { // when this child is created, add into root's children
                    root.next[key.charAt(d)-65] = new Node();
                    Node x_child = root.next[key.charAt(d)-65];
                    if (trietest) StdOut.println("create new node(): " + x_child + " for root.next[" + key.charAt(d) + "]");
//                    if (trietest) StdOut.println("x_child: "+key.charAt(d)+" is at: "+ x_child);
                }
                else { // this child already created under root
                    if (trietest) StdOut.println("root.next[" + key.charAt(d) + "] already exists at: "+ x);
                }  
            }
            else if (x == null) {
                if (trietest) StdOut.println(" node of " + key.charAt(d)+ " index: "+d+" doesn't exist, create new node(): " ); 
                x = new Node();
                if (trietest) StdOut.println(x);
            }
            else { //x != null
                if (trietest) StdOut.println(" key: "+key+" x node : " + x + " already exist! ");
            }
            */
            
//            int index = 0;
            
            if (x == null) {
                if (d == 0) {if (trietest) StdOut.println(" node root doesn't exist, create new node(): " ); }
                else {
//                if (trietest) StdOut.println(" node x doesn't exist, create new node(): " ); 
                    if (trietest) StdOut.println(" node of " + key.charAt(d-1)+ " depth: "+d+" doesn't exist, create new node(): " ); 
                }
                x = new Node();
                if (trietest) StdOut.println(x);
            }

            
            //after creating node x, changing its value and check whether continue for its children
            if (d == (key.length())) {
                x.val = val;
                x.word = key;
                if (trietest) StdOut.println("value: " + val);
                if (trietest) {
                    if( x.next == null)
                        StdOut.println(" when "+key.charAt(d-1)+" is end node, "+key.charAt(d-1)+".next is null! x.word: " + x.word);
                    else    
                        StdOut.println(" when "+key.charAt(d-1)+" is end node, "+key.charAt(d-1)+".next is not null!: " + x.next);
                        
                }
                return  x;
            }
            else { //d != key.length()
                // not yet end node
                
                // check first whether other words end at this node, if yes don't need to allocate new next[], and don't overwrite valid val with -1 !!
//                x.next = new Node[R];
                
                char c = key.charAt(d);
                
                if (trietest) StdOut.println("node x: " +x+ " is not ending node!"+" x.val: "+x.val+" x.word: "+x.word);
                
                if (x.val == null && x.word == null) {
                    x.val = -1;
                    x.word = "*";
                }
                
//                if (trietest) System.out.prinln("letter: %c, %d ", c, c);
                if (trietest) StdOut.println("now x.val: "+x.val+" x.word "+x.word);

                if (x.next == null) { // this is the first time to create child node under x
                    x.next = new Node[R];
                    
                }

                
                
                
                x.next[c-65] = put(x.next[c-65], key, val, d+1);

//                if (trietest) StdOut.println("x: " + x);
//                if (trietest) StdOut.println(" creating new child node: x.next["+c+"-65]: " + x.next[c-65]);
//                for ( int i = 0; i< R; i++) {
//                    if (trietest) StdOut.println("x: "+x+", x.next["+i+"]: " + x.next[i]);
//                }
                
                return  x;
            }
        }
    }
    
    /*============end inner class Trie26=========================================================*/
    
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        //dictionary-yawl.txt

        this.dict = new TrieR26ST();
        for (int i = 0; i < dictionary.length; i++) {
            if (test) StdOut.println(" dict.put(): " + dictionary[i]);
            this.dict.put(dictionary[i], i);
        }
        wordsQueue = null;
        /*
        if (test) {
            // print results
            for (String key : dict.keys()) {
                StdOut.println(key + " " + dict.get(key));
            }
        }*/
    }

     
    private class character {
        private char letter; 
        private int row;
        private int col;
        private int v;
        private SET<Integer> adj;
        
        /*
         * to indicate has been checked with dict or not,
         *  in case this node is not in dict, so won't be 
         *  makred as used in word, but when step back to
         *   its parent node to start searching new neighbor,
         *    already comes to check this node
         *    
         *  careful notice:! differentiate this boolean, 
         *  it only matters from the same parent node, i.e.
         *  if from another parent/neightbor node to check, it is possible
         *   that this node can be stored in dict, after this parent character
         */
//        private Bag<character>[] adj; //        private boolean searched;

        character (char letter, int row, int col) {
            this.letter = letter;
            this.row = row;
            this.col = col;
            this.v = row * N + col;
//            this.adj = null;
        }
    }

    
    private character dfsBoardnext(character c) {
        int row = c.row;
        int col = c.col;
        
        character next = null;
        
        //up
        if (row > 0) {
            //up, left
            if(col > 0){
               if (this.mark[row-1][col-1] != true) {
                   
                   //already initialize this node
                   // check first whether it has already been in c's adj list
                   // if yes, check this node has been searched by c before

                   next = node[row - 1][col - 1];
                   
                   if ( !c.adj.contains(next.v) ) {
                       //already added, means searched by c
                       c.adj.add(next.v);
                       if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                       return next;
                   }
               }
            }
            //up top
            {
                if (this.mark[row-1][col] != true) { 
                    
                    next = node[row - 1][col];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row-1][col+1] != true) { 
                    
                    next = node[row - 1][col + 1];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
            }
            
        }
        // same line 
        {
            //left
            if(col > 0){
                
                if (this.mark[row][col-1] != true) { 
                    next = node[row][col - 1];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
             }
            //right
            if(col < this.N -1 ){
                if (this.mark[row][col+1] != true) { 
                    next = node[row][col + 1];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
            }
        }
        //down
        if (row < this.M -1 ) {
            // down left
            if(col > 0){
               if (this.mark[row+1][col-1] != true) { 
                   next = node[row + 1][col - 1];
                   
                   if ( !c.adj.contains(next.v) ) {
                       //already added, means searched by c
                       c.adj.add(next.v);
                       if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                       return next;
                   }
               }
            }
            //up top
            {
                if (this.mark[row+1][col] != true) { 
                    next = node[row + 1][col];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row+1][col+1] != true) { 
                    next = node[row + 1][col + 1];
                    
                    if ( !c.adj.contains(next.v) ) {
                        //already added, means searched by c
                        c.adj.add(next.v);
                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                        return next;
                    }
                }
            }
            
        }
        
        return next;
    }
    
    private boolean inWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        return this.mark[row][col];
    }
    
    private void setInWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        this.mark[row][col] = true;;
    }
    
    private void notInWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        this.mark[row][col] = false;;
    }
    
    
    private int dfsDict(char letter) {

        
        //  >=0, valid value, returned by word ending node
        // -1, valid value, node exist but word not ending yet
        // -2, invalid value, no such node !
        int result = -2;
//        Node match = this.dict.get(letter);
        
        // found node initialized at root, so won't be null from scratch
        result = this.dict.getInt(this.dict.found, letter);
        
        if ( result == -1) {
            if (test) StdOut.println("dfsDict:: " + letter + " find node, word not ending, search from this node! ");

        }
        else if ( result == -2) {
            if (test) StdOut.println("dfsDict:: " + letter + " do not exist, stop search!");

        }
        else if ( result >= 0){ 
            if (test) StdOut.println("dfsDict:: " + letter + " find node, ! ");
            // this.dict.found.val;  if word not ends at node found, then val still -1
        }
        else {
            throw new java.lang.IndexOutOfBoundsException();
        }
        
        return result;
    }
    
    private void checkWord(character c, character previous) {
        
        Stack<character> s = new Stack<character>();
        
        if( c != null ) {

            if (test) StdOut.println("start to check for: " + c.letter);
            
            // step back to continue from previous node, if 
            // back.a) this node path is not in dict:
            int result = dfsDict( c.letter );
            if ( result == -1 ) {
                if (test) StdOut.println( c.letter + " is not in dict! step back from: " + previous.letter);
                
                notInWord(c);
                
                // garantee c wont be chose by previous again !
                if ( previous == null) return;

                character next = dfsBoardnext( previous );

                checkWord( next, previous );
            }
            else { // if ( dfsDict( c ) == true ) this node path is in dict,
                if (test) StdOut.println( c.letter + " is in dict: " );
                
                // back.b)  but no child node anymore in dict, i.e. this word stops here
                if ( this.dict.found.next == null ) { 
                    
                    if (test) StdOut.println( c.letter + " has no child in dict: " );
                    
                    // if in trie a node path stops with no child, then its return value should be valid !
//                    if ( dfsDict( c.letter ) == null ) throw new java.lang.NullPointerException();
                    
                    {// save word
                        if (test) StdOut.println( " find word in dict: " + this.dict.found.word);
                        
//                        int value = dfsDict( c.letter );
                        String word = this.dict.found.word;
                        wordsQueue.enqueue(word);
                        
                        // after save this word path, release current character
                        notInWord(c);
                        
                        if ( previous == null) return;
                        // step back
                        character next = dfsBoardnext( previous );

                        checkWord( next, previous );
                        
                    }
                }
                else { // continue with current node, if has child node, c->next != Null 
                    if (test) StdOut.println( c.letter + " has child in dict, continue: " );
//                    s.push(c);
                    if ( previous == null) return;
                    s.push(previous);
                    
                    setInWord(previous);
                    setInWord(c);
                    
                    // mark current c as been checking with dict, i.e. cannot be used twice
//                    setInWord(c);
                    
                    // forward.a) if return value != -2, get word from this node
                    if ( result >= 0 ) {
                        if (test) StdOut.println( " find word in dict: " + this.dict.found.word + " word ends with " + c.letter);
                        
                        // save word
//                        int value = dfsDict( c.letter );
                        String word = this.dict.found.word;
                        wordsQueue.enqueue(word);
                        
                        // continue from current
                        character next = dfsBoardnext( c );
                        
                        checkWord( next, c );
                        
                    }
                    // forward.b) if return value == null, word does not stop here, continue search in board neighbors to match word
                    else {
                        if (test) StdOut.println( " word not finished in dict, continue: " );
                     // continue from current
                        character next = dfsBoardnext( c );
                        
                        checkWord( next, c );
                        
                    }
                }    
            }
        }
        // c== null, all neighbors from previous character have been searched, step back to previous node last
        else {
            if (test) StdOut.println("checkWord find null character! ");
            if ( previous == null) return;
            
            //previous cannot find anymore neighbor, this path ends here, release previous, recursive from last
            notInWord(previous);
            
            // clean neighbors, in case may back to this node again
            previous.adj = null;
            
            character last = s.pop();
            if (test) StdOut.println( previous.letter + " has no more neighbors on board, continue: " + last.letter);
            //need stack to save recursive nodes
            
            if ( last == null) return;
            character next = dfsBoardnext( last );
            
            checkWord( next, last );
        }
    }
    
    private int map(int row, int col) {
        return row * N + col ;
    }
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        
        if (test) System.out.println(board.toString());
        this.board = board;
        M = board.rows();
        N = board.cols();
        word = new char[M * N];;
        mark = new boolean [M][N];
        
        node = new character[M][N];
        
        // start from every i, j for a loop
        for ( int i = 0; i < M; i++) {
            for ( int j = 0; j < N; j++) {
                node[i][j] = new character(this.board.getLetter(i, j), i, j);
                if (test) System.out.println(this.board.getLetter(i, j));
            }
        }    
        
        // start from every i, j for a loop
        for ( int i = 0; i < M; i++) {
            for ( int j = 0; j < N; j++) {
//                this.stringdfs(i, j);
//                character start = new character(this.board.getLetter(i,j), i, j);
//                int start = map(i , j);
//                char letter = this.board.getLetter(i,j);
                count = 0;
                
                character startLetter = node[i][j];
                if (test) StdOut.println("start: " + startLetter.letter);
                checkWord(startLetter, null);
            }
        }
        return wordsQueue;
    }

    
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {

        if (this.dict.contains(word)) {
            int pointValue;
            int length = word.length();
            if      (length < 5)  pointValue = 1;
            else if (length == 5) pointValue = 2;
            else if (length == 6) pointValue = 3;
            else if (length == 7) pointValue = 5;
            else                  pointValue = 11;
            return pointValue;
        }
        else return 0;

    }
    
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();

        BoggleSolver solver = new BoggleSolver(dictionary);
        
        BoggleBoard board = new BoggleBoard(args[1]);

        {
            int score = 0;
            for (String word : solver.getAllValidWords(board))
            {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);
        }
    }
    
}