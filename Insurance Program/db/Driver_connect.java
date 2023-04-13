package db;

import java.sql.*;

public class Driver_connect {
	public static Connection makeConnection(String dbname) {
		String url;
		if(dbname == "") {			
			url = "jdbc:mysql://localhost";
		}
		else {
			url = "jdbc:mysql://localhost/" + dbname;
		}
		String id = "root";
		String pass = "1234";
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			con = DriverManager.getConnection(url, id, pass);
			System.out.println("테이터베이스 연결 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이브 찾을 수 없습니다.");
		} catch (SQLException e) {
			System.out.println("연결 실패!!");
		}
		return con;
	}
}
