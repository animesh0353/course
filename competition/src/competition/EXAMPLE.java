package competition;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;

import weka.core.converters.ConverterUtils.DataSource;


public class EXAMPLE {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		DataSource source = null;
		try {
			source = new DataSource("/home/animesh/weka-3-6-11/data/breast-cancer.arff");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instances data = null;
		try {
			data = source.getDataSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			   data.setClassIndex(data.numAttributes() - 1);		
				
				
				//System.out.println(data);
	}

}
