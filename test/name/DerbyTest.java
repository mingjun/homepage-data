package name;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import org.junit.Test;

public class DerbyTest {
	//@Test
	public void testDB() {
		
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String protocol = "jdbc:derby:";
		try {
			Connection c = DriverManager.getConnection(protocol + "/home/mingjun/test/wwwDB;create=true", null);
			// c.nativeSQL("select * from visits");
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("select num, ip from visits");
			
			while(rs.next()) {
				System.out.println(rs.getInt("num"));
				System.out.println(rs.getString("ip"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
//	@Test 
	public void testFile() {
		String LOG_FILE = "/home/mingjun/www/visit.log";
		File f = new File(LOG_FILE);
		System.out.println(f.exists());
		try {
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(LOG_FILE, true)));
		out.println("ni daye");
		out.flush();
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		while(null != (line=br.readLine())) {
			System.out.println(line);
		}
		} catch(IOException e) {
			//
		}
	}

}
