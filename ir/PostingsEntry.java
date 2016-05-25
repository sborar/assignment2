/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;
import java.util.*;
import java.lang.*;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    public int docID;
    public double score;
    public LinkedList<Integer> offsetList = new LinkedList<Integer>();

    PostingsEntry(int docID1, int score1, int offset){
        docID=docID1;
        score=score1;
        offsetList.add(offset);
    }
    /**
     *  PostingsEntries are compared by their score (only relevant 
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    public int compareTo( PostingsEntry other ) {
        return Double.compare( score, other.score );
        }
    public static Comparator<PostingsEntry> PostingsEntryComparator=new Comparator<PostingsEntry>(){
        public int compare(PostingsEntry x, PostingsEntry y){
            return y.compareTo(x);
        }
    }; 
    public double tf(){
         
        return offsetList.size();
    }
    //
    //  YOUR CODE HERE
    //

}

    
