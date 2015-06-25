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


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class NeuralNetwork {
	public static enum Type {Regression, Classification};
	
	protected Type type=Type.Regression;
	
	protected int l1count,l2count,l3count;		// layer sizes (without bias-nodes)
	protected float[][] l12,l23;				// weight matrices (including bias-weights)
	private float[] l1,l2,l3,d3,d2,l2put; 		// fields for inplace propagations
	private String[] xattr,yattr;				// names of inputs (important not only for libsvm data)

	private static NumberFormat nf = NumberFormat.getInstance();

	/** create new net based on given dataset with estimated optimal #hidden units **/
	public NeuralNetwork(Data d) {
		this(d.getType(), d.getX()[0].length,d.getType()==Type.Regression?4:guessHiddenCLS(d.getX().length, d.getX()[0].length, d.getY()[0].length), d.getY()[0].length);
	}

	/** create new net of given type and layer sizes **/
	public NeuralNetwork(Type _type, int _l1count, int _l2count, int _l3count){
		nf.setMinimumFractionDigits(8);
		nf.setMaximumFractionDigits(8);
		type=_type;
		l1=new float[_l1count+1];
		l2=new float[_l2count+1];
		l2put=new float[_l2count+1];
		l3=new float[_l3count];
		d3=new float[_l3count];
		d2=new float[_l2count+1];
		l1count=_l1count;
		l2count=_l2count;
		l3count=_l3count;
		l12=new float[l1count+1][l2count];
		l23=new float[l2count+1][l3count];
		xattr=new String[l1count];
		yattr=new String[l3count];
		for(int n=0;n<xattr.length;n++)
			xattr[n]="x"+n;
		for(int n=0;n<yattr.length;n++)
			yattr[n]="y"+n;		
		clear();
	}
	
	public void train(Data d) {
		train(d,false,null);
	}

	public final synchronized void train(final Data d, boolean quiet,String msg,int depth){
		train(d,quiet,msg,depth,null,null);
	}
	
	public final synchronized void train(final Data d, boolean quiet,String msg){
		train(d,quiet,msg,1,null,null);		
	}
	
	public final synchronized void train(final Data d, boolean quiet,String msg,int depth, StringBuilder trainlog, Data validat){
		xattr=Data.copy(d.getXattr());
		yattr=Data.copy(d.getYattr());
		train(d.getX(),d.getY(),quiet,msg,trainlog,validat,depth);		
	}
	
	public final synchronized void train(final float[][] _x, final float[][] _y){
		train(_x,_y,false,"untitled");
	}
	
	public final synchronized void train(final float[][] _x, final float[][] _y, boolean quiet, String msg){
		train(_x,_y,quiet,msg,null,null,1);
	}
	
	public final synchronized void train(final float[][] _x, final float[][] _y, boolean quiet, String msg, StringBuilder trainlog, Data validat, int depth){
		float minspeed=(depth==0?0.1f:depth==2?0.001f:0.02f);
		float maxspeed=(depth==0?10f:depth==2?0.1f:1);
		train(_x,_y,quiet,msg,trainlog,validat,minspeed,maxspeed,1f,0.8f);
	}

	/** 'core' train-method **/
	public final synchronized void train(final float[][] _x, final float[][] _y,
			boolean quiet, String msg, StringBuilder trainlog, Data validat, float minspeed, float maxspeed,float pstart, float pend){
		final long t1=System.currentTimeMillis();
		if(!quiet)
			log("Nen Start Training");
		float speed=minspeed;
		long lastlog=0, millis=0;
		final float[][] cent=Data.kmeans(_x, l2count,3+100000000/(_x.length*l1count*l2count),true); 
		for(int n=0;n<cent.length;n++)
			for(int i=0;i<cent[0].length;i++)
				l12[i][n]=(cent[n][i]==0?(float)Math.random()/l2count:((1f/l2count)*cent[n][i]));	
		if(!quiet)
			log("Initialized Weights");
		NeuralNetwork bestnn=clone();
		float bestperc=0;
		float er_train=1, er_secr=1, er_train_best=1, er_secr_best=1, c1=0, c2=0;
		int sucbet=1, sucwor=1;
		float lrate=0.000001f;
		int part=(int)((1f-0.25f)*_x.length);
		float e=0, e_best=0;
		float[][] pred_cur=new float[_x.length][l3count];
		float[][] pred_best=new float[_x.length][l3count];
		float[][] tmp_preds;
		float[] tmp_errs;
		float[] errs_best=new float[_x.length];
		float[] errs_cur=new float[_x.length];
		bestnn.get(_x,pred_best);
		for(int n=0;n<_x.length;n++){
			errs_best[n]=NeuralNetwork.getError(pred_best[n], _y[(n)],type);
			errs_cur[n]=errs_best[n];
		}
		String flymsg=null;
		float lr_start=1f/l1count;
		lrate=lr_start;
		float steps=100;
		float a=1,b=1;
		float p;
		long t3;
		int putcount=2*_x.length;
		if(trainlog!=null)
			trainlog.append("\"s\",\"speed\",\"learnrate\",\"er_train\",\"er_secr\",\"er_train_best\",\"er_secr_best\",\"err_bestvalidat\"\n");
		final long t2=System.currentTimeMillis();
		for(float s=0;s<=steps;s+=speed){
			lrate=Math.min(0.01f,Math.max(0.00001f,(float)(lr_start*(((double)sucbet/sucwor)))));
			p=(float)(pstart+((s/(double)steps)*(pend-pstart)));
			part=(int)((double)_x.length*p);		
			putcount=(int)(Math.min(Math.max(2d,(3d*sucbet)/sucwor),25d)*part);
			t3=System.currentTimeMillis();
			for(int n=0;n<=putcount;n++){
				final int lidx=(int)(part*Math.random());
				put(_x[(lidx)],_y[(lidx)],lrate);
			}
			t3=System.currentTimeMillis()-t3;
			c1=0; c2=0; er_train=0; er_secr=0; er_train_best=0; er_secr_best=0;
			get(_x,pred_cur);
			for(int n=0;n<_x.length;n++){
				e=getError(pred_cur[n], _y[(n)],type);
				errs_cur[n]=e;
				e_best=errs_best[n];
				if(n<=part){
					er_train+=e;			
					er_train_best+=e_best;
					c1++;
				}else{
					er_secr+=e;
					er_secr_best+=e_best;
					c2++;
				}
			}
			er_train/=c1;
			er_train_best/=c1;
			if(c2==0){
				er_secr=er_train;
				er_secr_best=er_train_best;
			}else{
				er_secr/=c2;
				er_secr_best/=c2;
			}
			float wtrain=0.8f;
			
			a=((1-wtrain)*er_secr+((wtrain)*er_train));
			b=((1-wtrain)*er_secr_best+((wtrain)*er_train_best));
			
			if(a<b){
				bestnn=clone();
				tmp_preds=pred_cur;
				pred_cur=pred_best;
				pred_best=tmp_preds;
				tmp_errs=errs_cur;
				errs_cur=errs_best;
				errs_best=tmp_errs;
				bestperc=s/steps;
				speed=minspeed;
				sucwor=1;
				sucbet++;
			}else{
				sucbet=1;
				speed=Math.min(2f*speed,maxspeed);
				sucwor++;
			}
			if(!quiet && (s==0 || s+speed>steps || ((millis=System.currentTimeMillis())-lastlog>7000))){
				lastlog=millis;
				log("Nen Training   "+l1count+"x"+l2count+"x"+l3count+"-"+type+"-Network   " + (msg!=null?"("+msg+")":"")+", putcount="+putcount+"\n"+
					" Traindata:    "+"X: "+_x.length+"x"+_x[0].length+"   Y: "+_y.length+"x"+_y[0].length+"   Secret: "+(_x.length-part)+" Datapoints\n"+
					" Progress:     "+nf.format(100*s/steps)+"%   Speed:"+nf.format(speed)+"   Since:"+Data.renderTime(lastlog-t1,Data.t_sec,Data.t_min)+"   Learnrate:"+nf.format(lrate)+"   "+nf.format(1000d*putcount/t3)+" Datapoints per Second\n"+
					" Error:        Current Network: "+nf.format(er_train)+" (Traindata / Secret)   Candidate from "+nf.format(100*bestperc)+"% : "+nf.format((er_secr_best+er_train_best)/2)+" (Traindata ∪ Secret)"
					+(validat==null?"\n":" vali: "+nf.format(bestnn.getError(validat.getX(), validat.getY()))+"\n")
						+(flymsg!=null?"               *****     "+flymsg+"     *****\n":""));
				flymsg=null;
			}
			if(trainlog!=null){
				float sec=(float)((System.currentTimeMillis()-t2)/1000d);
				trainlog.append(nf.format(sec)+","+nf.format((speed/steps))+","+nf.format(lrate)+","+nf.format(er_train)
						+","+nf.format(er_secr)+","+nf.format(er_train_best)+","+nf.format(er_secr_best)+","+nf.format(validat==null?Math.random():bestnn.getError(validat.getX(), validat.getY()))+"\n");
			}
		}
		l12=bestnn.l12;
		l23=bestnn.l23;
		if(!quiet)
			log("Nen Training finished");
	}

	 
	/** perform cross validation - return optimal #hidden units **/
	public static int xval(Data d, int[] hidden, int k, int maxpartitionsize,String _msg, File logfile){
		int psize=(int)Math.min(d.getX().length/(float)k,maxpartitionsize);
		int[] partitions=Data.partition(d.getX().length,psize);
		String msg="# "+_msg+"\n# "+k+"-fold XVAL on X: " +d.getX().length+"x"+d.getX()[0].length+", Y: "+d.getY().length+"x"+d.getY()[0].length+", hidden=[";
		for(int n=0;n<hidden.length;n++)
			msg+=hidden[n]+((n<hidden.length-1)?",":"] ");
		msg+=", maxpartsize="+maxpartitionsize+"\n";
		msg+="\"hiddencount\",\"error\"\n";
		float[] er=new float[hidden.length];
		Arrays.fill(er, Float.NaN);
		String l=msg;
		for(int n=0;n<hidden.length;n++){
			er[n]=Data.mean(getErrors(d,partitions,k,hidden[n]));
			l=msg;
			for(int i=0;i<hidden.length;i++)
				l+=hidden[i]+","+(Float.isNaN(er[i])?"(pending)":""+er[i])+"\n";
			log(l);
			if(logfile!=null)
				Data.writeFile(logfile,l);
		}
		int idx=Data.minIdx(er);
		l+="XVAL finished best performance with "+hidden[idx]+" hidden nodes";
		if(logfile!=null)
			Data.writeFile(logfile,l);
		return hidden[idx];
	}
	
	/** work in progress **/
	public static void findTopology(float[][] X, float[][] Y,int maxhidden, int trainsize,int repetitions, String[] featnames){
		File f=new File("topology_"+(new Date().toString().replaceAll(" ", "_").trim()+".log"));
		StringBuilder lg=new StringBuilder();
		lg.append(new Date().toString()+":\tfind topology for X="+X.length+"x"+X[0].length+", Y="+Y.length+"x"+Y[0].length+"," +
				" maxhidden="+maxhidden+", trainsize="+trainsize+", repetitions="+repetitions+"\n");
		String msg="";
		int[] partitions=Data.partition(X.length,trainsize);
		int hid=1;
		Vector<Integer> featidx=new Vector<Integer>();
		Vector<Integer> hids=new Vector<Integer>();
		Vector<Float> errs=new Vector<Float>();
		int maxiter=(X[0].length*X[0].length+X[0].length)/2;
		int iter=1;
		long t0=System.currentTimeMillis();
		for(int l=0;l<X[0].length;l++){
			float[][] ferrs=new float[X[0].length][repetitions];
			int[] fidx=new int[featidx.size()+1];
			for(int i=0;i<featidx.size();i++)
				fidx[i]=featidx.get(i);
			for(int n=0;n<X[0].length;n++)
				if(!featidx.contains(n)){
					fidx[fidx.length-1]=n;
					ferrs[n]=getErrors(Data.selectColumns(X,fidx),Y,partitions,repetitions,hid);
					float iterperc=((float)iter/(float)maxiter)*100;
					long t=System.currentTimeMillis()-t0;
					long eta=(long)((100d-iterperc)*(double)t/iterperc);
					log(lg+"\nFind topology iter "+iter+" of "+maxiter+" ("+iterperc+"%) since "+Data.renderTime(t, Data.t_sec, Data.t_day)+"" +
							" (ETA "+Data.renderTime(eta, Data.t_sec, Data.t_day)+") l="+l+", n="+n+", current network: "+fidx.length+"x"+hid+"x"+Y[0].length+(msg.length()>0?"\n"+msg:""));					
					iter++;
				}else
					ferrs[n]=null;
			float[] tmp=new float[ferrs.length];
			for(int i=0;i<tmp.length;i++)
				tmp[i]=ferrs[i]==null?Float.MAX_VALUE:Data.mean(ferrs[i]);
			int minidx=Data.minIdx(tmp);
			float e=tmp[minidx];
			fidx[fidx.length-1]=minidx;
			boolean b=true;
			while(hid>1){
				float eminus=Data.mean(getErrors(Data.selectColumns(X,fidx),Y,partitions,repetitions,hid-1));
				log("\n\t\t*** EMINUS="+eminus);
				if(eminus<e){
					hid--;
					e=eminus;
					b=false;
				}else
					break;
			}
			if(b){
				while(hid<maxhidden){
					float eplus=Data.mean(getErrors(Data.selectColumns(X,fidx),Y,partitions,repetitions,hid+1));
					log("\n\t\t*** EPLUS="+eplus);
					if(eplus<e){
						hid++;
						e=eplus;
					}else
						break;
				}
			}
			featidx.add(minidx);
			errs.add(e);
			hids.add(hid);
			lg.append("*******************\n");
			lg.append("\tIteration:   \t"+l+"\n");
			lg.append("\tTime:        \t"+new Date().toString()+"\n");
			lg.append("\tBest Feature:\tIndex="+minidx);
			if(featnames!=null && featnames.length>minidx)
				lg.append(",\tName="+featnames[minidx]);
			lg.append("\n\tError:     \t"+e+" ("+hid+" hidden nodes)\n");
			Data.writeFile(f,msg+"\n"+lg.toString());
			log(lg.toString());
			if(hid<maxhidden)
				hid++;
			msg="";
			for(int j=0;j<featidx.size();j++){
				msg+="\tfeat "+j+":\tidx="+featidx.get(j)+"\tname="+featnames[featidx.get(j)]+
				"\thid="+hids.get(j)+"\terr="+errs.get(j);
				if(j>0)
					msg+="\timproved by "+(-100f*(errs.get(j)-errs.get(j-1))/errs.get(j-1)+"% over last and "+(-100f*(errs.get(j)-errs.get(0))/errs.get(0)+"% overall"));
				msg+=(j<featidx.size()-1?"\n":"");
			}
		}
		
	}
	
	/** to be deleted soon  **/
	public static float[] getErrors(float[][] X,float[][] Y, int[] partitions, int rep,int hidden){
		final float[] result=new float[rep];
		final NeuralNetwork nen=new NeuralNetwork(Type.Regression,X[0].length,hidden,Y[0].length);
		for(int r=0;r<rep;r++){
			final int[] trainidx=Data.find(partitions,r,false);
			final int[] testidx=Data.find(partitions,r,true);
			nen.train(Data.select(X,trainidx),Data.select(Y,trainidx),true,"");
			result[r]=nen.getError(Data.select(X,testidx), Data.select(Y,testidx));
			System.out.print("rep "+(r+1)+"/"+rep+"=>"+result[r]+".. "+((r+1)%5==0?"\n":""));
			nen.clear();
		}
		return result;
	}
	
	public static float[] getErrors(Data d, int[] partitions, int rep,int hidden){
		final float[] result=new float[rep];
		final NeuralNetwork nen=new NeuralNetwork(d.getType(),d.getX()[0].length,hidden,d.getY()[0].length);
		for(int r=0;r<rep;r++){
			final int[] trainidx=Data.find(partitions,r,true);
			final int[] testidx=Data.find(partitions,r,false);
			nen.train(new Data(d.getType(),Data.select(d.getX(),trainidx),Data.select(d.getY(),
					trainidx),Data.copy(d.getXattr()),Data.copy(d.getYattr())),true,"");
			result[r]=nen.getError(Data.select(d.getX(),testidx), Data.select(d.getY(),testidx));
			System.out.print("rep "+(r+1)+"/"+rep+" "+trainidx.length+" train/"+testidx.length+" test =>"+result[r]+".. "+((r+1)%3==0?"\n":""));
			nen.clear();
		}
		System.out.println();
		return result;
	}

	/** propagates a new datapoint forward and backward - updates weights **/
	public synchronized void put(final float[] in, final float[] out, float lrate){
		if(out==null)
			return;
		for(int n=0;n<in.length;n++)
			l1[n]=in[n];
		l1[l1count]=1;//bias
		Data.mult(l1,l12,l2put);
		actf(l2put);
		l2put[l2count]=1;//bias
		Data.mult(l2put, l23,l3);
		for(int n=0;n<l3count;n++)
			d3[n]=l3[n]-out[n];		
		Data.mult(l23, d3,d2);
		for(int n=0;n<d2.length;n++)
			d2[n]=d2[n]*(float)(1-(l2put[n]*l2put[n]));
		for(int n=0;n<l1count+1;n++){
			final float[] row=l12[n];
			final float f=l1[n]*lrate;
			for(int i=0;i<l2count;i++)
				row[i]=row[i]-(d2[i]*f);
		}
		for(int n=0;n<l2count+1;n++){
			final float[] row=l23[n];
			final float f=l2put[n]*lrate;
			for(int i=0;i<l3count;i++)
				row[i]=row[i]-(d3[i]*f);
		}
	}
	
	public static final void predictLibSVMFormat(NeuralNetwork nen, File in, File out){
		try{
			Data d=Data.readLibSVM(nen.type, in,nen.xattr,nen.yattr);
			float[][] p=nen.get(d.getX());
			float[] py=new float[p.length];
			float[] ty=new float[p.length];
			BufferedWriter w=new BufferedWriter(new FileWriter(out));
			if(nen.getType()==Type.Regression){
				for(int n=0;n<p.length;n++){
					py[n]=p[n][0];
					ty[n]=d.getY()[n][0];
					w.write(py[n]+"\n");
				}
				w.close();
				log("Mean-Squared-Error:\t"+NeuralNetwork.getError(py,ty,nen.getType()));	
			}else{
				for(int n=0;n<p.length;n++){
					py[n]=p[n][0];
					ty[n]=d.getY()[n][0];
					w.write(nen.getYattr()[Data.maxIdx(p[n])]+"\n");
				}			
				w.close();
				log("Accuracy:\t"+100*(1f-nen.getError(d.getX(),d.getY()))+"%");	
			}
		}catch(Exception e){e.printStackTrace();}
	}

	/** get predictions for each row-vector of X **/
	public synchronized final float[][] get(float[][] X){
		final float[][] result=new float[X.length][l3count];
		for(int n=0;n<X.length;n++)
			get(X[n],result[n]);
		return result;
	}
	
	/** get predictions for each row-vector of X **/
	public synchronized final void get(float[][] X, float[][] result){
		for(int n=0;n<X.length;n++)
			get(X[n],result[n]);
	}
	
	/** get prediction for a single datapoint **/
	public synchronized final float[] get(final float[] in){
		final float[] out=new float[l3count];
		get(in,out);
		return out;
	}
	
	/** get prediction for a single datapoint **/
	public synchronized final void get(final float[] in, final float[] out){
		for(int n=0;n<l1count;n++)
			l1[n]=in[n];
		l1[l1count]=1;
		Data.mult(l1,l12,l2);
		actf(l2);
		l2[l2count]=1;
		Data.mult(l2, l23,out);
	}

	/** get the average error given input X and output Y **/
	public float getError(float[][] X, float[][] Y){
		double er=0;
		for(int n=0;n<Y.length;n++){
			float[] in=X[n];
			for(int i=0;i<in.length;i++)
				l1[i]=in[i];
			l1[l1count]=1;//bias
			Data.mult(l1,l12,l2put);
			actf(l2put);
			l2put[l2count]=1;//bias
			Data.mult(l2put, l23,l3);
			er+=getError(l3,Y[n],type);
		}
		er/=(double)Y.length;
		return (float)er;
	}
	
	/** 
	 * get error for prediction and truth - MSE for regression, binary for classification
	 * To make it to predict the optimal near optimal : We changed MSE function 
	 **/
	public static float getError(float[] y, float[] t, Type type){
		if(type==Type.Classification)
		{
			return Data.maxIdx(y)==Data.maxIdx(t)?0:1;
		}
		else
		{
			double er=0,e=0;
			for(int n=0;n<t.length;n++){
				e = t[n]-(y==null?0:y[n]);
				e = e*( 1 + 1.0/(1 + Math.exp(5*e)));
				er+=e*e;
			}
			er/=(double)t.length;
			return (float)er;
		}
	}
	
	/** apply activation function to v **/
	public void actf(float[] v){
		for(int n=0;n<v.length;n++)
			v[n]=Data.fastTanH(v[n]);
	}
	
	/** intitialize net with random weights **/
	public void clear(){
		for(int n=0;n<l1count+1;n++)
			for(int i=0;i<l2count;i++)
				l12[n][i]=.1f-(0.2f*(float)Math.random());
		for(int n=0;n<l2count+1;n++)
			for(int i=0;i<l3count;i++)
				l23[n][i]=0.1f-(0.2f*(float)Math.random());
	}
	
	/** create a piece of java sourcecode that will instatiate the given net **/
	public static String encodeJavaSRC(NeuralNetwork nen,String varname){
		try{
			StringBuilder s=new StringBuilder();
			ByteArrayOutputStream bo=new ByteArrayOutputStream();
			encode(nen, bo);
			byte[] b=bo.toByteArray();
			s.append("private static final Nen "+varname+"=");
			s.append("Nen.decode(new ByteArrayInputStream(new byte[]{");
			boolean first=true;
			for(int n=0;n<b.length;n++){
				if(first)
					first=false;
				else
					s.append(",");
				s.append(b[n]);
				if(n%30==0)
					s.append("\n\t");
			}
			s.append("}));\n");
			return s.toString();
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/** read a net from input stream **/
	public static NeuralNetwork decode(InputStream _in){
		try{
			DataInputStream in=new DataInputStream(_in);
			int t=in.readInt();
			int l1count=in.readInt();
			int l2count=in.readInt();
			int l3count=in.readInt();
			NeuralNetwork nen=new NeuralNetwork(t==1?Type.Regression:Type.Classification,l1count,l2count,l3count);
			int sxattrcount=in.readInt();
			StringBuilder sxattr=new StringBuilder();
			for(int n=0;n<sxattrcount;n++)
				sxattr.append(in.readChar());
			sxattr.trimToSize();
			nen.xattr=sxattr.toString().split(",");
			int syattrcount=in.readInt();
			StringBuilder syattr=new StringBuilder();
			for(int n=0;n<syattrcount;n++)
				syattr.append(in.readChar());
			syattr.trimToSize();
			nen.yattr=syattr.toString().split(",");

			for(int n=0;n<nen.l12.length;n++)
				for(int i=0;i<nen.l12[0].length;i++)
					nen.l12[n][i]=in.readFloat();
			for(int n=0;n<nen.l23.length;n++)
				for(int i=0;i<nen.l23[0].length;i++)
					nen.l23[n][i]=in.readFloat();
			
			in.close();
			return nen;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}

	/** write a net to output stream - for example a file **/
	public static void encode(NeuralNetwork nen, OutputStream _out){
		try{	
			DataOutputStream out=new DataOutputStream(_out);
			out.writeInt(nen.type==Type.Regression?1:-1);
			out.writeInt(nen.l1count);
			out.writeInt(nen.l2count);
			out.writeInt(nen.l3count);
			StringBuilder sxattr=new StringBuilder();
			boolean first=true;
			for(int n=0;n<nen.xattr.length;n++){
				if(first)
					first=false;
				else 
					sxattr.append(',');
				sxattr.append(nen.xattr[n]);
			}
			sxattr.trimToSize();
			out.writeInt(sxattr.length());
			out.writeChars(sxattr.toString());
			StringBuilder syattr=new StringBuilder();
			first=true;
			for(int n=0;n<nen.yattr.length;n++){
				if(first)
					first=false;
				else 
					syattr.append(',');
				syattr.append(nen.yattr[n]);
			}
			syattr.trimToSize();
			out.writeInt(syattr.length());
			out.writeChars(syattr.toString());
			for(int n=0;n<nen.l12.length;n++)
				for(int i=0;i<nen.l12[0].length;i++)
					out.writeFloat(nen.l12[n][i]);
			for(int n=0;n<nen.l23.length;n++)
				for(int i=0;i<nen.l23[0].length;i++)
					out.writeFloat(nen.l23[n][i]);
			out.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public NeuralNetwork clone(){
		try{
			final ByteArrayOutputStream bo=new ByteArrayOutputStream();
			NeuralNetwork.encode(this,bo);
			return NeuralNetwork.decode(new ByteArrayInputStream(bo.toByteArray()));
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public static void log(final String msg){
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public int getL1count() {
		return l1count;
	}

	public int getL2count() {
		return l2count;
	}

	public int getL3count() {
		return l3count;
	}

	/** a tiny network for guessing the number of hidden nodes **/
	private static final NeuralNetwork nen_hid_cls=NeuralNetwork.decode(new ByteArrayInputStream(new byte[]{0
			,0,0,1,0,0,0,3,0,0,0,3,0,0,0,1,0,0,0,5,0,49,0,44,0,50,0,44,0,51,0
			,0,0,2,0,114,0,48,65,85,60,-37,62,-107,91,-85,61,98,-30,67,-65,120,-3,-89,-65,-94,7,8,64,10,-31
			,17,-64,14,12,-24,-66,-32,-4,17,-66,-79,-83,-115,-63,22,81,-118,63,19,84,-5,-65,-116,-75,86,63,16,-78,127,-64
			,96,38,14,-65,-18,-21,-66,63,-17,-34,-19}));

	public static int guessHiddenCLS(int N, int D, int C){
		final float in_1=(float)(-1+(2*(Math.log(N)-3.4339871)/7.517486));
		final float in_2=(float)(-1+(2*(Math.log(D)-0.6931472)/5.0106354));
		final float in_3=(float)(-1+(2*(Math.log(C)-0.6931472)/2.5649493));
		float oh=nen_hid_cls.get(new float[]{in_1,in_2,in_3})[0];
		oh=(float)(0.0+(((oh+1f)/2f)*(3.871201)));
		return Math.min(100,Math.max(C,Math.min(2*D+Math.max(0, C-D),(int)Math.exp(oh))));
	}

}

