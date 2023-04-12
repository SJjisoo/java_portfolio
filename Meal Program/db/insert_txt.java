package db;

import java.io.*;
import java.sql.*;
import java.util.*;

public class insert_txt {
	public insert_txt() {
		Connection con = Driver_connect.makeConnection("meal");
		PreparedStatement psmt = null;
		
		String text [] = {
				"insert into member values (?, ?, ?)",
				"insert into cuisine values (?, ?)",
				"insert into meal values (?, ?, ?, ?, ?, ?)",
				"insert into orderlist values (?, ?, ?, ?, ?, ?, ?)"
		};
		
		String name [] = {"member", "cuisine", "meal", "orderlist"};
		
		for(int i=0; i<name.length; i++) {
			try {
				Scanner fsc = new Scanner(new FileInputStream("C:\\Users\\userpc\\eclipse-workspace\\customer\\meal\\" + name[i] + ".txt"));
				StringTokenizer st = new StringTokenizer(fsc.nextLine(), "\t");
				
				String line [] = new String[st.countTokens()];
				psmt = con.prepareStatement(text[i]);
				
				while(fsc.hasNext()) {
					st = new StringTokenizer(fsc.nextLine(), "\t");
					for(int j=0; j<line.length; j++) {
						line[j] = st.nextToken();
						psmt.setString(j+1, line[j]);
					}
					psmt.executeUpdate();
				}
			} catch (IOException e) {
				System.out.println("파일 오류!!!");
			} catch (SQLException e) {
				System.out.println("SQL 오류!!!");
			}
		}
	}
}
