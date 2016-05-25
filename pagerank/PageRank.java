/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2012
 */  

import java.util.*;
import java.io.*;

public class PageRank{
    /**  
     *   Maximal number of documents. We're assuming here that we
     *   don't have more docs than we can keep in main memory.
     */
    final static int MAX_NUMBER_OF_DOCS = 2000000;

    /**
     *   Mapping from document names to document numbers.
     */
    Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();

    /**
     *   Mapping from document numbers to document names
     */
    String[] docName = new String[MAX_NUMBER_OF_DOCS];

    /**  
     *   A memory-efficient representation of the transition matrix.
     *   The outlinks are represented as a Hashtable, whose keys are 
     *   the numbers of the documents linked from.<p>
     *
     *   The value corresponding to key i is a Hashtable whose keys are 
     *   all the numbers of documents j that i links to.<p>
     *
     *   If there are no outlinks from i, then the value corresponding 
     *   key i is null.
     */
    Hashtable<Integer,Hashtable<Integer,Boolean>> link = new Hashtable<Integer,Hashtable<Integer,Boolean>>();

    /**
     *   The number of outlinks from each node.
     */
    int[] out = new int[MAX_NUMBER_OF_DOCS];

    /**
     *   The number of documents with no outlinks.
     */
    int numberOfSinks = 0;

    /**
     *   The probability that the surfer will be bored, stop
     *   following links, and take a random jump somewhere.
     */
    final static double BORED = 0.15;

    /**
     *   Convergence criterion: Transition probabilities do not 
     *   change more that EPSILON from one iteration to another.
     */
    final static double EPSILON = 0.0001;

    /**
     *   Never do more than this number of iterations regardless
     *   of whether the transistion probabilities converge or not.
     */
    final static int MAX_NUMBER_OF_ITERATIONS = 1000;
    // declare probability matrix
    
    
    /* --------------------------------------------- */


    public PageRank( String filename ) {
	int noOfDocs = readDocs( filename );

	computePagerank( noOfDocs );
    }


    /* --------------------------------------------- */


