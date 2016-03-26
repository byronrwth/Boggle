import java.lang.*;
import java.security.acl.LastOwnerException;
import java.util.*;


public class BoggleSolver
{
	private static int R = 26;
    private boolean test = false;
    private boolean trietest = false;
    
    private static boolean[][]  mark;
    
    //my own TrieR26 class
    private TrieR26ST dict;
    private BoggleBoard board;
    private static char[] word;
    private static int M;
    private static int N;
    private static character[][] node;

    
    static private Queue<String> wordsQueue;
//    static private Stack<character> eachsearchStack;
    static private Queue<character> neighborQueue;

    private class Node {
        private Integer val = null; 
        private Node[] next = new Node[R];
        private String word = null; //save whole string key as word at end node 
    }
    
    /*======inner class Trie26 =====================================================================*/
    private class TrieR26ST extends TrieST<Integer>{
//        private final int R = 26;        // for english charachters search

        private Node root = new Node();
        public Node found = root;  // this node indicates where current search reaches
        
        
        public boolean contains(String key) {
            return newget(key);
        }
        public boolean newget(String key) {
            Node x = get(this.root, key, 0);
            if (x == null) return false;
            return (x.val >= 0 );
        }
        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c-65], key, d+1);
        }
        

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
        			if(trietest) StdOut.println("put: root should already created ! "); 
        			throw new java.lang.NullPointerException();
        		}
        		else {
					if(trietest) StdOut.println("put: create a new node at depth: " + d + " for " + key.charAt(d-1));
					x = new Node();
                    x.val = -1;
                    x.word = "*";
        		}
        	}
            
            //after creating node x, changing its value and check whether continue for its children
            if (d == (key.length())) {
                x.val = val;
                x.word = key;
               return  x;
            }
            else { 
                char c = key.charAt(d);
                if (x.val == null && x.word == null) {  // only for root node
                    x.val = -1;
                    x.word = "*";
                }

//                if (x.next == null) { // this is the first time to create child node under x
//                    x.next = new Node[R];
//                }
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
        	if (dictionary[i].length() > 2) {
                this.dict.put(dictionary[i], i);
        	}
       }
    }

     
    private class character {
        private char letter; 
        private int row;
        private int col;
        private int v;

        character (char letter, int row, int col) {
            this.letter = letter;
            this.row = row;
            this.col = col;
            this.v = row * N + col;
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
               }
            }
            //up top
            {
                if (this.mark[row-1][col] != true) { 
                    next = node[row - 1][col];
                    neighborQueue.enqueue(next);
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row-1][col+1] != true) { 
                    next = node[row - 1][col + 1];
                    neighborQueue.enqueue(next);
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
                }
             }
            //right
            if(col < this.N -1 ){
                if (this.mark[row][col+1] != true) { 
                    next = node[row][col + 1];
                    neighborQueue.enqueue(next);
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
               }
            }
            //up top
            {
                if (this.mark[row+1][col] != true) { 
                    next = node[row + 1][col];
                    neighborQueue.enqueue(next);
                }
            }
            // up right
            if(col < this.N -1 ){
                if (this.mark[row+1][col+1] != true) { 
                    next = node[row + 1][col + 1];
                    neighborQueue.enqueue(next);
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
    
    
    private void checkWord(character c, Node nodetocheck) {
        if( c != null ) {
            if (test) {
            	StdOut.println("checkWord: start to check for: " + c.letter);
            	//if(previous != null) StdOut.println("checkWord : previous: " + previous.letter);
            }
            
            int result = -100;
            
            if ( nodetocheck.next == null || nodetocheck.next[c.letter-65] == null ) {
            	result = -2;
            }
            else {
            	result = nodetocheck.next[c.letter-65].val;
            	if (test) StdOut.println(" checkWord: " + c.letter + " added in this word " );
            	
            	if ( result >= 0 ) {
            		if (test) StdOut.println(" checkWord: complete a word: " + nodetocheck.next[c.letter-65].word);
            		wordsQueue.enqueue( nodetocheck.next[c.letter-65].word ); 
            	}
            }
            
            if ( result == -2 ) {  // character c is not in any node of dictionary
            	return;    // finish this search , choose next letter on board to start next
            }		
            else {
                Queue<character> myneighbour = dfsBoardnext( c );
                
                if (myneighbour != null ) {
                	// c has already been used, it is invisible as avaialble dice for its neighbors
                	setInWord(c);
                	
                	//QueueToStack(myneighbout);
                	for ( character neighbor : myneighbour) {
                		if (test) StdOut.println(" checkWord: " + c.letter +  " has neighboring not searched: " + neighbor.letter );
                		
                		if ( c.letter != 'Q' ) {
                			checkWord( neighbor, nodetocheck.next[c.letter-65]) ;
                		}
                		else {
                			if ( nodetocheck.next[c.letter-65].next != null && nodetocheck.next[c.letter-65].next['U' - 65] != null) {
                				if (test) StdOut.println(" checkWord: for Qu directly jump to sub node U for neighbor search "    );
                				checkWord( neighbor, nodetocheck.next[c.letter-65].next['U' - 65]) ;
                			}

                		}
                	}
                	//  make sure c is available for above level dices !
                	removefromWord(c);
                }
                else {
                	return;    // finish this search , choose next letter on board to start next
                }
            }
        }
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
        
        wordsQueue = new Queue<>();
        
        // start from every i, j for a loop
        for ( int i = 0; i < M; i++) {
            for ( int j = 0; j < N; j++) {
                character startLetter = node[i][j];
                
                // every start from a letter on board is a new start
                //eachsearchStack = new Stack<character>();
                //eachsearchStack.push(startLetter);
                // scan the whole board and store found words into the wordsQueue
                if (test) StdOut.println(" getAllValidWords: start a new word search from dict root, startin with: " + startLetter.letter);
                checkWord(startLetter, this.dict.root);
            }
        }
        
        HashSet<String> uniqueMap = new HashSet<String>() ;
        for (String word : wordsQueue) {
        	uniqueMap.add(word) ;
        }
        if (test) StdOut.println(" getAllValidWords: finish all checkWord steps, now find words no. of " + wordsQueue.size() + " unqiue: " + uniqueMap.size());
        
        return uniqueMap;
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