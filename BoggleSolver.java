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
    
    static private Queue<String> wordsQueue;
    
    static private Stack<character> eachsearchStack;
    
    static private Queue<character> neighborQueue;
    
    
//    private Bag<character>[] adj;
//    private static Bag<Edge>[] adj;
//    private static Bag<DirectedEdge>[] adj;

    
    /*======inner class Trie26 =====================================================================*/
    private class TrieR26ST extends TrieST<Integer>{
        private final int R = 26;        // for english charachters search

        private Node root = new Node();
        

        public Node found = root;  // this node indicates where current search reaches
        
        private class Node {
            private Integer val = null; 

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
                root = put(root, key, val, 0);
            }
        }
        
        private Node put(Node x, String key, Integer val, int d) {

        	if (x == null) {
        		if (d == 0) {
        			StdOut.println(" root should alreadz created ! "); 
        			throw new java.lang.NullPointerException();
        		}
        		else {
					StdOut.println(" create a new node at depth: " + d + " for " + key.charAt(d-1));
					x = new Node();
                    x.val = -1;
                    x.word = "*";
        		}
        	}
            
            //after creating node x, changing its value and check whether continue for its children
            if (d == (key.length())) {
                x.val = val;
                x.word = key;
                if (trietest) {
                	StdOut.println(" when "+key.charAt(d-1)+" is end node for key: " + key +  " x.val "+ x.val + " x.word "+ x.word);
               }
                
                return  x;
            }
            else { 
            	
                char c = key.charAt(d);
                
                if (x.val == null && x.word == null) {  // only for root node
                    x.val = -1;
                    x.word = "*";
                }
                
                if (trietest) StdOut.println("node x: is not ending node! reuse node x of ( "+" x.val: "+x.val+" x.word: "+x.word + ") go for next node: " + key.charAt(d) );


                if (x.next == null) { // this is the first time to create child node under x
                    x.next = new Node[R];
                }
                
                x.next[c-65] = put(x.next[c-65], key, val, d+1);

                
                return  x;
            }
        }
    }
    
    /*============end inner class Trie26=========================================================*/
    
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dict = new TrieR26ST();
        for (int i = 0; i < dictionary.length; i++) {
            this.dict.put(dictionary[i], i);
        }
        
        wordsQueue = null;
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

    
    private Queue<character> dfsBoardnext(character c) {
        int row = c.row;
        int col = c.col;
        
        character next = null;
        neighborQueue = new Queue<>();
        
        //up
        if (row > 0) {
            //up, left
            if(col > 0){
               if (this.mark[row-1][col-1] != true) {
                   
                   //already initialize this node
                   // check first whether it has already been in c's adj list
                   // if yes, check this node has been searched by c before

                   next = node[row - 1][col - 1];
                   neighborQueue.enqueue(next);
                   if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                   
//                   if ( !c.adj.contains(next.v) ) {
//                       //already added, means searched by c
//                       c.adj.add(next.v);
//                       if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                       return next;
//                   }
               }
            }
            //up top
            {
                if (this.mark[row-1][col] != true) { 
                    
                    next = node[row - 1][col];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row-1][col+1] != true) { 
                    
                    next = node[row - 1][col + 1];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
            }
            
        }
        // same line 
        {
            //left
            if(col > 0){
                
                if (this.mark[row][col-1] != true) { 
                    next = node[row][col - 1];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
             }
            //right
            if(col < this.N -1 ){
                if (this.mark[row][col+1] != true) { 
                    next = node[row][col + 1];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
            }
        }
        //down
        if (row < this.M -1 ) {
            // down left
            if(col > 0){
               if (this.mark[row+1][col-1] != true) { 
                   next = node[row + 1][col - 1];
                   neighborQueue.enqueue(next);
                   if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                   
                   
//                   if ( !c.adj.contains(next.v) ) {
//                       //already added, means searched by c
//                       c.adj.add(next.v);
//                       if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                       return next;
//                   }
               }
            }
            //up top
            {
                if (this.mark[row+1][col] != true) { 
                    next = node[row + 1][col];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row+1][col+1] != true) { 
                    next = node[row + 1][col + 1];
                    neighborQueue.enqueue(next);
                    if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
                    
                    
//                    if ( !c.adj.contains(next.v) ) {
//                        //already added, means searched by c
//                        c.adj.add(next.v);
//                        if (test) StdOut.println( " dfsBoardnext: find new neighbor on board, continue: " + next.letter);
//                        return next;
//                    }
                }
            }
            
        }
        
        return neighborQueue;
    }
    
    private void resetWord() {
    	for( int row = 0; row < N; row++ ) {
    		for ( int col =0; col < N; col++ ) {
    			this.mark[row][col] = false ;
    		}
    	}
    }
    
    private boolean checkinWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        return this.mark[row][col];
    }
    
    private void setInWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        this.mark[row][col] = true; 
    }
    
    private void removefromWord(character c) {
        int row = c.v / N;
        int col = c.v % N;
        
        this.mark[row][col] = false;  // for this search, c has been searched
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
    
    private character goback( ) {
    	eachsearchStack.pop();
    	while ( eachsearchStack.peek() != null && checkinWord( eachsearchStack.peek() ) == true ) {
    		character temp = eachsearchStack.pop(); //pop out already searched characters, try to find a letter yet not used in current word
    		removefromWord(temp);
    		if (test) StdOut.println("checkWord : "+ temp.letter + "  pop out  " );
    	}
    	
    	character next = eachsearchStack.peek() ; // can further search in current word
    	
    	return next;

    	
    } 
    private void checkWord(character c, character previous) {

        if( c != null ) {

            if (test) {
            	StdOut.println("checkWord: start to check for: " + c.letter);
            	if(previous != null) StdOut.println("checkWord : previous: " + previous.letter);
            }
            
            //eachsearchStack.push(c);
            
            // step back to continue from previous node, if 
            // back.a) this node path is not in dict:
            int result = dfsDict( c.letter, previous.letter );
            
            if ( result == -2 ) {  // character c is not in any node of dictionary
            	character mynext = goback();
            	
            	if ( mynext != null) {
            		if (test) StdOut.println("checkWord : continue search with  : " + mynext.letter);
            		checkWord(mynext, null);
            	}
            	else {   // stack is empty now, stop current word search, find next letter to start a new search
            		resetWord();
            		return;    // finish this search , choose next letter on board to start next
            	}
            }		
            else if ( result == -1 ) {
                Queue<character> myneighbout = dfsBoardnext( c );
                if (myneighbout != null ) {
                	QueueToStack(myneighbout);
                	setInWord(c);
                    if (test) StdOut.println("checkWord : "+ c.letter + " is in dict, no words end here, keep on search more letters with neighbors" );
                }
                else {
                    if (test) StdOut.println("checkWord : "+ c.letter + " is in dict, no words end here, but no neighbors, pop out" );

                    character mynext = goback();
                    
                	if ( mynext != null) {
                		if (test) StdOut.println("checkWord : continue search with  : " + mynext.letter);
                		checkWord(mynext, null);
                	}
                	else {   // stack is empty now, stop current word search, find next letter to start a new search
                		resetWord();
                		return;    // finish this search , choose next letter on board to start next
                	}
                }

            }
            else { // if dfsDict >= 0, at least 1 word is found ending at current letter, can further search
                if (test) StdOut.println("checkWord: " + c.letter + " ends with a word in dict: " );
                
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
//                        notInWord(c);
                        
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
                    
//                    setInWord(previous);
//                    setInWord(c);
                    
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
//        // c== null, all neighbors from previous character have been searched, step back to previous node last
//        else {
//            if (test) StdOut.println("checkWord find null character! ");
//            if ( previous == null) return;
//            
//            //previous cannot find anymore neighbor, this path ends here, release previous, recursive from last
////            notInWord(previous);
//            
//            // clean neighbors, in case may back to this node again
//            previous.adj = null;
//            
//            character last = s.pop();
//            if (test) StdOut.println( previous.letter + " has no more neighbors on board, continue: " + last.letter);
//            //need stack to save recursive nodes
//            
//            if ( last == null) return;
//            character next = dfsBoardnext( last );
//            
//            checkWord( next, last );
//        }
    }
    
    private int map(int row, int col) {
        return row * N + col ;
    }
    
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        
        if (test) System.out.println("getAllValidWords :" + board.toString());
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
            }
        }    
        
        // start from every i, j for a loop
        for ( int i = 0; i < M; i++) {
            for ( int j = 0; j < N; j++) {
                character startLetter = node[i][j];
                
                // every start from a letter on board is a new start
                eachsearchStack = new Stack<character>();
                eachsearchStack.push(startLetter);
                // scan the whole board and store found words into the wordsQueue
                checkWord(startLetter, null);
            }
        }
        
        if (test) StdOut.println(" getAllValidWords: finish all checkWord steps, now find words no. of " + wordsQueue.size());
        
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

        
		int score = 0;
		for (String word : solver.getAllValidWords(board)) {
			StdOut.println(word);
			score += solver.scoreOf(word);
		}
		StdOut.println("main(): Score = " + score);
        
    }
    
}