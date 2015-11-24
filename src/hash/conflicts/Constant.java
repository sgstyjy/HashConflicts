package hash.conflicts;

public class Constant {
	public static String file="";
	public static int HASH_METHOD = 2;
	public static String hashtable="";
	public static int blocksize=4*1024;
	public static int COLUMNS = 10000;
	public static int totalblocks=0;
	public static int TOTAL_CONFLICTS = 0;
	public static int PRIME = 10007;      //one big prime number, it is used for deciding the node list length
	public static int[]  nodenumlistbkdr = new int[Constant.PRIME];     //the node number of each position in the list for the BKDR table
	public static int[]  nodenumlistap = new int[Constant.PRIME];       //the node number of each position in the list for the AP table
}
