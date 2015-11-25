package hash.conflicts;

import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FindConflicts {

	public void find(String file, String table) throws BiffException, IOException, RowsExceededException, WriteException{
		   //read file and hashtable
		    File file_in = new File(file);
		    RandomAccessFile file_reader = new RandomAccessFile(file_in,"r");
		    
		    File table_in = new File(table);
			Workbook book = Workbook.getWorkbook(table_in);
			
			//find conflicts
			Sheet sheetap, sheetbkdr;
			int i =0;
			int j=0;
			String temps1, temps2=null;
			String temps3, temps4;
			String fileblock1, fileblock2;
			long templ1,templ2 = 0;
			long templ3,templ4 = 0;
			byte[] bb1 = new byte[Constant.blocksize];
			byte[] bb2 = new byte[Constant.blocksize];
			Node[] bkdrlist, aplist;
			Constant.TOTAL_CONFLICTS = 0;
			switch (Constant.HASH_METHOD)
			{
			case 0:
				sheetap = book.getSheet(0);
				aplist = generateArray(sheetap, Constant.nodenumlistap);
				
				while(i< Constant.totalblocks){
					//read item from the hashtable in order
					temps1 = sheetap.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
					templ1 = Long.parseLong(temps1);
					findHash(file_reader,aplist,Constant.nodenumlistap,templ1,i);
					i++;
				}
				book.close();
				break;
			case 1:
				sheetbkdr = book.getSheet(0);
				bkdrlist = generateArray(sheetbkdr, Constant.nodenumlistbkdr);
				
				while(i< Constant.totalblocks){
					//read item from the hashtable in order
					temps1 = sheetbkdr.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
					templ1 = Long.parseLong(temps1);
					findHash(file_reader, bkdrlist,Constant.nodenumlistbkdr,templ1,i);
					i++;
				}
				book.close();
				break;
			case 2:
				sheetbkdr = book.getSheet(0);
				sheetap = book.getSheet(1);
				bkdrlist = generateArray(sheetbkdr, Constant.nodenumlistbkdr);
				aplist = generateArray(sheetap, Constant.nodenumlistap);
				
				while(i< Constant.totalblocks){
					//read item from the hashtable in order
					temps1 = sheetbkdr.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
					templ1 = Long.parseLong(temps1);
					temps2 = sheetap.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
					templ2 = Long.parseLong(temps2);
					findHash2(file_reader, bkdrlist,Constant.nodenumlistbkdr,templ1, aplist, Constant.nodenumlistap,templ2,i);
					i++;
				}
				book.close();
				break;
			}
		
			double ratio = (double) Constant.TOTAL_CONFLICTS / Constant.totalblocks / 2; 
			System.out.println("The conflict block number is: "+Constant.TOTAL_CONFLICTS/2);
			System.out.println("The conflict ratio is: "+ratio);
			return;
	   }
     
	//generate node list according to hashtable
	public Node[]  generateArray(Sheet sheet, int[] nodenumlist){
	     int i = 0;
	     //initialize the nodenumlist[]
	     for (int j=0; j<Constant.PRIME;j++)
	   	     nodenumlist[j]=0;
	         Node[] nodelist = new Node[Constant.PRIME];
	    	 while (i< Constant.totalblocks){
	    	     String tempstr = sheet.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
	    	     long abst = Long.parseLong(tempstr);
	    	     int position = (int) (Math.abs(abst)%Constant.PRIME);                   //calculate the position of the hash value in the node list
	    	 	        
	    	     //put new node into the position calculated
	    	     Node tempnode = new Node();
	    	     tempnode.setBlocknum(i);
	    	     tempnode.setHashvalue(abst);
	    	     tempnode.next = null;
	    	     if(nodenumlist[position]==0){
	    	        nodelist[position] = tempnode;
	    	    }
	    	    else {
	    	        Node temp = nodelist[position];
 	    	        while(temp.getNext()!=null){
 	    	            temp = temp.getNext();
 	    	        }
 	    	        temp.setNext(tempnode);
	    	    }
	    	    nodenumlist[position]++;
	    	    i++;
	    	}
	    	     
			return nodelist;	 
	 }
	    
	public void findHash(RandomAccessFile file_reader, Node[] nodelist, int[] nodenumlist, long hashvalue, int blockn) throws IOException{
		int position =(int) (Math.abs(hashvalue)%Constant.PRIME);
		//if this position does not have a node
		if (nodenumlist[position]==0)
	        return;
	    //if this position has  nodes
	    Node tempnode = nodelist[position];
	    //String tempstr1 = Long.toString(hashvalue);
	    byte[] bb1 = new byte[Constant.blocksize];
	    byte[] bb2 = new byte[Constant.blocksize];
	    while(tempnode!=null){
	        long templong = tempnode.getHashvalue();
	        int tempblockn = tempnode.getBlocknum();
	        if((templong == hashvalue) && (tempblockn != blockn))
	        {
	        	file_reader.seek(blockn);
				file_reader.read(bb1);
				file_reader.seek(tempblockn);
				file_reader.read(bb2);
				if(!bb1.equals(bb2))
				{
					Constant.TOTAL_CONFLICTS++;
				}
	        }
	       	tempnode = tempnode.getNext();
	    }
		return;
	}

	public void findHash2(RandomAccessFile file_reader, Node[] nodelist1, int[] nodenumlist1, long hashvalue1, Node[] nodelist2, int[] nodenumlist2, long hashvalue2, int blockn) throws IOException{
		int position1 =(int) (Math.abs(hashvalue1)%Constant.PRIME);
		int position2 =(int) (Math.abs(hashvalue2)%Constant.PRIME);
		//if this position does not have a node
		if (nodenumlist1[position1]==0 || nodenumlist2[position2]==0)
	        return;
	    //if this position has  nodes
		Node tempnode1 = nodelist1[position1];
	    //String tempstr1 = Long.toString(hashvalue);
	    byte[] bb1 = new byte[Constant.blocksize];
	    byte[] bb2 = new byte[Constant.blocksize];
	    while(tempnode1!=null){
	        long templong1 = tempnode1.getHashvalue();
	        int tempblockn1 = tempnode1.getBlocknum();
	        //find same hash value in the first hash list
	        if(templong1 == hashvalue1 && tempblockn1 != blockn)
	        {
	        	Node tempnode2 = nodelist2[position2];
	        	while(tempnode2 != null)
		        {
	        		long templong2 = tempnode2.getHashvalue();
	    	        int tempblockn2 = tempnode2.getBlocknum();
	    	        if((templong2 == hashvalue2) && (tempblockn1 ==tempblockn2) && (tempblockn2 !=blockn))
	    	        {
	    	        	file_reader.seek(blockn);
	    				file_reader.read(bb1);
	    				file_reader.seek(tempblockn1);
	    				file_reader.read(bb2);
	    				if(!bb1.equals(bb2))
	    				{
	    					Constant.TOTAL_CONFLICTS++;
	    				}
	    	        }
	    	        tempnode2 = tempnode2.getNext();
		        }
	        }
	        tempnode1 = tempnode1.getNext();
	    }
		return;
	}
}
