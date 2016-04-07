package davisql;

import java.io.RandomAccessFile;



public class MakeZooSchema {
	public static void main(String[] args) {
		try {
			/* FIXME: Put all binary data files in a separate subdirectory (subdirectory tree?) */
			/* FIXME: Should there not be separate Class static variables for the file names?
			 *        and just hard code them here?
			 */
			/* TODO: Should there be separate methods to checkfor and subsequently create each file
			 *       granularly, instead of a big bang all or nothing?
			 */
			RandomAccessFile schemataTableFile = new RandomAccessFile("Zoo_schema.schemata.tbl", "rw");
			RandomAccessFile tablesTableFile = new RandomAccessFile("Zoo_schema.table.tbl", "rw");
			RandomAccessFile columnsTableFile = new RandomAccessFile("Zoo_schema.columns.tbl", "rw");
			
	

			/*
			 *  Create the SCHEMATA table file.
			 *  Initially it has only one entry:
			 *      Zoo_schema
			 */
			// ROW 1: Zoo_schema.schemata.tbl
			schemataTableFile.writeByte("Zoo_schema".length());
			schemataTableFile.writeBytes("Zoo_schema");

			/*
			 *  Create the TABLES table file.
			 *  Remember!!! Column names are not stored in the tables themselves
			 *              The column names (TABLE_SCHEMA, TABLE_NAME, TABLE_ROWS)
			 *              and their order (ORDINAL_POSITION) are encoded in the
			 *              COLUMNS table.
			 *  Initially it has three rows (each row may have a different length):
			 */
			// ROW 1: Zoo_schema.tables.tbl
			tablesTableFile.writeByte("Zoo_schema".length()); // TABLE_SCHEMA
			tablesTableFile.writeBytes("Zoo_schema");
			tablesTableFile.writeByte("Zoo".length()); // TABLE_NAME
			tablesTableFile.writeBytes("Zoo");
			tablesTableFile.writeLong(0); // TABLE_ROWS

			/*
			 *  Create the COLUMNS table file.
			 *  Initially it has 11 rows:
			 */
			// ROW 1: Zoo_schema.columns.tbl
			columnsTableFile.writeByte("Zoo_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("Zoo_schema");
			columnsTableFile.writeByte("Zoo".length()); // TABLE_NAME
			columnsTableFile.writeBytes("Zoo");
			columnsTableFile.writeByte("Animal_ID".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("Animal_ID");
			columnsTableFile.writeInt(1); // ORDINAL_POSITION
			columnsTableFile.writeByte("int".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("int");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("PRI");

			// ROW 2: Zoo_schema.columns.tbl
			columnsTableFile.writeByte("Zoo_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("Zoo_schema");
			columnsTableFile.writeByte("Zoo".length()); // TABLE_NAME
			columnsTableFile.writeBytes("Zoo");
			columnsTableFile.writeByte("Name".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("Name");
			columnsTableFile.writeInt(2); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(20)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(20)");
			columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("YES");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 3: Zoo_schema.columns.tbl
			columnsTableFile.writeByte("Zoo_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("Zoo_schema");
			columnsTableFile.writeByte("Zoo".length()); // TABLE_NAME
			columnsTableFile.writeBytes("Zoo");
			columnsTableFile.writeByte("Sector".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("Sector");
			columnsTableFile.writeInt(3); // ORDINAL_POSITION
			columnsTableFile.writeByte("short".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("short");
			columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("YES");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");


		}
		catch(Exception e) {
			System.out.println(e);
		}

	}
}


