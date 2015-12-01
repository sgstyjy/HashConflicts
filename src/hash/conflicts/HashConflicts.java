package hash.conflicts;

import java.io.IOException;

import jxl.JXLException;
import jxl.write.biff.JxlWriteException;

public class HashConflicts {

	public static void main(String[] args) throws JxlWriteException, IOException, JXLException {
		/*
		 * @para file,  the original file data
		 * @para hash method,  the conflicts of which hash function you want to research, ap, bkdr, or double
		 * @para blocksize,    in the unit of KB 
		 */
		Constant.file=args[0];
		System.out.println("The input file name is: "+Constant.file);
		
		String hashmethod=args[1];
		if(hashmethod.equals("ap"))
			Constant.HASH_METHOD = 0;
		else if(hashmethod.equals("bkdr"))
			Constant.HASH_METHOD = 1;
		else if(hashmethod.equals("double"))
			Constant.HASH_METHOD = 2;
		System.out.println("The hash method is: "+Constant.HASH_METHOD);
		
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
		
		Long START_TIME = System.currentTimeMillis();
		//generate the hashtables
		GenerateHash hashgenerater = new GenerateHash();  
		hashgenerater.generater(Constant.file,Constant.hashtable);
		Long END_TIME = System.currentTimeMillis();
		Long Duration = END_TIME - START_TIME;
		System.out.println("The hash time is:"+ Duration);
		FindConflicts finder = new FindConflicts();
		
		Long START_TIME1 = System.currentTimeMillis();
		finder.find(Constant.file, Constant.hashtable);
		Long END_TIME1 = System.currentTimeMillis();
		Long Duration1 = END_TIME1 - START_TIME1;
		System.out.println("The compare time is:"+ Duration1);
		
	}
}
