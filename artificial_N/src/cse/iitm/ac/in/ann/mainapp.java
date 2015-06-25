//
//	Nen - Neural Network Implementation in Java
//	Copyright (C) 2012  Pascal Lehwark
//
//	This library is free software; you can redistribute it and/or
//	modify it under the terms of the GNU Lesser General Public
//	License as published by the Free Software Foundation; either
// 	version 2.1 of the License, or (at your option) any later version.
//
//	This library is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//	Lesser General Public License for more details.
//
//	You should have received a copy of the GNU Lesser General Public
//	License along with this library; if not, write to the Free Software
//	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

package cse.iitm.ac.in.ann;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

import cse.iitm.ac.in.ann.NeuralNetwork.Type;


public class mainapp {
	public static final String txt_usage_train=	"***   Training    ***\njava -jar nen.jar train [options] train_file model_file\n"+
											" Options:\n"+
											" -t model_type : set type of model (default 1)\n"+
											" 	1 -- Regression\n"+
											"	2 -- Classification\n"+
											" -h hidden : set number of hidden units\n" +
											"            (default 4 for Regression, #classes for Classification)\n" +
											" -d depth :  set training-depth (default 1)\n" +
											"	0 -- Fast and Poor\n" +
											"	1 -- Normal Speed and good performance\n" +
											"	2 -- Slower Training but possibly better performance\n\n";
	public static final String txt_usage_predict="***   Prediction    ***\njava -jar nen.jar predict test_file model_file output_file\n\n";
	public static final String txt_usage_scale="***   Scaling    ***\njava -jar nen.jar scale [options] input_file output_file\n"+
											" Options:\n"+
											" -t type : set scaling-type (default 1)\n"+
											" 	1 -- Scale both X and Y (for Regression problems)\n"+
											" 	2 -- Scale only X (for Classification problems)\n\n";
	public static final String txt_usage_split="***   Splitting    ***\njava -jar nen.jar split [options] input_file train_outputfile test_outputfile\n"+
											" Options:\n"+
											" -t data_type : set type of data (default 1)\n"+
											" 	1 -- Regression\n"+
											" 	2 -- Classification\n"+
											" -p train_percentage : set trainsize in % (default 80)\n\n";
	
	public static final String txt_usage_xval="***   XVal   ***\njava -jar nen.jar xval [options] train_file"+
											" Options:\n"+
											" -t model_type : set type of model (default 1)\n"+
											" 	1 -- Regression\n"+
											" 	2 -- Classification\n"+
											" -k folds : set #partitions (default 3)\n"+
											" -h hidden : csv-separated list of #hidden-nodes (default 1,2,4,8,16,32)\n\n";

	public static void main(String[] args)
	{
		run(args);
	}
	
	public static void run(String[] args){
		if(args.length>0){
			if("train".equals(args[0].trim()))
				train(args);
			else if("predict".equals(args[0].trim()))
				predict(args);
			else if("scale".equals(args[0].trim()))
				scale(args);
			else if("split".equals(args[0].trim()))
				split(args);
			else if("xval".equals(args[0].trim()))
				xval(args);
			else
				exit();
		}else
			exit();
	}
	
	public static void xval(String[] args){
		try{
			Type t=Type.Regression;
			int k=3;
			String hids="1,2,4,8,16,32";
			boolean quiet=false;
			for(int n=1;n<args.length;n++)
				if("-t".equals(args[n]))
					t="1".equals(args[n+1])?Type.Regression:Type.Classification;
				else if("-q".equals(args[n]))
					quiet=true;
				else if("-h".equals(args[n]))
					hids=args[n+1].trim();
				else if("-k".equals(args[n]))
					k=Integer.parseInt(args[n+1]);
			String tmp=args[args.length-1];
			if(tmp.startsWith("-"))
				exit(txt_usage_xval);
			File trainfile=new File(tmp);
			String msg="Trainfile:"+trainfile.getAbsolutePath();
			log(msg);
			Data data=Data.readLibSVM(t,trainfile);
			String[] tmph=hids.split(",");
			int[] hidden=new int[tmph.length];
			for(int n=0;n<tmph.length;n++)
				hidden[n]=Integer.parseInt(tmph[n]);
			int h=NeuralNetwork.xval(data, hidden, k, data.getX().length, msg, new File("xval.log"));
			log("Based on this Cross-Validation, the optimal number of hidden units is "+h+".");
		}catch(Exception e){
			exit(txt_usage_xval);
		}
	}

	public static void train(String[] args){
		try{
			Type t=Type.Regression;
			int h=-1;
			int d=1;
			boolean quiet=false;
			for(int n=1;n<args.length;n++)
				if("-t".equals(args[n]))
					t="1".equals(args[n+1])?Type.Regression:Type.Classification;
				else if("-q".equals(args[n]))
					quiet=true;
				else if("-h".equals(args[n]))
					h=Integer.parseInt(args[n+1]);
				else if("-d".equals(args[n]))
					d=Integer.parseInt(args[n+1]);
			String tmp=args[args.length-2];
			if(tmp.startsWith("-"))
				exit(txt_usage_train);
			File trainfile=new File(tmp);
			tmp=args[args.length-1];
			if(tmp.startsWith("-"))
				exit(txt_usage_train);
			File modelfile=new File(tmp);
				Data data=Data.readLibSVM(t,trainfile);
			if(h==-1)
				h=t==Type.Regression?4:data.getY()[0].length;
			String msg="Trainfile: "+trainfile.getAbsolutePath()+", Modelfile: "+modelfile.getAbsolutePath()+", Training-Depth: "+d;
			NeuralNetwork nen=new NeuralNetwork(t,data.getX()[0].length,h,data.getY()[0].length);
			nen.train(data, quiet,msg,d);
			NeuralNetwork.encode(nen, new BufferedOutputStream(new FileOutputStream(modelfile)));
			log("Training Fisnihed");
		}catch(Exception e){
			exit(txt_usage_train);
		}
	}

