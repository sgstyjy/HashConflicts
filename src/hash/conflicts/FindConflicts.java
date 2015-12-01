package hash.conflicts;

import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

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
			String temps1, temps2=null;
			long templ1,templ2 = 0;
			
			Node[] bkdrlist, aplist;
			System.out.println("The block size is: "+ Constant.blocksize);
			Constant.TOTAL_CONFLICTS = 0;
			switch (Constant.HASH_METHOD)
			{
			case 0:
				System.out.println("Coming in the case 0 of FindConflicts()!");
				sheetap = book.getSheet(0);
				aplist = generateArray(sheetap, Constant.nodenumlistap);
				
				while(i< Constant.totalblocks){
					//read item from the hashtable in order
					temps1 = sheetap.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
					templ1 = Long.parseLong(temps1);
					findHash(file_reader,aplist,Constant.nodenumlistap,templ1,i);
					i++;
				}
				System.out.println("The total blocks are compared: "+ i);
				book.close();
				break;
			case 1:
				System.out.println("Coming in the case 1 of FindConflicts()!");
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
				System.out.println("The total blocks are compared: "+ i);
				break;
			case 2:
				System.out.println("Coming in the case 2 of FindConflicts()!");
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
				System.out.println("The total blocks are compared: "+ i);
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
	    //if(blockn < 10)
	    	//System.out.println("The original conflict number is: "+ Constant.TOTAL_CONFLICTS);
	    byte[] bb1 = new byte[Constant.blocksize];
	    byte[] bb2 = new byte[Constant.blocksize];
	    while(tempnode!=null){
	        long templong = tempnode.getHashvalue();
	        int tempblockn = tempnode.getBlocknum();
	        if((templong == hashvalue) && (tempblockn != blockn))
	        {
	        	file_reader.seek(blockn*Constant.blocksize);
				file_reader.read(bb1);
				String str1 = new String(bb1);

				file_reader.seek(tempblockn*Constant.blocksize);
				file_reader.read(bb2);
				String str2 = new String(bb2);

				if(!str1.equals(str2))
				{
					Constant.TOTAL_CONFLICTS++;
				}
	        }
	       	tempnode = tempnode.getNext();
	    }
		return;
	}

	public void findHash2(RandomAccessFile file_reader, Node[] nodelistap, int[] nodenumlistap, long hashvalueap, Node[] nodelistbkdr, int[] nodenumlistbkdr, long hashvaluebkdr, int blocknum) throws IOException{
		int positionap =(int) (Math.abs(hashvalueap)%Constant.PRIME);
		int positionbkdr =(int) (Math.abs(hashvaluebkdr)%Constant.PRIME);
		//if this position does not have a node
		if (nodenumlistap[positionap]==0 || nodenumlistbkdr[positionbkdr]==0)
	        return;

	    byte[] bb1 = new byte[Constant.blocksize];
	    byte[] bb2 = new byte[Constant.blocksize];
	    String str1=null;
	    String str2=null;
	    
	    Node tempnodeap = nodelistap[positionap];
	    while(tempnodeap!=null){
	        long templongap = tempnodeap.getHashvalue();
	        int tempblocknap = tempnodeap.getBlocknum();
	        //find same hash value in the first hash list
	        if(templongap == hashvalueap && tempblocknap != blocknum)
	        {
	        	Node tempnodebkdr = nodelistbkdr[positionbkdr];
	        	while(tempnodebkdr != null)
		        {
	        		long templongbkdr = tempnodebkdr.getHashvalue();
	    	        int tempblocknbkdr = tempnodebkdr.getBlocknum();
	    	        //if the same  block in the bkdr link is also same as the bkdr hash
	    	        if((templongbkdr == hashvaluebkdr) && (tempblocknap ==tempblocknbkdr) && (tempblocknbkdr !=blocknum))
	    	        {
	    	        	file_reader.seek(blocknum*Constant.blocksize);
	    				file_reader.read(bb1);
	    				str1 = new String(bb1);
	    				
	    				file_reader.seek(tempblocknap*Constant.blocksize);
	    				file_reader.read(bb2);
	    				str2 = new String(bb2);

	    				if( !str1.equals(str2) )
	    				{
	    					Constant.TOTAL_CONFLICTS++;
	    				}
	    	        }
	    	        tempnodebkdr = tempnodebkdr.getNext();
		        }
	        }
	        tempnodeap = tempnodeap.getNext();
	    }
		return;
	}
}
