package davisql;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.StringTokenizer;



/**
 * @author Mingyuan Wang
 * @version 1.0
 * <b>This is an example of how to read/write binary data files using RandomAccessFile class</b>
 *
 */
public class DavisBaseLite {
	// This can be changed to whatever you like
	static String prompt = "davisql> ";

    public static void main(String[] args) {
		/* Display the welcome splash screen */
		splashScreen();
		
		/* 
		 *  Manually create a binary data file for the single hardcoded 
		 *  table. It inserts 5 hardcoded records. The schema is inherent 
		 *  in the code, pre-defined, and static.
		 *  
		 *  An index file for the ID field is created at the same time.
		 */

		/* 
		 *  The Scanner class is used to collect user commands from the prompt
		 *  There are many ways to do this. This is just one.
		 *
		 *  Each time the semicolon (;) delimiter is entered, the userCommand String
		 *  is re-populated.
		 */
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand; // Variable to collect user input from the prompt
		String active_schema = "information_schema"; // default schema is system schema: information_schema;
		HashMap<String, ArrayList<String>> schema_table_list = new HashMap<String, ArrayList<String>>(); // to keep tables of all schemas
		schema_table_list.put(active_schema, new ArrayList<String>());
		schema_table_list.put("Zoo_schema", new ArrayList<String>());
		// tables which already exist in all schemas.
		schema_table_list.get(active_schema).add("SCHEMATA");
		schema_table_list.get(active_schema).add("TABLES");
		schema_table_list.get(active_schema).add("COLUMNS");
		schema_table_list.get(active_schema).add("Zoo");
		schema_table_list.get("Zoo_schema").add("Zoo");
		

		do {  // do-while !exit
			System.out.print(prompt);
			userCommand = scanner.next().trim();
			String[] command = userCommand.split("[ ]");  // delimiter: space
			ArrayList<String> schemaList = getAllSchemaNames();
			if (command[0].equals("SHOW") && command[1].equals("SCHEMAS")){  // SHOW SCHEMAS command
				displayAllSchemas();
			}else if (command[0].equals("USE")) {    // USE schema_name command
				active_schema = command[1];
				//System.out.print(active_schema);
			}else if (command[0].equals("CREATE") && command[1].equals("SCHEMA")) { // CREATE SCHEMA command
				if (!schema_table_list.containsKey(command[2])){
					writeNewSchemaIntoSchemaTable(command[2]);
					schema_table_list.put(command[2], new ArrayList<String>());
				}
//				System.out.print(command[2]);
//				System.out.print(schemaList.contains(command[2]));
			}else if (command[0].equals("SHOW") && command[1].equals("TABLES")){  // SHOW TABLES command
				displayTablesInSelectedSchema(active_schema, schema_table_list);
			}else if (command[0].equals("CREATE") && command[1].equals("TABLE")){  // CREATE TABLE command
				//System.out.print(command[2]);
				String parameters = userCommand.substring(15 + command[2].length(), userCommand.length()-1);
				if (!schema_table_list.get(active_schema).contains(command[2])){
					createTableToSelectedSchema(command[2], parameters, active_schema);
					schema_table_list.get(active_schema).add(command[2]);
				}
			}else if (command[0].equals("INSERT") && command[1].equals("INTO") && command[2].equals("TABLE")){ // INSERT INTO TABLE command
				if (schema_table_list.get(active_schema).contains(command[3])) {
					
				}
			}
		} while(!userCommand.equals("exit"));
		System.out.println("Exiting...");
		
    } /* End main() method */

    
//  ===========================================================================
//  STATIC METHOD DEFINTIONS BEGIN HERE
//  =========================================================================== 
    
    private static void displayTablesInSelectedSchema(String active_schema, HashMap<String, ArrayList<String>> schema_table_list) {
		ArrayList<String> table_list = schema_table_list.get(active_schema);
		formatTableEdge("TABLE_NAME", 14, 3);
		for (String table: table_list) {
			System.out.print("|" + " ");
			int tableLen = table.length();
			int tableNameLen = "TABLE_NAME".length();
			int space;
			if (tableLen> tableNameLen) {
				space = 3 - (tableLen - tableNameLen);
			}else {
				space = 3 + (tableNameLen - tableLen);
			}
			System.out.print(table + line(" ",space) + "|");
			System.out.println();
		}
		System.out.println("+" + line("-", 14) + "+");
   }


