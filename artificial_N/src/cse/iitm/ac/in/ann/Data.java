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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

import cse.iitm.ac.in.ann.NeuralNetwork.Type;

/** A class for holding a dataset - two matrices x and y - and many static helper methods **/
public class Data {
	public static final long t_millis=1l, t_sec=1000l, t_min=t_sec*60l, t_hour=t_min*60l, t_day=t_hour*24l, t_week=t_day*7l,t_year=t_day*365l;

	private Type type;	// regression or classification 
	private float[][] x,y;	
	private String[] xattr,yattr;	// column names for the two matrices
	
	public Data(Type t, float[][] _x, float[][] _y, String[] _xattr, String[] _yattr) {
		type=t;
		x=_x;
		y=_y;
		xattr=_xattr;
		yattr=_yattr;
	}
	
	/** split a dataset into train- and test-set -  result[0]=train, result[1]=test **/
	public static Data[] split(Data data,int trainpercent){
		Data[] result=new Data[2];
		boolean[] b=new boolean[data.x.length];
		int traincount=0;
		for(int n=0;n<data.x.length;n++){
			b[n]=((int)(Math.random()*100))%100<=trainpercent;
			if(b[n])
				traincount++;
		}
		int testcount=data.getX().length-traincount;
		if(testcount==0){
			b[(int)(Math.random()*b.length)]=false;
			testcount=1;
			traincount--;
		}else if(traincount==0){
			b[(int)(Math.random()*b.length)]=true;
			traincount=1;
			testcount--;
		}		
		result[0]=new Data(data.type,new float[traincount][data.getX()[0].length],
				new float[traincount][data.getY()[0].length],copy(data.xattr),copy(data.yattr));
		result[1]=new Data(data.type,new float[testcount][data.getX()[0].length],
				new float[testcount][data.getY()[0].length],copy(data.xattr),copy(data.yattr));
		int trainidx=0,testidx=0;
		for(int n=0;n<data.x.length;n++){
			if(b[n]){
				result[0].x[trainidx]=Arrays.copyOf(data.x[n], data.x[n].length);
				result[0].y[trainidx++]=Arrays.copyOf(data.y[n], data.y[n].length);
			}else{
				result[1].x[testidx]=Arrays.copyOf(data.x[n], data.x[n].length);
				result[1].y[testidx++]=Arrays.copyOf(data.y[n], data.y[n].length);				
			}
		}
		return result;
	}
	
	public static String[] copy(String[] a){
		return Arrays.copyOf(a,a.length);
	}

	/** scale columns using given mins and maxs (from float[][] scaleColumns(float[][] m, float min, float max)) **/
	public static void scaleColumns(float[][] m, float min, float max, float[] mins, float[] maxs){
		for(int n=0;n<m.length;n++)
			for(int i=0;i<m[0].length;i++)
				m[n][i]=maxs[i]==mins[i]?0:min+((max-min)*(m[n][i]-mins[i])/(maxs[i]-mins[i]));
	}
	
	/** scales each column of data to [-1..1] AS IF the given scaling contains mins/maxs for them **/
	public static void scaleColumns(Data data, Data scaling){
		scaleColumns(data.getX(), -1,1,scaling.getX()[0],scaling.getX()[1]);
		if(data.type==Type.Regression)
			scaleColumns(data.getY(), -1,1,scaling.getY()[0],scaling.getY()[1]);
	}
	
