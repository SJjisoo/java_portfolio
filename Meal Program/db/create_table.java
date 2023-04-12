package db;

import java.sql.*;

public class create_table {
	public create_table() {
		String createdb = "create database if not exists meal";
		String createmember = "create table if not exists member (memberNo int not null primary key auto_increment, memberName varchar(20), passwd varchar(4))";
		String createcuisine = "create table if not exists cuisine (cuisineNo int not null primary key auto_increment, coisineName varchar(10))";
		String createmeal = "create table if not exists meal (mealNo int not null primary key not null auto_increment, cuisineNo int, mealName varchar(20), price int, maxCount int, todayMeal tinyint(1))";
		String createorderlist = "create table if not exists orderlist (orderNo int primary key not null auto_increment, cuisineNo int, mealNo int, memberNo int, orderCount int, amount int, orderDate datetime)";
		
		Connection con = Driver_connect.makeConnection("");
		
		try {
			Statement st = con.createStatement();
			st.executeUpdate(createdb);
			st.executeUpdate("use meal");
			st.executeUpdate(createmember);
			st.executeUpdate(createcuisine);
			st.executeUpdate(createmeal);
			st.executeUpdate(createorderlist);
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
