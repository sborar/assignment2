/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellstrom, 2012-14
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.*;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {
    public int N=17482;
    
    /** The index as a hashtable. */
    public HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    /**
     *  Inserts this token in the index.
     */
    public void insert( String token, int docID, int offset ) {
          /*  check if the word exists in index
            if it exists
                if docID exists
                    increment score
                else
                    create a postings entry with docID and Offset
                    Add to the list
            else
                create a postings list
                create a postings entry with docID and Offset
                Add to the list

        */
        if(index.containsKey(token)) {
            PostingsList postingsListForToken = index.get(token);
            if(postingsListForToken.list.getLast().docID==docID) {
                postingsListForToken.list.getLast().score++;
                postingsListForToken.list.getLast().offsetList.add(offset);
            }
            else {
                PostingsEntry newPostingsEntry = new PostingsEntry(docID, 1, offset);
                postingsListForToken.list.add(newPostingsEntry);
            }
        }
        else {
            PostingsList newPostingsList = new PostingsList();
            PostingsEntry newPostingsEntry = new PostingsEntry(docID, 1, offset);
            newPostingsList.list.add(newPostingsEntry);
            index.put(token, newPostingsList);
        }
    }


    /**
     *  Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
    // 
    //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
    //
    return null;
    }


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
    // 
    //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
    //
        if(index.containsKey(token)) {
            return index.get(token);
        }
        else {
            return null;
        }
    }
    public PostingsList intersection(PostingsList ret, PostingsList tokenLists){
        boolean docIDPresent = false;
        PostingsList result = new PostingsList();
        System.out.println(ret.size()+" "+tokenLists.size());
        for(int i=0;i<ret.size();i++) {
            docIDPresent = false;
            for(int j=0; j<tokenLists.size(); j++) {
                if(ret.list.get(i).docID==tokenLists.list.get(j).docID) {
                    docIDPresent = true;
                    break;
                }
            }
            if(docIDPresent){
                result.list.add(ret.list.get(i));
            }
        }
        return result;
    }
    /**
     *  calculates tfidf of the term with document
     */
    public double tfidf(String token, int docID){
        PostingsList x=getPostings(token);
        PostingsEntry y=x.getEntry(docID);
        if(y==null){
            return 0.0;
        }
        //System.out.println(docLengths.get(docID+""));
        String doc=docID+"";
        double tf= ((double)(1+Math.log(y.offsetList.size())));
        double idf=Math.log10(N/x.list.size());
        return tf*idf;
    }
    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
    // 
    //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
    // 
        if(queryType==INTERSECTION_QUERY) {
            System.out.println("INTERSECTION_QUERY");
            if(query.size()==1) {
                return getPostings(query.terms.getFirst());
            } 
            else if (query.size()>1) {
                PostingsList ret = getPostings(query.terms.getFirst());
                for(int i = 1; i<query.size(); i++){
                    PostingsList PostingsListAtI = getPostings(query.terms.get(i));
                    if(PostingsListAtI==null) {
                        return null;
                    }
                    ret=intersection(ret,PostingsListAtI);
                }
                return ret;
            } 
            else {
                return null;
            }
        } 
        else if(queryType==PHRASE_QUERY){
            System.out.println("PHRASE_QUERY");
            if(query.size()==1) {
                return getPostings(query.terms.getFirst());
            } 
            else if (query.size()>1) {
                PostingsList ret = new PostingsList();
                PostingsList firstTermsPostingsList = getPostings(query.terms.getFirst());
                for(int i=0;i<firstTermsPostingsList.list.size();i++){
                    boolean phraseFound = false;
                    int _docID = firstTermsPostingsList.list.get(i).docID;
                    for(int j=1;j<query.size();j++){
                        boolean offsetConsecutive = false;
                        PostingsEntry sameDocIDPostingsEntry = getPostings(query.terms.get(j)).getEntry(_docID);
                        if(sameDocIDPostingsEntry == null){
                            break;
                        }
                        for(int k=0; k<firstTermsPostingsList.list.get(i).offsetList.size();k++){
                            for(int l=0;l<sameDocIDPostingsEntry.offsetList.size();l++){
                                if(sameDocIDPostingsEntry.offsetList.get(l)==(firstTermsPostingsList.list.get(i).offsetList.get(k)+j)){
                                    //check with next j, either break here and twice offsetConsecutive
                                    System.out.println(_docID);
                                    offsetConsecutive = true;
                                    if(j==(query.size()-1)){
                                        phraseFound = true;
                                    }
                                    break;
                                }
                            }
                            if(offsetConsecutive){
                                break;
                            }
                        }
                    }
                    if(phraseFound){
                        ret.list.add(firstTermsPostingsList.list.get(i));
                    }
                }
                return ret;
            }
            else{
                return null;
            }
        }
        if(queryType==RANKED_QUERY){
            /*
            find out the tf-idf score of each of the term-document pair and check the cosine of that vector with the query
            */
            if(query.size() == 1) {
                String token = query.terms.getFirst();
                 PostingsList termPostingsList = getPostings(token);
                 for(int i = 0 ; i < termPostingsList.list.size() ; i++){//docids of the postingslist
                    String docID = termPostingsList.list.get(i).docID+"";
                    double tf = termPostingsList.list.get(i).tf()/docLengths.get(docID);
                    termPostingsList.list.get(i).score=tf;
                 }
                 Collections.sort(termPostingsList.list,PostingsEntry.PostingsEntryComparator);
                 return termPostingsList;
            } 
            else{
                HashMap<String,Double> queryTf = new HashMap<String, Double>();//store the tfidf of unique query words in hashmap
                for(int i = 0 ; i < query.terms.size() ; i++){//go through all query terms
                    //tf-idf of the query
                    String token = query.terms.get(i);
                    if(queryTf.containsKey(token)){//if exists increment
                        queryTf.put(token,queryTf.get(token) + 1.0);
                    }
                   else{//add in hashmap
                        queryTf.put(token, 1.0);
                   }
                }
                Set<String> keys = queryTf.keySet();
                String[] uniqueQueryTermList = keys.toArray(new String[keys.size()]);
                for(int i = 0;i < uniqueQueryTermList.length;i++){
                    String token = query.terms.get(i);
                    PostingsList termPostingsList = getPostings(token);//get the postings lsit of taht token
                   double idf = Math.log10(N / termPostingsList.list.size());
                   queryTf.put(token,queryTf.get(token)*idf);//put tf*idf in querytf
                }
                /*
                so we need to check similarity for all documents containing any of the three words so we nned to loop through the postings lists of all unique words in the query
                */
                //similarity hashmap of docid of the query terms
                //need to calculate the tfs of all the query terms in one docid
                PostingsList result = new PostingsList();
                HashMap<Integer,Double> similarity = new HashMap<Integer,Double>();
                for(int i = 0 ; i < uniqueQueryTermList.length ; i++){//going through all unique query words
                    String token = uniqueQueryTermList[i];
                    PostingsList termPostingsList = getPostings(token);// double dotProduct=queryTf.get(token);
                    for(int j = 0 ; j < termPostingsList.list.size() ; j++){//for each docid we compute the tf idf score
                        int docId = termPostingsList.list.get(j).docID;
                        if(similarity.containsKey(docId)){
                            continue;
                        }
                        else{
                            double tfIdfDocTerm, tfIdfQueryTerm, numerator=0;
                            for(int k = 0 ; k < uniqueQueryTermList.length ; k++){//to calculate the cosine similarity we need the tfidf of a word in document and in query
                                String ithTerm = uniqueQueryTermList[k];
                                tfIdfQueryTerm = queryTf.get(ithTerm);
                                tfIdfDocTerm = tfidf(ithTerm,docId);
                                numerator = numerator + tfIdfDocTerm*tfIdfQueryTerm;
                            }
                            double cosine = numerator/(docLengths.get(""+docId)*query.terms.size());
                            similarity.put(docId,cosine);
                            termPostingsList.list.get(j).score=cosine;
                            result.list.add(termPostingsList.list.get(j));
                        }
                   }
                }
                Collections.sort(result.list,PostingsEntry.PostingsEntryComparator);
                return result;
            }
        }
        else {
            return null;
        }
    }
    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
