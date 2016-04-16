package davisql;

import java.io.RandomAccessFile;

public class DatabaseOperations {

	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	/**
	 * formatTableEdge: create edges for tables
	 */
	 private static void formatTableEdge(String s, int edgeLength, int distance) {
	    	System.out.print("+");
			System.out.print(line("-", edgeLength));
			System.out.println("+");
			System.out.println("|" + " " + s + line(" ",distance) + "|");
			System.out.print("+");
			System.out.print(line("-", edgeLength));
			System.out.println("+");
	  }
	
	private static void displayAllSchemas() {
		try {
			RandomAccessFile schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
			formatTableEdge("SCHEMA_NAME", 22, 10);
			int length = (int) schemataTableFile.length();
			//System.out.print(length);
			while(length > 0) {
				
				byte varcharLength = schemataTableFile.readByte();
				length -= (int)varcharLength + 1;  // Each time use file length to minus (varcharLength + 1), 1 denotes schema length;
				System.out.print("|" + " ");
				int space = 0;
				for(int i = 0; i < varcharLength; i++) {
					if (varcharLength > "SCHEMA_NAME".length()) {
						space = 10 - (varcharLength - "SCHEMA_NAME".length());
					} else {
						space = 10 + ("SCHEMA_NAME".length() -varcharLength);
					}
					System.out.print((char)schemataTableFile.readByte());
				}
				System.out.print(line(" ",space) + "|");
				System.out.println();
			}
			System.out.print("+");
			System.out.print(line("-", 22));
			System.out.println("+");
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
		

	}

}