	/** returns a data containing two rows with mins and maxs for x and y **/
	public static Data getBounds(Data d){
		try{
			final float[][] x=new float[2][d.getXattr().length];
			final float[][] y=new float[2][d.getYattr().length];
			Arrays.fill(x[0], Float.MAX_VALUE);
			Arrays.fill(x[1], -Float.MAX_VALUE);
			Arrays.fill(y[0], Float.MAX_VALUE);
			Arrays.fill(y[1], -Float.MAX_VALUE);
			for(int n=0;n<d.getX().length;n++){
				float[] xrow=d.getX()[n];
				float[] yrow=d.getY()[n];
				for(int i=0;i<xrow.length;i++)
					if(!Float.isInfinite(xrow[i]) && !Float.isNaN(xrow[i])){
						if(xrow[i]<x[0][i])
							x[0][i]=xrow[i];
						if(xrow[i]>x[1][i])
							x[1][i]=xrow[i];
				}
				for(int i=0;i<yrow.length;i++)
					if(!Float.isInfinite(yrow[i]) && !Float.isNaN(yrow[i])){
						if(yrow[i]<y[0][i])
							y[0][i]=yrow[i];
						if(yrow[i]>y[1][i])
							y[1][i]=yrow[i];
				}	
			}
			return new Data(d.type,x,y,Data.copy(d.xattr),Data.copy(d.yattr));
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/** scale columns of m[][] to [min...max] and return the mins and maxs of the original data **/
	public static float[][] scaleColumns(float[][] m, float min, float max){
		float[] mins=new float[m[0].length];
		float[] maxs=new float[m[0].length];
		for(int n=0;n<mins.length;n++){
			mins[n]=Float.MAX_VALUE;
			maxs[n]=-Float.MAX_VALUE;
		}
		for(int n=0;n<m.length;n++){
			for(int i=0;i<m[0].length;i++){
				if(m[n][i]<mins[i])
					mins[i]=m[n][i];
				if(m[n][i]>maxs[i])
					maxs[i]=m[n][i];
			}
		}
		scaleColumns(m,min,max,mins,maxs);
		return new float[][]{mins,maxs};
	}
	
	/** scale column m[][idx] to [min..max] **/
	public static void scaleColumn(float[][] m, int idx, float min, float max){
		float mi=Float.MAX_VALUE;
		float ma=-Float.MAX_VALUE;
		for(int n=0;n<m.length;n++){
			if(m[n][idx]<mi)
				mi=m[n][idx];
			if(m[n][idx]>ma)
				ma=m[n][idx];
		}
		float div=ma-mi;
		if(div!=0)
			for(int n=0;n<m.length;n++)
				m[n][idx]=min+((max-min)*(m[n][idx]-mi)/(div));
	}
	
	/** transpose given matrix **/
	public static float[][] transpose(float[][] m){
		final float r[][]=new float[m[0].length][m.length];
		for(int n=0;n<m.length;n++)
			for(int i=0;i<m[0].length;i++)
				r[i][n]=m[n][i];
		return r;
	}
	
	/** transpose given matrix into a double-matrix **/
	public static double[][] transposeD(float[][] m){
		final double r[][]=new double[m[0].length][m.length];
		for(int n=0;n<m.length;n++)
			for(int i=0;i<m[0].length;i++)
				r[i][n]=m[n][i];
		return r;
	}
	
	/** remove the x-column with the given name **/
	public static Data deleteColumn(Data d, String name){
		int idx=find(d.xattr,name);
		if(idx==-1){
			log("Data.deleteColumn(): Column '"+name+"' not found - returning original object");
			return d;
		}
		float[][] x=new float[d.x.length][d.x[0].length-1];
		float[][] y=new float[d.y.length][d.y[0].length];
		String[] xattr=new String[d.xattr.length-1];
		for(int i=0;i<d.xattr.length-1;i++)
			xattr[i]=d.xattr[i>=idx?i+1:i];
		for(int n=0;n<x.length;n++){
			for(int i=0;i<d.x[0].length-1;i++)
				x[n][i]=d.x[n][i>=idx?i+1:i];
			for(int i=0;i<d.y[0].length;i++)
				y[n][i]=d.y[n][i];
		}
		return new Data(d.type,x,y,xattr,copy(d.yattr));
	}
	
	/** return a set of different values for a given column **/
	public HashSet<Float> distinct(final String colname){
		final int idx=Data.find(xattr, colname);
		if(idx==-1){
			log("distinct col '"+colname+"' not found");
			return null;
		}
		final HashSet<Float> h=new HashSet<Float>();
		for(int n=0;n<x.length;n++)
			h.add(x[n][idx]);
		return h;
	}
	
	/** create a subset of given data using a row-tester **/
	public static Data selectRows(Data d, Selector<float[]> xrowtester){
		HashSet<Integer> h=new HashSet<Integer>();
		for(int n=0;n<d.x.length;n++)
			if(xrowtester.test(d.x[n]))
				h.add(n);
		float[][] x=new float[h.size()][d.x[0].length];
		float[][] y=new float[h.size()][d.y[0].length];
		int row=0;
		for(int n=0;n<d.x.length;n++)
			if(h.contains(n)){
				x[row]=Arrays.copyOf(d.x[n],d.x[n].length);
				y[row]=Arrays.copyOf(d.y[n],d.y[n].length);
				row++;
			}
		return new Data(d.type,x,y,copy(d.xattr),copy(d.yattr));
	}
	
	/** return the first index of v in a **/
	public static int find(String[] a, String v){
		for(int n=0;n<a.length;n++)
			if(a[n]!=null && a[n].equals(v))
				return n;
		return -1;
	}
	
	/** create a set of (max partitions) binary columns representing value-ranges of column with the given name **/
	public static void expandXColumnByRange(Data data, String name, int partitions){
		int idx=find(data.xattr,name);
		if(idx==-1){
			log("Error: attr '"+name+"' does not exist");
			return;
		}
		Vector<Float> nums=new Vector<Float>();
		boolean neednan=false;
		for(int n=0;n<data.x.length;n++)
			if(!Float.isNaN(data.x[n][idx]) && !Float.isInfinite(data.x[n][idx])){
				nums.add(data.x[n][idx]);
			}else
				neednan=true;
		Collections.sort(nums);
		float step=nums.size()/(float)partitions;
		float[] bounds=new float[partitions-1];
		for(int n=0;n<bounds.length;n++){
			int i=(int)(step*(n+1));
			i=i>=nums.size()?nums.size()-1:i<0?0:i;
			bounds[n]=nums.get(i);
		}
		String[] colnames=new String[data.x[0].length+bounds.length+(neednan?1:0)];
		for(int i=0;i<data.x[0].length-1;i++)
			colnames[i]=data.xattr[i>=idx?i+1:i];
		for(int n=0;n<colnames.length-data.x[0].length+1;n++){
			if(n==0)
				colnames[n+data.x[0].length-1]=name+"<"+bounds[0];
			else if(n<bounds.length)
				colnames[n+data.x[0].length-1]=name+"["+bounds[n-1]+"-"+bounds[n]+")";
			else if(n==bounds.length)
				colnames[n+data.x[0].length-1]=name+"≥"+bounds[n-1];
			else if(n==bounds.length+1)
				colnames[n+data.x[0].length-1]=name+"_Nan";
		}
		Vector<HashSet<Integer>> sets=new Vector<HashSet<Integer>>();
		for(int n=0;n<bounds.length+(neednan?2:1);n++)
			sets.add(new HashSet<Integer>());
		for(int n=0;n<data.x.length;n++){
			float v=data.x[n][idx];
			if(Float.isNaN(v) || Float.isInfinite(v))
				sets.lastElement().add(n);
			else if(v<bounds[0])
				sets.firstElement().add(n);
			else if(v>=bounds[bounds.length-1])
				sets.get(sets.size()-(neednan?2:1)).add(n);
			else for(int i=1;i<bounds.length;i++)
					if(v>=bounds[i-1] && v<bounds[i]){
						sets.get(i).add(n);
						break;
					}
		}
		float[][] x=new float[data.x.length][colnames.length];
		for(int n=0;n<x.length;n++){
			for(int i=0;i<data.x[0].length-1;i++)
				x[n][i]=data.x[n][i>=idx?i+1:i];
		}
		for(int n=0;n<sets.size();n++){
			HashSet<Integer> e=sets.get(n);
			for(int i : e)
				x[i][n+data.x[0].length-1]=1;
		}
		data.x=x;
		data.xattr=colnames;
	}
	
	/** create a set of (max maxount) binary columns for different values in column with the given name **/
	public static float[] expandXColumn(Data data, String name, int maxcount){
		int idx=find(data.xattr,name);
		if(idx==-1){
			log("Error: attr '"+name+"' does not exist");
			return null;
		}
		HashMap<Float,HashSet<Integer>> sets=new HashMap<Float,HashSet<Integer>>();
		for(int n=0;n<data.x.length;n++){
			float val=data.x[n][idx];
			HashSet<Integer> set=sets.get(val);
			if(set==null){
				set=new HashSet<Integer>();
				sets.put(val,set);
			}
			set.add(n);
		}
		HashSet<Integer> rest=new HashSet<Integer>();
		if(sets.size()>maxcount){
			Vector<Entry<Float,HashSet<Integer>>> v=new Vector<Entry<Float,HashSet<Integer>>>();
			v.addAll(sets.entrySet());
			Collections.sort(v,new Comparator<Entry<Float,HashSet<Integer>>>(){
				@Override
				public int compare(Entry<Float, HashSet<Integer>> a, Entry<Float, HashSet<Integer>> b) {
					return -new Integer(a.getValue().size()).compareTo(b.getValue().size());
				}
			});
			sets=new HashMap<Float,HashSet<Integer>>();
			for(int n=0;n<v.size();n++)
				if(n<maxcount-1)
					sets.put(v.get(n).getKey(), v.get(n).getValue());
				else
					rest.addAll(v.get(n).getValue());
		}
		float[][] x=new float[data.x.length][data.x[0].length-1+sets.size()+(rest.size()>0?1:0)];
        String[] colnames=new String[x[0].length];
		for(int n=0;n<x.length;n++){
			for(int i=0;i<data.x[0].length-1;i++)
				x[n][i]=data.x[n][i>=idx?i+1:i];
		}
		for(int i=0;i<data.x[0].length-1;i++)
			colnames[i]=data.xattr[i>=idx?i+1:i];
		int pos=data.x[0].length-1;
		float[] result=new float[sets.size()];
		for(Entry<Float,HashSet<Integer>> e : sets.entrySet()){
			colnames[pos]=name+"_"+e.getKey();
			result[pos-data.x[0].length+1]=e.getKey();
			for(int n : e.getValue()){
				x[n][pos]=1;
			}
			pos++;
		}
		if(rest.size()>0){
			colnames[pos]=name+"_rest";
			for(int n : rest)
				x[n][pos]=1;
		}
		data.x=x;
		data.xattr=colnames;
		return result;
	}
	
	/** return a little report about the given dataset **/
	public static String analyze(Data d){
		return "X:\n"+analyze(d.x,d.xattr)+"\nY:\n"+analyze(d.y,d.yattr);
	}
	
	private static String analyze(float[][] m,String[] colnames){
		StringBuilder s=new StringBuilder();
		double[] mins=new double[m[0].length];
		double[] maxs=new double[m[0].length];
		double[] means=new double[m[0].length];
		int[] nans=new int[m[0].length];
		int[] infs=new int[m[0].length];
		Arrays.fill(mins,Double.MAX_VALUE);
		Arrays.fill(maxs,-Double.MAX_VALUE);
		for(int n=0;n<m.length;n++){
			for(int i=0;i<m[0].length;i++){
				if(Double.isNaN(m[n][i]))
					nans[i]++;
				else if(Double.isInfinite(m[n][i]))
					infs[i]++;
				else{
					if(m[n][i]<mins[i])
						mins[i]=m[n][i];
					if(m[n][i]>maxs[i])
						maxs[i]=m[n][i];
					means[i]+=m[n][i];				
				}
			}
		}
		for(int i=0;i<m[0].length;i++){
			double d=m.length-nans[i]-infs[i];
			means[i]=d==0?Double.NaN:means[i]/d;
		}
		s.append(m.length+"x"+m[0].length+"-Matrix:\n");
		for(int n=0;n<m[0].length;n++)
			s.append("\t"+colnames[n]+": \tMin="+mins[n]+", Max="+maxs[n]+", Mean="+means[n]+", NaN="+nans[n]+", Infs="+infs[n]+"\n");
		return s.toString();
	}
	
	/** simple, secure, inefficient **/
	public static Data clone(Data d){
		try{
			final ByteArrayOutputStream bo=new ByteArrayOutputStream();
			encode(d,new BufferedOutputStream(bo));
			return decode(new BufferedInputStream(new ByteArrayInputStream(bo.toByteArray())));
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/** simple, secure, inefficient **/
	/*public static Data clone(Data d){
		try{
			File f=new File("tmp_"+Math.random()+".data");
			encode(d,new FileOutputStream(f));
			Data result=decode(new FileInputStream(f));
			f.delete();
			return result;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}*/

	/** read a Data-Object from given stream (e.g. a file) **/
	public static Data decode(InputStream _in){
		try{
			DataInputStream in=new DataInputStream(new BufferedInputStream(_in));
			Type type=in.readInt()==1?Type.Regression:Type.Classification;
			int xr=in.readInt();
			int xc=in.readInt();
			int yr=in.readInt();
			int yc=in.readInt();
			float[][] x=new float[xr][xc];
			float[][] y=new float[yr][yc];
			int sxattrcount=in.readInt();
			StringBuilder sxattr=new StringBuilder();
			for(int n=0;n<sxattrcount;n++)
				sxattr.append(in.readChar());
			sxattr.trimToSize();
			String[] xattr=sxattr.toString().split(",");
			int syattrcount=in.readInt();
			StringBuilder syattr=new StringBuilder();
			for(int n=0;n<syattrcount;n++)
				syattr.append(in.readChar());
			syattr.trimToSize();
			String[] yattr=syattr.toString().split(",");
			for(int n=0;n<xr;n++)
				for(int i=0;i<xc;i++)
					x[n][i]=in.readFloat();
			for(int n=0;n<yr;n++)
				for(int i=0;i<yc;i++)
					y[n][i]=in.readFloat();
			in.close();
			return new Data(type,x,y,xattr,yattr);
		}catch(Exception e){e.printStackTrace();}
		return null;
	}

	/** write a Data-Object into given stream **/
	public static void encode(Data data, OutputStream _out){
		try{	
			DataOutputStream out=new DataOutputStream(new BufferedOutputStream(_out));
			out.writeInt(data.type==Type.Regression?1:-1);
			out.writeInt(data.x.length);
			out.writeInt(data.xattr.length);
			out.writeInt(data.y.length);
			out.writeInt(data.yattr.length);
			StringBuilder sxattr=new StringBuilder();
			boolean first=true;
			for(int n=0;n<data.xattr.length;n++){
				if(first)
					first=false;
				else 
					sxattr.append(',');
				sxattr.append(data.xattr[n]);
			}
			sxattr.trimToSize();
			out.writeInt(sxattr.length());
			out.writeChars(sxattr.toString());

			StringBuilder syattr=new StringBuilder();
			first=true;
			for(int n=0;n<data.yattr.length;n++){
				if(first)
					first=false;
				else 
					syattr.append(',');
				syattr.append(data.yattr[n]);
			}
			syattr.trimToSize();
			out.writeInt(syattr.length());
			out.writeChars(syattr.toString());

			for(int n=0;n<data.x.length;n++)
				for(int i=0;i<data.x[0].length;i++)
					out.writeFloat(data.x[n][i]);
			for(int n=0;n<data.y.length;n++)
				for(int i=0;i<data.y[0].length;i++)
					out.writeFloat(data.y[n][i]);
			out.close();
		}catch(Exception e){e.printStackTrace();}

	}
	
	/** export libsvm-format file (column names will get lost/have to be integers)**/
	public static void writeLibSVM(Data _d, Writer _w) {
		BufferedWriter w=new BufferedWriter(_w);
		Data d=Data.clone(_d);
		String[] sorted=d.getXattr().clone();
		Arrays.sort(sorted,new Comparator<String>(){
			@Override
			public int compare(String a, String b) {
				return ((Integer)Integer.parseInt(a)).compareTo(Integer.parseInt(b));
			}			
		});
		int[] colidxs=new int[sorted.length];
		for(int n=0;n<sorted.length;n++)
			colidxs[n]=find(d.getXattr(),sorted[n]);
		d.setX(Data.selectColumns(d.x, colidxs));
		d.setXattr(sorted);
		if(d.getType()==Type.Classification)
			try{
				for(int n=0;n<d.getX().length;n++){
					String st=d.getYattr()[maxIdx(d.getY()[n])];
					Integer is=Integer.parseInt(st.startsWith("+")?st.substring(1):st);
					w.write(""+is);
					for(int i=0;i<d.getX()[0].length;i++)
						w.write(" "+d.getXattr()[i]+":"+d.getX()[n][i]);
						w.write('\n');
				}
				w.close();
			}catch(Exception e){e.printStackTrace();}			
		else if(d.getType()==Type.Regression)
			try{
				for(int n=0;n<d.getX().length;n++){
					w.write(Float.toString(d.getY()[n][0]));
					for(int i=0;i<d.getX()[0].length;i++)
						w.write(" "+d.getXattr()[i]+":"+d.getX()[n][i]);
						w.write('\n');
				}
				w.close();
			}catch(Exception e){e.printStackTrace();}
	}
	
	/** read a Data-Object from given libsvm-file (of the given Type with given column names) **/
	/*public static Data readLibSVM(Type t, File f,String[] _xattr, String[] _yattr){
		if(!f.exists()){
			log("File '"+f.getAbsolutePath()+"' does not exist");
			return null;
		}
		try{
			HashMap<Integer,Integer> hxattr=new HashMap<Integer,Integer>();
			HashMap<Integer,Integer> hyattr=new HashMap<Integer,Integer>();
			Vector<float[]> X=new Vector<float[]>();
			Vector<float[]> Y=new Vector<float[]>();

			for(int n=0;n<_xattr.length;n++)
				hxattr.put(Integer.parseInt(_xattr[n]),n);
			for(int n=0;n<_yattr.length;n++){
				hyattr.put(Integer.parseInt(_yattr[n]),n);
				//log("added"+_yattr[n]+" .."+hyattr.get(-1));
				
			}
			BufferedReader in=new BufferedReader(new FileReader(f));
			String line=null;
			while((line=in.readLine())!=null){
				String[] tmp=line.trim().split(" ");
				float[] xrow=new float[_xattr.length];
				float[] yrow=new float[_yattr.length];
				if(t==Type.Regression)
					yrow[0]=Float.parseFloat(tmp[0]);
				else if(t==Type.Classification){
					String[] tmp2=tmp[0].split(",");
					for(int n=0;n<tmp2.length;n++){
						final Integer yid="+1".equals(tmp2[n])?1:Integer.parseInt(tmp2[n]);
						Integer yidx=hyattr.get(yid);
						if(yidx==null)
							log("yidx null for yid "+yid);
						yrow[yidx]=1;
					}
				}
				X.add(xrow);
				Y.add(yrow);
			}
			in.close();
			float[][] x=new float[X.size()][_xattr.length];
			float[][] y=new float[X.size()][_yattr.length];
			for(int n=0;n<X.size();n++){
				x[n]=X.get(n);
				y[n]=Y.get(n);
			}
			return new Data(t,x,y,copy(_xattr),copy(_yattr));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}*/

	public static Data readLibSVM(Type t, File f,String[] _xattr, String[] _yattr){
		if(!f.exists()){
			log("File '"+f.getAbsolutePath()+"' does not exist");
			return null;
		}
		try{
			HashMap<String,Integer> hxattr=new HashMap<String,Integer>();
			HashMap<String,Integer> hyattr=new HashMap<String,Integer>();
			for(int n=0;n<_xattr.length;n++)
				hxattr.put(_xattr[n],n);
			for(int n=0;n<_yattr.length;n++){
				hyattr.put(_yattr[n],n);
			}
			BufferedReader in=new BufferedReader(new FileReader(f));
			String line=null;
			int rowcount=0;
			while((line=in.readLine())!=null){
				int idx=line.indexOf(' ');
				if(idx==-1){
					log("Error in line "+rowcount+" of "+f.getAbsolutePath());
					return null;
				}
				rowcount++;
			}
			in.close();
			float[][] x=new float[rowcount][_xattr.length];
			float[][] y=new float[rowcount][_yattr.length];
			in=new BufferedReader(new FileReader(f));
			line=null;
			rowcount=0;
			while((line=in.readLine())!=null){
				int idx=line.indexOf(' ');			
				String sy=line.substring(0,idx).trim();
				String sx=line.substring(idx+1).trim();
				String tmp[]=sx.split(" ");
				for(int n=0;n<tmp.length;n++){
					String pair[]=tmp[n].split(":");
					Integer xidx=hxattr.get(pair[0].trim());
					if(xidx!=null)
						x[rowcount][xidx]=Float.parseFloat(pair[1].trim());
				}
				if(t==Type.Regression)
					y[rowcount][0]=Float.parseFloat(sy.trim());
				else if(t==Type.Classification){
					Integer i=Integer.parseInt(sy.startsWith("+")?sy.substring(1):sy);
					sy=""+i;
					y[rowcount][hyattr.get(sy)]=1;
				}
				rowcount++;	
			}
			in.close();
			return new Data(t,x,y,copy(_xattr),copy(_yattr));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/** import libsvm data **/
	/*public static Data readLibSVM(Type t, File f){
		log("readlibsvm without attr");

		if(!f.exists()){
			log("File '"+f.getAbsolutePath()+"' does not exist");
			return null;
		}
		try{
			final BufferedReader in=new BufferedReader(new FileReader(f));
			String line=null;
			final HashMap<Integer,Integer> hxattr=new HashMap<Integer,Integer>();
			final HashMap<Integer,Integer> hyattr=new HashMap<Integer,Integer>();
			final Vector<HashMap<Integer,Float>> X=new Vector<HashMap<Integer,Float>>();
			final Vector<HashMap<Integer,Float>> Y=new Vector<HashMap<Integer,Float>>();
			if(t==Type.Regression)
				hyattr.put(0,0);
			long count=0;
			log("in maps");
			String[] tmp=null;
			String[] tmp2=null;
			while((line=in.readLine())!=null){
				if(count++%10000==0){
					log("gc"+count);
					System.gc();
				}
				line=line.trim();
				if(line.length()>0){
					final HashMap<Integer,Float> xrow=new HashMap<Integer,Float>();
					final HashMap<Integer,Float> yrow=new HashMap<Integer,Float>();
					X.add(xrow);
					Y.add(yrow);
					tmp=line.trim().split(" ");
					if(t==Type.Regression)
						yrow.put(0, Float.parseFloat(tmp[0]));
					else if(t==Type.Classification){
						tmp2=tmp[0].split(",");
						for(int n=0;n<tmp2.length;n++){
							final Integer yid=Integer.parseInt(tmp2[n].startsWith("+")?tmp2[n].substring(1):tmp2[n]);
							Integer yidx=hyattr.get(yid);
							if(yidx==null){
								yidx=hyattr.size();
								hyattr.put(yid, yidx);
							}
							yrow.put(yidx, 1f);
						}
					}
					for(int n=1;n<tmp.length;n++){
						final String[] pair=tmp[n].split(":");
						if(pair.length==2){
							final Integer xid=Integer.parseInt(pair[0]);
							Integer xidx=hxattr.get(xid);
							if(xidx==null){
								xidx=hxattr.size();
								hxattr.put(xid, xidx);
							}
							xrow.put(xidx, Float.parseFloat(pair[1]));
						}
					}	
				}
			}
			in.close();
			log("in array");
			final float[][] x=new float[X.size()][hxattr.size()];
			final float[][] y=new float[Y.size()][hyattr.size()];
			final String[] xcolnames=new String[hxattr.size()];
			final String[] ycolnames=new String[hyattr.size()];
			for(Entry<Integer,Integer> e : hxattr.entrySet())
				xcolnames[e.getValue()]=e.getKey().toString();
			for(Entry<Integer,Integer> e : hyattr.entrySet())
				ycolnames[e.getValue()]=e.getKey().toString();
			for(int n=0;n<X.size();n++){
				HashMap<Integer,Float> xrow=X.get(n);
				HashMap<Integer,Float> yrow=Y.get(n);
				for(Entry<Integer,Float> e : xrow.entrySet())
					x[n][e.getKey()]=e.getValue();
				for(Entry<Integer,Float> e : yrow.entrySet())
					y[n][e.getKey()]=e.getValue();
			}
			log("done");
			return new Data(t,x,y,xcolnames,ycolnames);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}*/

	/** import libsvm data **/
	/*public static Data readLibSVM(Type t, File f){
		if(!f.exists()){
			log("File '"+f.getAbsolutePath()+"' does not exist");
			return null;
		}
		try{
			BufferedReader in=new BufferedReader(new FileReader(f));
			String line=null;
			int rowcount=0;
			final HashMap<String,Integer> hxattr=new HashMap<String,Integer>();
			final HashMap<String,Integer> hyattr=new HashMap<String,Integer>();
			if(t==Type.Regression)
				hyattr.put("r0",0);
			while((line=in.readLine())!=null){
				line=line.trim();
				final StringTokenizer tk=new StringTokenizer(line);
				if(tk.hasMoreTokens()){//Y
					final String yst=tk.nextToken().trim();
					if(t==Type.Classification){
						final Integer yidx=hyattr.get(yst);
						if(yidx==null)
							hyattr.put(yst,hyattr.size());							
					}
				}
				while(tk.hasMoreTokens()){
					final String sx=tk.nextToken();
					int idx=sx.indexOf(":");
					if(idx!=-1){
						final String tr=sx.substring(0,idx-1);
						final Integer xidx=hxattr.get(tr);
						if(xidx==null)
							hxattr.put(tr,hxattr.size());							
					}
				}
				rowcount++;
				if(rowcount%10000==0)
					System.gc();
			}
			in.close();
			final String[] xattr=new String[hxattr.size()];
			final String[] yattr=new String[hyattr.size()];
			for(Entry<String,Integer> a : hxattr.entrySet())
				xattr[a.getValue()]=a.getKey();
			for(Entry<String,Integer> a : hyattr.entrySet())
				yattr[a.getValue()]=a.getKey();
			final float[][] x=new float[rowcount][hxattr.size()];
			final float[][] y=new float[rowcount][hyattr.size()];			
			in=new BufferedReader(new FileReader(f));
			line=null;
			rowcount=0;
			while((line=in.readLine())!=null){
				
				
				line=line.trim();
				final StringTokenizer tk=new StringTokenizer(line);
				if(tk.hasMoreTokens()){//Y
					final String yst=tk.nextToken().trim();
					if(t==Type.Regression)
						y[rowcount][0]=Float.parseFloat(yst);
					else if(t==Type.Classification)
						y[rowcount][hyattr.get(yst)]=1;
				}
				while(tk.hasMoreTokens()){
					final String sx=tk.nextToken().trim();
					int idx=sx.indexOf(":");
					if(idx>-1)						
						x[rowcount][hxattr.get(sx.substring(0,idx-1))]=Float.parseFloat(sx.substring(idx+1));
				}
				rowcount++;
				if(rowcount%10000==0)
					System.gc();
			}
			in.close();
			return new Data(t,x,y,xattr,yattr);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}*/
	
	public static Data readLibSVM(Type t, File f){
		if(!f.exists()){
			log("File '"+f.getAbsolutePath()+"' does not exist");
			return null;
		}
		try{
			BufferedReader in=new BufferedReader(new FileReader(f));
			String line=null;
			int rowcount=0;
			final HashMap<String,Integer> hxattr=new HashMap<String,Integer>();
			final HashMap<String,Integer> hyattr=new HashMap<String,Integer>();
			if(t==Type.Regression)
				hyattr.put("0",0);
			while((line=in.readLine())!=null){
				int idx=line.indexOf(' ');
				if(idx==-1){
					log("Error 1 in line "+rowcount+" of "+f.getAbsolutePath());
					return null;
				}else{
					if(t==Type.Classification){
						String sy=line.substring(0,idx).trim();
						Integer i=Integer.parseInt(sy.startsWith("+")?sy.substring(1):sy);
						sy=""+i;
						Integer yidx=hyattr.get(sy);
						if(yidx==null){
							hyattr.put(sy,hyattr.size());			
						}
					}
					String sx=line.substring(idx+1).trim();
					String tmp[]=sx.split(" ");
					for(int n=0;n<tmp.length;n++){
						String pair[]=tmp[n].split(":");
						if(pair.length==2){
							Integer xidx=hxattr.get(pair[0].trim());
							if(xidx==null)
								hxattr.put(pair[0].trim(),hxattr.size());							
						}
					}
					rowcount++;
				}
			}
			in.close();
			String[] xattr=new String[hxattr.size()];
			String[] yattr=new String[hyattr.size()];
			for(Entry<String,Integer> a : hxattr.entrySet())
				xattr[a.getValue()]=a.getKey();
			for(Entry<String,Integer> a : hyattr.entrySet()){
				yattr[a.getValue()]=a.getKey();
			}
			float[][] x=new float[rowcount][hxattr.size()];
			float[][] y=new float[rowcount][hyattr.size()];			
			in=new BufferedReader(new FileReader(f));
			line=null;
			rowcount=0;
			while((line=in.readLine())!=null){
				int idx=line.indexOf(' ');			
				String sy=line.substring(0,idx).trim();
				String sx=line.substring(idx+1).trim();
				String tmp[]=sx.split(" ");
				for(int n=0;n<tmp.length;n++){
					String pair[]=tmp[n].split(":");
					if(pair.length==2)
						x[rowcount][hxattr.get(pair[0].trim())]=Float.parseFloat(pair[1].trim());
				}
				if(t==Type.Regression)
					y[rowcount][0]=Float.parseFloat(sy.trim());
				else if(t==Type.Classification){
					Integer i=Integer.parseInt(sy.startsWith("+")?sy.substring(1):sy);
					sy=""+i;
					y[rowcount][hyattr.get(sy)]=1;
				}
				rowcount++;	
			}
			in.close();
			return new Data(t,x,y,xattr,yattr);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/** export a comma-separated-values file from given Data **/
	public static boolean writeCSV(Data d, Writer w) {
		try{
		boolean first=true;
		for(int n=0;n<d.getYattr().length;n++){
			if(first)
				first=false;
			else
				w.write(',');
			w.write("\""+d.getYattr()[n]+"\"");
		}
		for(int n=0;n<d.getXattr().length;n++)
			w.write(",\""+d.getXattr()[n]+"\"");
		w.write('\n');
		for(int n=0;n<d.getX().length;n++){
			first=true;
			for(int i=0;i<d.getY()[0].length;i++){
				if(first)
					first=false;
				else
					w.write(',');
				w.write(Float.toString(d.getY()[n][i]));
			}
			for(int i=0;i<d.getX()[0].length;i++)
				w.write(","+Float.toString(d.getX()[n][i]));
			if(n<d.getX().length-1)
				w.write('\n');
		}
		w.close();
		}catch(Exception e){e.printStackTrace();}
		return false;
	}
	

	/** get column-means of x (without nans and infinites) **/
	public float[] meanX() {
		double[] _means=new double[x[0].length];
		int[] nans=new int[x[0].length];
		for(int n=0;n<x.length;n++)
			for(int i=0;i<x[0].length;i++)
				if(Float.isNaN(x[n][i]) || Float.isInfinite(x[n][i]))
					nans[i]++;
				else
					_means[i]+=x[n][i];			
		float[] means=new float[_means.length];
		for(int i=0;i<x[0].length;i++){
			float d=x.length-nans[i];
			means[i]=d==0?Float.NaN:(float)(_means[i]/d);
		}
		return means;
	}
	
	/** create a partitioning of N elements with given max-partitionsize **/
	public static int[] partition(int N,int maxpartsize){
		int[] result=new int[N];
		Arrays.fill(result,-1);
		Vector<Integer> idxs=new Vector<Integer>();
		for(int n=0;n<N;n++)
			idxs.add(n);
		Collections.shuffle(idxs);
		int p=0;
		while(idxs.size()>0){
			for(int n=0;n<Math.min(maxpartsize,idxs.size());n++)
				result[idxs.remove(0)]=p;
			p++;
		}	
		return result;
	}
	
	/** return indices of x in v (or all others if inverse==true) **/
	public static int[] find(int[] v,int x,boolean inverse){
		Vector<Integer> a=new Vector<Integer>();
		for(int n=0;n<v.length;n++)
			if((!inverse && v[n]==x) || (inverse && v[n]!=x))
				a.add(n);
		int[] result=new int[a.size()];
		for(int n=0;n<result.length;n++)
			result[n]=a.get(n);
		return result;
	}
	
	/** create a (re-ordererd) subset of matrix d given row- and column-indices **/
	public static float[][] select(float[][] d, int[] rowidxs, int[] colidxs){
		final float[][] result=new float[rowidxs.length][colidxs.length];
		for(int r=0;r<rowidxs.length;r++)
			for(int c=0;c<colidxs.length;c++)
				result[r][c]=d[rowidxs[r]][colidxs[c]];
		return result;			
	}
	
	/** create a (re-ordererd) subset of matrix d given and column-indices **/
	public static float[][] selectColumns(float[][] d, int[] colidxs){
		final float[][] result=new float[d.length][colidxs.length];
		for(int r=0;r<d.length;r++)
			for(int c=0;c<colidxs.length;c++)
				result[r][c]=d[r][colidxs[c]];
		return result;			
	}

	/** create a (re-ordererd) subset of matrix d given row-indices **/
	public static float[][] select(float[][] d, int[] rowidxs){
		final float[][] result=new float[rowidxs.length][d[0].length];
		for(int r=0;r<rowidxs.length;r++)
			for(int c=0;c<d[0].length;c++)
				result[r][c]=d[rowidxs[r]][c];
		return result;			
	}
	
	/** write a string to file **/
	 static public void writeFile(File aFile, String aContents){
		  try{
			  Writer output = new BufferedWriter(new FileWriter(aFile));
			  output.write( aContents );
			  output.close();
		   }catch(Exception e){e.printStackTrace();}
	 }
	
	 /** create a nice string for a given time-span **/
		public static String renderTime(Long t, Long minunit,Long maxunit){
			Long l=new Long(t);
			String result="";
			if(minunit<=t_year && maxunit>=t_year &&l/t_year>0){
				result+=(l/t_year+"y");
				l%=t_year;
			}
			if(minunit<=t_week && maxunit>=t_week && l/t_week>0){
				result+=(l/t_week+"w");
				l%=t_week;
			}
			if(minunit<=t_day && maxunit>=t_day && l/t_day>0){
				result+=(l/t_day+"d");
				l%=t_day;
			}
			if(minunit<=t_hour && maxunit>=t_hour && l/t_hour>0){
				result+=(l/t_hour+"h");
				l%=t_hour;
			}
			if(minunit<=t_min && maxunit>=t_min && l/t_min>0){
				result+=(l/t_min+"m");
				l%=t_min;
			}
			if(minunit<=t_sec && maxunit>=t_sec && l/t_sec>0){
				result+=(l/t_sec+"s");
				l%=t_sec;
			}
			if(minunit<=t_millis && maxunit>=t_millis){
				result+=(l/t_millis+"ms");
				l%=t_millis;
			}
			return result;
		}

		/** found this fast tanh-method on stackoverflow.com - sorry for not citing correctly **/
		private static final int TANH_FRAC_EXP = 6; // LUT precision == 2 ** -6 == 1/64
		private static final int TANH_LUT_SIZE = (1 << TANH_FRAC_EXP) * 8 + 1;
		private static final float TANH_FRAC_BIAS = Float.intBitsToFloat((0x96 - TANH_FRAC_EXP) << 23);
		private static float[] TANH_TAB = new float[TANH_LUT_SIZE];
		static {	for (int i = 0; i < TANH_LUT_SIZE; ++ i) {	TANH_TAB[i] = (float) Math.tanh(i / 64.0);  } }
		public static float fastTanH(float x) {
		    if (x<0) return -fastTanH(-x);
		    if (x>8) return 1f;
		    float xp = TANH_FRAC_BIAS + x;
		    short ind = (short) Float.floatToRawIntBits(xp);
		    float tanha = TANH_TAB[ind];
		    float b = xp - TANH_FRAC_BIAS;
		    x -= b;
		    return tanha + x * (1f - tanha*tanha);
		}
		
		/** return the index with the minimal value of v **/
		public static int minIdx(float[] v){
			float min=Float.MAX_VALUE;
			int idx=-1;
			for(int n=0;n<v.length;n++)
				if(v[n]<min){
					min=v[n];
					idx=n;
				}
			return idx;
		}
		
		/** return the index with the maximal value of v **/
		public static int maxIdx(float[] v){
			float max=-Float.MAX_VALUE;
			int idx=-1;
			for(int n=0;n<v.length;n++)
				if(v[n]>max){
					max=v[n];
					idx=n;
				}
			return idx;
		}

		/** return the mean of values of x (ignoring nans and infinites) **/
		public static float mean(float[] x){
			double m=0;
			double c=0;
			for(int n=0;n<x.length;n++)
				if(!Float.isInfinite(x[n]) && !Float.isNaN(x[n])){
					m+=x[n];
					c++;
				}
			return c>0?(float)(m/c):Float.NaN;
		}
		
		/** multiply a matrix with a scalar **/
		public static void mult(float[][] a, float f){
			for(int n=0;n<a.length;n++)
				for(int i=0;i<a[0].length;i++)
					a[n][i]*=f;
		}
		
		/** elementwise add two equally sized matrices **/
		public static void add(float[][] a, float[][] f){
			for(int n=0;n<a.length;n++)
				for(int i=0;i<a[0].length;i++)
					a[n][i]+=f[n][i];
		}
		
		/** vector-matrix-multiplication **/
		public static float[] mult(float[] a, float[][] m){
			final float[] result=new float[m[0].length];
			mult(a,m,result);
			return result;
		}
		
		/** vector-matrix-multiplication (inplace) **/
		public static void mult(float[] a, float[][] m, float[] result){
			final int N=m.length, I=m[0].length;
			for(int i=0;i<I;i++)
				result[i]=0;
			for(int n=0;n<N;n++){
				final float[] row=m[n];
				final float f=a[n];
				for(int i=0;i<I;i++)
					result[i]+=f*row[i];
			}
		}
		
		/*public static void mult(float[] a, float[][] m, float[] result){
			int n=(result.length < m[0].length) ? result.length : m[0].length;
			final int I=(a.length < m.length) ? a.length : m.length;
			int i=I;
			while(n-->0){
				float sum=0;
				while(i-->0)
					sum+=a[i]*m[i][n];
				result[n]=sum;
				i=I;
			}
		}*/

		/** matrix-vector-multiplication **/
		public static float[] mult( float[][] m, float[] a){
			final float[] result=new float[m.length];
			mult(m,a,result);
			return result;
		}

		/** matrix-vector-multiplication (inplace) **/
		/*public static void mult(float[][] m, float[] a,float[] result){
			final int N=m.length, I=m[0].length;
			float sum;
			for(int n=0;n<N;n++){
				final float[] row=m[n];
				sum=0;
				for(int i=0;i<I;i++)
					sum+=a[i]*row[i];
				result[n]=sum;
			}
		}*/
		
		/** matrix-vector-multiplication (inplace) **/
		public static void mult(float[][] m, float[] a,float[] result){
			int n=(result.length < m.length) ? result.length : m.length;
			final int I=(a.length < m[0].length) ? a.length : m[0].length;
			int i=I;
			while(n-->0){
				float sum=0;
				while(i-->0)
					sum+=a[i]*m[n][i];
				result[n]=sum;
				i=I;
			}
		}

	
	/** kmeans - returns l2-normalized cluster centroids **/
	public static float[][] kmeans(float[][] x, int k, int maxiter, boolean norm){
		int I=0;
		float[][] c=new float[k][x[0].length];  // centroids
		int[] a=new int[x.length];			    // assignments
		int[] counts=new int[k];				// datapoints per cluster
		boolean changed=false;
		for(int n=0;n<x.length;n++)
			a[n]=(int)(Math.random()*k);
		while(true){
			for(int n=0;n<k;n++)
				Arrays.fill(c[n],0);
			Arrays.fill(counts,0);
			for(int n=0;n<a.length;n++){
				counts[a[n]]++;
				for(int i=0;i<x[0].length;i++)
					c[a[n]][i]+=x[n][i];
			}
			for(int n=0;n<k;n++)
				if(counts[n]>0)
					for(int i=0;i<c[n].length;i++)
						c[n][i]/=counts[n];
			changed=false;
			for(int n=0;n<x.length;n++){
				float mindist=Float.MAX_VALUE;
				int minidx=-1;
				for(int i=0;i<k;i++){
					float dist=L2(x[n],c[i]);
					if(dist<mindist){
						mindist=dist;
						minidx=i;
					}
				}
				if(!changed && a[n]!=minidx)
					changed=true;
				a[n]=minidx;
			}
			I++;
			if(I>=maxiter || !changed){
				if(norm)
					for(int n=0;n<k;n++)
						normL2(c[n]);
				return c;
			}
		}	
	}
	
	/** scale a vector to unit-length (L2-norm) **/
	public static void normL2(float[] a){
		double sum=0;
		for(int n=0;n<a.length;n++)
			sum+=(a[n]*a[n]);
		sum=Math.sqrt(sum);
		if(sum!=0)
		for(int n=0;n<a.length;n++)
			a[n]/=(float)sum;
	}
	
	/** euclidean distance between a and b **/
	public static float L2(float[] a, float[] b){
		double d=0;
		for(int n=0;n<a.length;n++)
			d+=(a[n]-b[n])*(a[n]-b[n]);
		return (float)Math.sqrt(d);
	}

	public static void log(String msg){
		System.out.println(msg);
	}
	
	public String[] getXattr() {
		return xattr;
	}
	
	public void setXattr(String[] xattr) {
		this.xattr = xattr;
	}
	
	public String[] getYattr() {
		return yattr;
	}
	
	public void setYattr(String[] yattr) {
		this.yattr = yattr;
	}
	
	public float[][] getX() {
		return x;
	}
	
	public void setX(float[][] x) {
		this.x = x;
	}
	
	public float[][] getY() {
		return y;
	}
	
	public void setY(float[][] y) {
		this.y = y;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String toString(){
		return "X: "+x.length+"x"+x[0].length+", Y: "+y.length+"x"+y[0].length+" ("+type+")";
	}

}
