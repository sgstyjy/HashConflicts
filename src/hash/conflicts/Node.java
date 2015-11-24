package hash.conflicts;

public class Node {
    int blocknum;  //the block number
    long hashvalue;   //the hash value of this block
    Node next;
    
    public void setBlocknum(int bn){
 	        this.blocknum = bn;
    }
    public void setHashvalue(long hv){
 	        this.hashvalue = hv;
    }
    public void setNext(Node  nt){
 	        this.next = nt;
    }
    public int getBlocknum(){
 	         return this.blocknum;
    }
    public long getHashvalue(){
 	        return this.hashvalue;
    }
    public Node getNext(){
 	        return this.next;
    }
}