	public static void predict(String[] args){
		try{
			String tmp=args[args.length-3];
			if(tmp.startsWith("-"))
				exit(txt_usage_predict);
			File inputfile=new File(tmp);
			if(!inputfile.exists())
				exit(inputfile.getAbsolutePath()+" DOES NOT EXIST");
			tmp=args[args.length-2];
			if(tmp.startsWith("-"))
				exit(txt_usage_predict);
			File modelfile=new File(tmp);
			if(!modelfile.exists())
				exit(modelfile.getAbsolutePath()+" DOES NOT EXIST");
			tmp=args[args.length-1];
			if(tmp.startsWith("-"))
				exit(txt_usage_predict);
			File outputfile=new File(tmp);
			String msg="Nen Prediction:\n\tInputfile:\t"+inputfile.getAbsolutePath()+
					"\n\tModelfile:\t"+modelfile.getAbsolutePath()+"\n\tOutputfile:\t"+outputfile.getAbsolutePath();
			log(msg);
			NeuralNetwork nen=NeuralNetwork.decode(new BufferedInputStream(new FileInputStream(modelfile)));
			NeuralNetwork.predictLibSVMFormat(nen, inputfile, outputfile);
		}catch(Exception e){
			exit(txt_usage_predict);
		}
	}
	
	public static void scale(String[] args){
		try{
			Type t=Type.Regression;
			for(int n=1;n<args.length;n++)
				if("-t".equals(args[n]))
					t="1".equals(args[n+1])?Type.Regression:Type.Classification;			
			String tmp=args[args.length-2];
			if(tmp.startsWith("-"))
				exit(txt_usage_scale);
			File inputfile=new File(tmp);
			if(!inputfile.exists())
				exit(inputfile.getAbsolutePath()+" DOES NOT EXIST");
			tmp=args[args.length-1];
			if(tmp.startsWith("-"))
				exit(txt_usage_scale);
			File outputfile=new File(tmp);
			String msg="Nen Scaling:\n\tType:    \t"+t+"\n\tInputfile:\t"+
				inputfile.getAbsolutePath()+"\n\tOutputfile:\t"+outputfile.getAbsolutePath();
			log(msg);
			Data data=Data.readLibSVM(t,inputfile);
			Data.scaleColumns(data.getX(), -1, 1);
			if(t==Type.Regression)
				Data.scaleColumns(data.getY(), -1, 1);
			Data.writeLibSVM(data, new FileWriter(outputfile));
			log("Done");
		}catch(Exception e){
			exit(txt_usage_scale);
		}
	}
	
	public static void split(String[] args){
		try{
			Type t=Type.Regression;
			int p=80;
			for(int n=1;n<args.length;n++)
				if("-t".equals(args[n]))
					t="1".equals(args[n+1])?Type.Regression:Type.Classification;
				else if("-p".equals(args[n]))
					p=Integer.parseInt(args[n+1]);
			String tmp=args[args.length-3];
			if(tmp.startsWith("-"))
				exit(txt_usage_split);
			File inputfile=new File(tmp);
			if(!inputfile.exists())
				exit(inputfile.getAbsolutePath()+" DOES NOT EXIST");
			tmp=args[args.length-2];
			if(tmp.startsWith("-"))
				exit(txt_usage_split);
			File trainoutputfile=new File(tmp);
			tmp=args[args.length-1];
			if(tmp.startsWith("-"))
				exit(txt_usage_split);
			File testoutputfile=new File(tmp);
			String msg="Nen Splitting:\n\tType:     \t"+t+"\n\tInputfile:\t"+inputfile.getAbsolutePath()+
						"\n\tTrainsize:\t"+p+"%"+
						"\n\tTrain-Output:\t"+trainoutputfile.getAbsolutePath()+
						"\n\tTest-Output:\t"+testoutputfile.getAbsolutePath();
			log(msg);
			Data[] data=Data.split(Data.readLibSVM(t,inputfile), p);
			log("Trainpartition: "+data[0]+", Testpartition: "+data[1]);
			log("Writing Train- and Test-File");
			Data.writeLibSVM(data[0], new FileWriter(trainoutputfile));
			Data.writeLibSVM(data[1], new FileWriter(testoutputfile));
			log("Done");
		}catch(Exception e){
			exit(txt_usage_split);
		}
	}
	
	public static void log(String msg){
		System.out.println(msg);
	}
	
	public static void exit(){
		exit("Usage for Nen:\n---------------\n"+txt_usage_train+txt_usage_predict+txt_usage_scale+txt_usage_split+txt_usage_xval);
	}
	
	public static void exit(String msg){
		log(msg);
		System.exit(0);
	}
}
