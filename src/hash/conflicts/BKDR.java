package hash.conflicts;

public class BKDR {
	public long bkdrhash(String str){
		long seed = 131;//31131131313131131313etc..
		long hash = 0;
		for(int i = 0; i < str.length(); i++)
				hash = (hash * seed) + str.charAt(i);
		return hash;
}
}