    /**
     *   Reads the documents and creates the docs table. When this method 
     *   finishes executing then the @code{out} vector of outlinks is 
     *   initialised for each doc, and the @code{p} matrix is filled with
     *   zeroes (that indicate direct links) and NO_LINK (if there is no
     *   direct link. <p>
     *
     *   @return the number of documents read.
     */
    int fileIndex = 0;
    int readDocs( String filename ) {
	try {
	    System.err.print( "Reading file... " );
	    BufferedReader in = new BufferedReader( new FileReader( filename ));
	    String line;
	    while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) {
		int index = line.indexOf( ";" );
		String title = line.substring( 0, index );
		Integer fromdoc = docNumber.get( title );
		//  Have we seen this document before?
		if ( fromdoc == null ) {	
		    // This is a previously unseen doc, so add it to the table.
		    fromdoc = fileIndex++;
		    docNumber.put( title, fromdoc );
		    docName[fromdoc] = title;
		}
		// Check all outlinks.
		StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
		while ( tok.hasMoreTokens() && fileIndex<MAX_NUMBER_OF_DOCS ) {
		    String otherTitle = tok.nextToken();
		    Integer otherDoc = docNumber.get( otherTitle );
		    if ( otherDoc == null ) {
			// This is a previousy unseen doc, so add it to the table.
			otherDoc = fileIndex++;
			docNumber.put( otherTitle, otherDoc );
			docName[otherDoc] = otherTitle;
		    }
		    // Set the probability to 0 for now, to indicate that there is
		    // a link from fromdoc to otherDoc.
		    if ( link.get(fromdoc) == null ) {
			link.put(fromdoc, new Hashtable<Integer,Boolean>());
		    }
		    if ( link.get(fromdoc).get(otherDoc) == null ) {
			link.get(fromdoc).put( otherDoc, true );
			out[fromdoc]++;
		    }
		}
	    }
	    if ( fileIndex >= MAX_NUMBER_OF_DOCS ) {
		System.err.print( "stopped reading since documents table is full. " );
	    }
	    else {
		System.err.print( "done. " );
	    }
	    // Compute the number of sinks.
	    for ( int i=0; i<fileIndex; i++ ) {
		if ( out[i] == 0 )
		    numberOfSinks++;
	    }
	}
	catch ( FileNotFoundException e ) {
	    System.err.println( "File " + filename + " not found!" );
	}
	catch ( IOException e ) {
	    System.err.println( "Error reading file " + filename );
	}
	System.err.println( "Read " + fileIndex + " number of documents" );
	return fileIndex;
    }


    /* --------------------------------------------- */
    double dist(double[] x,double[] y, int numberOfDocs){
        double dist=0.0;
        for(int i = 0; i < numberOfDocs; i++){
            dist = dist + Math.abs(x[i] - y[i]);
        }
        return dist;
    }
    
    public double manhattanDistance(double[] x1,double[] x2,int numOfDocs){
        double res = 0.0;
        for(int i=0;i<numOfDocs;i++){
            res += Math.abs(x1[i]-x2[i]);
        }
        System.out.println("inside manhattanDiatance: "+res);
        return res;
    }
    
    /*
     *   Computes the pagerank of each document.
     */
    void computePagerank( int numberOfDocs ) {
        /* Calculate the page rank of pages with d=0.15 and initial values 0
         update the values and check with epsilon blah blah
         if its less than epsilon then, its the page rank
         so in the transposition matrix we put the computed values
         */
        //calculate probability matrix
        
        double[][] g = new double[numberOfDocs][numberOfDocs];
        Boolean check=true;
        double[] x = new double[numberOfDocs];
        double[] x1 = new double[numberOfDocs];
        double dist;
        for(int i = 0; i < numberOfDocs; i++){//to calculate g
            for(int j = 0; j < numberOfDocs; j++){
                
                if(out[i] == 0){
                    g[i][j] = 1/(double)numberOfDocs;//jump no out links
                }
                else{
                    if(link.get(i).containsKey(j) && (link.get(i)).get(j)){//link have
                        g[i][j] = ((1-BORED)*1/(double)out[i])+((BORED)*1/(double)numberOfDocs);
                        //System.out.println("link have");
                    }
                    else {
                          g[i][j] = (BORED)*1/(double)numberOfDocs;//bored jump, cause otherwise cant go from i to j, no link
                        //System.out.println("link no");
                    }
                }
            }
            x[i]=0.0;
        }
        x[0]=1.0;
        int counter=0;
        Boolean converged = false;
        while(counter < MAX_NUMBER_OF_ITERATIONS){
            System.out.println("iteration="+counter);
            for(int i = 0; i < numberOfDocs; i++){
                x1[i] = 0.0;
                for(int j = 0; j < numberOfDocs; j++){
                    x1[i] = x1[i] + (x[j] * g[j][i]);
                }
               // System.out.println(x1[i]+" ");
            }
            double res=0.0;
            for(int i = 0; i < numberOfDocs; i++){
                 res=res+Math.abs(x[i]-x1[i]);
            }
            System.out.println("res="+res);
            if(res < EPSILON){
                converged = true;
            }
            if(converged){
                break;
            }
            for(int i = 0; i < numberOfDocs; i++){
                x[i] = x1[i];
            }
            counter++;
        }
        final double[] ranked = new double[numberOfDocs];
        Integer[] indices = new Integer[numberOfDocs];
        for (int i = 0; i < numberOfDocs; i++) {
            ranked[i] = x[i];
            indices[i] = i;
        }
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(ranked[o1], ranked[o2]);
            }
        });
        for (int i = 0; i < 50; i++) {
            int idx = indices[indices.length - i - 1];
            System.out.println((i + 1) + ": " + docName[idx] + " "
                               + ranked[idx]);
        }
    }
    
    
    /* --------------------------------------------- */


    public static void main( String[] args ) {
	if ( args.length != 1 ) {
	    System.err.println( "Please give the name of the link file" );
	}
	else {
	    new PageRank( args[0] );
	}
    }
}
