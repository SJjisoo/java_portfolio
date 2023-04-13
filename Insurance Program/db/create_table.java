package db;

import java.sql.*;

public class create_table {
	public create_table() {
		String createdb = "create database if not exists customer";
		String createadmin = "create table if not exists admin (name varchar(20) not null, passwd varchar(20) not null, position varchar(20), jumin char(14), inputDate date, primary key(name, passwd))";
		String createcontract = "create table if not exists contract (customerCode char(7) not null, contractName varchar(20) not null, regPrice int, regDate date not null, monthPrice int, adminName varchar(20) not null)";
		String createcustomer = "create table if not exists customer (code char(7) not null, name varchar(20) not null, birth date, tel varchar(20), address varchar(100), company varchar(20), primary key(code, name))";
		
		Connection con = Driver_connect.makeConnection("");
		
		try {
			Statement st = con.createStatement();
			st.executeUpdate(createdb);
			st.executeUpdate("use customer");
			st.executeUpdate(createadmin);
			st.executeUpdate(createcontract);
			st.executeUpdate(createcustomer);
			System.out.println("만들기 성공");
		} catch (SQLException e) {
			System.out.println("SQL 오류!!");
		}
	}
	
	public static void main(String[] args) {
		new create_table();
		new insert_txt();
	}
}
