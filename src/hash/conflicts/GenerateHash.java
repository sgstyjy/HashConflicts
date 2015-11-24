package hash.conflicts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.JxlWriteException;

public class GenerateHash {

	public int generater (String file, String hashtable) throws IOException, JxlWriteException, JXLException{
		//according to the hash method to generate corresponding hash table
		//the input image file
		File file_in = new File(file);
		InputStream reader = new FileInputStream(file_in);
		
		//the output hashtable
		File file_out = new File(hashtable);
		OutputStream writer = new FileOutputStream(file_out);
		WritableWorkbook workbook = Workbook.createWorkbook(writer);
		WritableSheet sheetap,sheetbkdr;
		AP aphasher;
		BKDR bkdrhasher;
		String temp1, temp2;
		Label tempcell1, tempcell2;
		int blocknum = 0;
		int position = 0;
		int size = reader.available();     //the total image size in byte
		byte[] bb = new byte[Constant.blocksize];
		String temp = null;
		long bkdrabs = 0;
		long apabs = 0;
		switch(Constant.HASH_METHOD)
		{
		case 0: 
			 	sheetap = workbook.createSheet("AP",0);
			 	aphasher = new AP();
			 
				//call hash functions		
				while(position<size){
				     	//special tackle the last block
							if((size-position)<Constant.blocksize){
										byte[] lastbf = new byte[(size-position)];
										reader.read(lastbf);
										temp = new String(lastbf);
										apabs = aphasher.aphash(temp);	    	
										 temp2 = Long.toString(apabs);
										 tempcell2 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp2);
										sheetap.addCell(tempcell2);
										break;
							}
							reader.read(bb);
							temp = new String (bb);
							apabs = aphasher.aphash(temp);	    	
							 temp2 = Long.toString(apabs);
							 tempcell2 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp2);
							sheetap.addCell(tempcell2);
							position += Constant.blocksize;
							blocknum++;
				}
			break;
		case 1:
		    sheetbkdr = workbook.createSheet("BKDR",0);
		    bkdrhasher = new BKDR();
		    
			//call hash functions		
			while(position<size){
			     	//special tackle the last block
						if((size-position)<Constant.blocksize){
									byte[] lastbf = new byte[(size-position)];
									reader.read(lastbf);
									temp = new String(lastbf);
									bkdrabs = bkdrhasher.bkdrhash(temp);   	
									 temp1 = Long.toString(bkdrabs);
									 tempcell1 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp1);
									sheetbkdr.addCell(tempcell1);
									break;
						}
						reader.read(bb);
						temp = new String (bb);
						bkdrabs = bkdrhasher.bkdrhash(temp);	    	
						 temp1 = Long.toString(bkdrabs);
						 tempcell1 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp1);
						sheetbkdr.addCell(tempcell1);
						position += Constant.blocksize;
						blocknum++;
			}
			break;
		case 2:
			sheetbkdr = workbook.createSheet("BKDR",0);
			 sheetap = workbook.createSheet("AP",1);
			 bkdrhasher = new BKDR();
			aphasher = new AP();
			
			//call hash functions		
			while(position<size){
			     	//special tackle the last block
						if((size-position)<Constant.blocksize){
									byte[] lastbf = new byte[(size-position)];
									reader.read(lastbf);
									temp = new String(lastbf);
									bkdrabs = bkdrhasher.bkdrhash(temp);
									apabs = aphasher.aphash(temp);	    	
									 temp1 = Long.toString(bkdrabs);
									 temp2 = Long.toString(apabs);
									 tempcell1 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp1);
									 tempcell2 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp2);
									sheetbkdr.addCell(tempcell1);
									sheetap.addCell(tempcell2);
									break;
						}
						reader.read(bb);
						temp = new String (bb);
						bkdrabs = bkdrhasher.bkdrhash(temp);
						apabs = aphasher.aphash(temp);	    	
						 temp1 = Long.toString(bkdrabs);
						 temp2 = Long.toString(apabs);
						 tempcell1 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp1);
						 tempcell2 = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, temp2);
						sheetbkdr.addCell(tempcell1);
						sheetap.addCell(tempcell2);
						position += Constant.blocksize;
						blocknum++;
			}
			break;
		}
		//System.out.println("Total block: "+blocknum);
		workbook.write();
		workbook.close();
		reader.close();
		return blocknum;
	}
}