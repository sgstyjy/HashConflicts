package hash.conflicts;

public class Constant {
	public static String file="";
	public static int HASH_METHOD = 2;
	public static String hashtable="";
	public static String hashtable2="";
	public static String compareresult="";
	public static int blocksize=4*1024;
	public static int COLUMNS = 10000;
	public static int totalblocks1=0;
	public static int totalblocks2=0;
	public static int PRIME = 10007;      //one big prime number, it is used for deciding the node list length
	public static int[]  nodenumlistbkdr = new int[Constant.PRIME];     //the node number of each position in the list for the BKDR table
	public static int[]  nodenumlistap = new int[Constant.PRIME];       //the node number of each position in the list for the AP table
	public static int similar = 0;  //the similar block number
}