	private static String[] parseTableParameters(String parameters) {
    	String[] list = parameters.split(",");
    	return list;
    }
    private static void createTableToSelectedSchema(String tablename,
			String command, String schema) {
		// create table in selected schema and its index files
    	
    	try {
    		RandomAccessFile systemTableFile = new RandomAccessFile("information_schema.table.tbl", "rw");
    		RandomAccessFile systemColumnFile = new RandomAccessFile("information_schema.columns.tbl","rw");
    		RandomAccessFile tableFile = new RandomAccessFile(schema + "." + tablename + ".tbl","rw");
    		String[] lines = parseTableParameters(command);
//    		for (int i = 0; i< lines.length;i++) {
//    			System.out.print(Integer.toString(i) + lines[i]);
//    		}
    		//System.out.print(systemTableFile.length());  105
    		// write table schema to information_schema.table.tbl	
    		systemTableFile.seek(systemTableFile.length());  // start writing from the end of last revised file
    		systemTableFile.writeByte(schema.length());   // Table schema
    		systemTableFile.writeBytes(schema);
    		systemTableFile.writeByte(tablename.length()); // Table name
    		systemTableFile.writeBytes(tablename);
    		systemTableFile.writeLong(0);  // Table rows, default:0 (Insertion of rows will change this number)
    		
    		// write columns to information_schema.columns.tbl
    		/*
    		 * CREATE TABLE table_name (
    		 * column_name1 data_type(size) [primary key|not null],
    		 * column_name2 data_type(size) [primary key|not null],
    		 * ....
    		 * );
    		 */
    		for (int i = 1 ; i < lines.length + 1; i ++) {
    			systemColumnFile.seek(systemColumnFile.length()); // start writing from the end of last revised file
    			systemColumnFile.writeByte(schema.length());   // Table schema
    			systemColumnFile.writeBytes(schema);
    			systemColumnFile.writeByte(tablename.length());   // Table name
    			systemColumnFile.writeBytes(tablename);
    			String[] tmp = lines[i-1].trim().split("[ ]");
//    			for (int j = 0; j < tmp.length; j++) {
//    				System.out.println(Integer.toString(j) + tmp[j]);
//    			}
    			systemColumnFile.writeByte(tmp[0].length());  // Column name
    			systemColumnFile.writeBytes(tmp[0]);
    			systemColumnFile.writeInt(i);  // Ordinal position 
    			systemColumnFile.writeByte(tmp[1].length()); // Column type
    			systemColumnFile.writeBytes(tmp[1]);
    			//System.out.print(tmp[0]);
    			if (tmp.length == 4) {
    				if ((tmp[2].equals("PRIMARY") && tmp[3].equals("KEY")) || (tmp[2].equals("primary") && tmp[3].equals("key"))){
    					systemColumnFile.writeByte("NO".length());  // IS_Nullable (Not NULL)
    					systemColumnFile.writeBytes("NO");
    					systemColumnFile.writeByte("PRI".length()); // Column key (PRI)
    					systemColumnFile.writeBytes("PRI");
    				}else if ((tmp[2].equals("NOT") && tmp[3].equals("NULL")) ||(tmp[2].equals("not") && tmp[3].equals("null"))){
    					systemColumnFile.writeByte("NO".length()); // IS_Nullable (NOT NULL)
    					systemColumnFile.writeBytes("NO");
    					systemColumnFile.writeByte("".length());  // Column key (empty)
    					systemColumnFile.writeBytes("");
    				}
    			}else {
    				systemColumnFile.writeByte("YES".length()); // IS_Nullable (Can be Null)
    				systemColumnFile.writeBytes("YES");
    				systemColumnFile.writeByte("".length());  // Column key (empty)
    				systemColumnFile.writeBytes("");
    			}
    		}
    		

    	}
    	catch(Exception e) {
    		System.out.print(e);
    	}
		
	}
 
    private static void writeNewSchemaIntoSchemaTable(String newSchema) {
    	try {
    		RandomAccessFile schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
    		ArrayList<String> schemaList = getAllSchemaNames();
    		if (!schemaList.contains(newSchema)) {
    			schemataTableFile.seek(schemataTableFile.length());
    			schemataTableFile.writeByte(newSchema.length());
    			schemataTableFile.writeBytes(newSchema);
    		}else {
    			System.out.println();
    		}
    	} 
    	catch(Exception e) {
    		System.out.print(e);
    	}
    }
    
    private static ArrayList<String> getAllSchemaNames() {
    	try {
    		RandomAccessFile schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
    		ArrayList<String> schemaList = new ArrayList<String>();
    		int length = (int) schemataTableFile.length();
    		while(length > 0) {
				byte varcharLength = schemataTableFile.readByte();
				length -= (int)varcharLength + 1;  // Each time use file length to minus (varcharLength + 1), 1 denotes schema length;
				StringBuilder sb = new StringBuilder(varcharLength);
				for(int i = 0; i < varcharLength; i++) {
					sb.append((char)schemataTableFile.readByte());
				}
				schemaList.add(sb.toString());
			}
    		return schemaList;
    	} 
    	catch(Exception e) {
    		return null;
    	}
    	
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
	
	/**
	 *  Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("\tversion;       Show the program version.");
		System.out.println("\thelp;          Show this help information");
		System.out.println("\texit;          Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*",80));
	}
	
	/**
	 *  Display the welcome "splash screen"
	 */
	public static void splashScreen() {
		System.out.println(line("*",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}

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
	 * @param num The number of newlines to be displayed to <b>stdout</b>
	 */
	public static void newline(int num) {
		for(int i=0;i<num;i++) {
			System.out.println();
		}
	}
	
	public static void version() {
		System.out.println("DavisBaseLite v1.0\n");
	}	
}


