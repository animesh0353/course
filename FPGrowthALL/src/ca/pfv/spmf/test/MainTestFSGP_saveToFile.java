package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan.AlgoFSGP;
import ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase;


/**
 * Example of how to use the FSGP algorithm in source code.
 * @author Philippe Fournier-Viger
 */
public class MainTestFSGP_saveToFile {

	public static void main(String [] arg) throws IOException{  
//		String outputPath = "animesh.txt";
		String outputPath = "/home/animesh/Dropbox/workspace/FPGrowthALL/bin/ca/pfv/spmf/test/output.txt";
		// Load a sequence database
		SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
		String string = "/home/animesh/Dropbox/workspace/FPGrowthALL/bin/ca/pfv/spmf/test/retail.txt";
//		String database = fileToPath("/home/animesh/Dropbox/workspace/Apriori/retail.dat");
		sequenceDatabase.loadFile(string);
//		sequenceDatabase.loadFile(fileToPath("retail.txt"));
//		sequenceDatabase.loadFile(fileToPath("contextPrefixSpan.txt"));
		// print the database to console
		sequenceDatabase.print();
		
		// Create an instance of the algorithm with minsup = 50 %
		AlgoFSGP algo = new AlgoFSGP(); 
		
		int minsup = 2; // we use a minimum support of 2 sequences.
		
		// execute the algorithm
		boolean performPruning = true;// to activate pruning of search space
		algo.runAlgorithm(sequenceDatabase, outputPath, minsup, performPruning);    
		algo.printStatistics(sequenceDatabase.size());
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFSGP_saveToFile.class.getResource(filename);
		System.out.println(java.net.URLDecoder.decode(url.getPath(),"UTF-8"));
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}