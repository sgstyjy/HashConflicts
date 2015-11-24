package hash.conflicts;

import java.io.IOException;

import jxl.JXLException;
import jxl.write.biff.JxlWriteException;

public class HashConflicts {

	public static void main(String[] args) throws JxlWriteException, IOException, JXLException {
		/*
		 * @para file,  the original file data
		 * @para hash method,  the conflicts of which hash function you want to research, ap, bkdr, or double
		 * @para blocksize，      in the unit of KB 
		 */
		Constant.file=args[0];
		System.out.println("The input file name is: "+Constant.file);
		String hashmethod=args[1];
		System.out.println("The hash function is: "+hashmethod);
		if(hashmethod.equals("ap"))
			Constant.HASH_METHOD = 0;
		else if(hashmethod.equals("bkdr"))
			Constant.HASH_METHOD = 1;
		else if(hashmethod.equals("double"))
			Constant.HASH_METHOD = 2;
		int size = Integer.parseInt(args[2]);
		System.out.println("The blocksize is: "+size+"K");
		Constant.blocksize=size*1024;
		//build the name of hashtables
		StringBuilder strbuilder1 = new StringBuilder();
		String file1part = Constant.file.split("\\.")[0];    //cannot use point directly, must use "\\" before it
		strbuilder1.append(file1part);
		switch(Constant.HASH_METHOD)
		{
		case 0: 
			strbuilder1.append("_ap.xls");
			break;
		case 1:
			strbuilder1.append("_bkdr.xls");
			break;
		case 2:
			strbuilder1.append("_double.xls");
			break;
		}
		Constant.hashtable=strbuilder1.toString();
		System.out.println("The name of hashtable is: "+Constant.hashtable);
		
		//the start time
		Long starttime = System.currentTimeMillis();
		//System.out.println("The start time is: "+starttime);
		
		//generate the hashtables
		Long starttime_hash1 = System.currentTimeMillis();
		GenerateHash hashgenerater = new GenerateHash();  
		Constant.totalblocks1 = hashgenerater.generater(Constant.file,Constant.hashtable);
		System.out.println("The  total block numbers of the first image  are: "+Constant.totalblocks1);
		Long endtime_hash1 = System.currentTimeMillis();
		Long hashtime1=endtime_hash1-starttime_hash1;
		System.out.println("The hash time of first image is:"+hashtime1);
		
		Long starttime_hash2 = System.currentTimeMillis();
		Constant.totalblocks2  = hashgenerater.generater(Constant.file2,Constant.hashtable2);
		System.out.println("The  total block numbers of the second image  are: "+Constant.totalblocks2);
		Long endtime_hash2 = System.currentTimeMillis();
		Long hashtime2=endtime_hash2-starttime_hash2;
		System.out.println("The hash time of second image is:"+hashtime2);
				
		//compare hashtables
		Long starttime_compare = System.currentTimeMillis();
		CompareHash comparer = new CompareHash();
		comparer.fastCompareHash(Constant.hashtable1, Constant.hashtable2);
		Long endtime_compare = System.currentTimeMillis();
		Long comparetime=endtime_compare-starttime_compare;
		System.out.println("The similar size is: "+ Constant.similar*size/1024+"MB");
		System.out.println("The compare time is:"+comparetime);
		
		//the end time
		Long endtime = System.currentTimeMillis();
		Long temp = endtime-starttime;
		Long duration;
		if(hashtime1>hashtime2)
			   duration = temp-hashtime2;
		else
			   duration = temp-hashtime1;
		System.out.println("The total time is:"+duration);
	}

}
