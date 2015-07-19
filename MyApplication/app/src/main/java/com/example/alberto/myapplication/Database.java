package com.example.alberto.myapplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;





public class Database {
	

	final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   final static String DB_URL = "jdbc:mysql://localhost:3306/app";
	  
	   //  Database credentials
	    final static String USER = "root";
	   final static String PASS = "1234";
	  static Connection connection = null;
	public static void connect() throws SQLException,  IOException, ParseException {


		
		System.out.println("-------- MySQL JDBC Connection Testing ------------");
		 
		try {
		
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
	 
		System.out.println("MySQL JDBC Driver Registered!");
		
	 
		try {
			 connection = DriverManager
			.getConnection("jdbc:mysql://localhost:3306/cinema","root", "1234");
	 
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");

		} else {
			System.out.println("Failed to make connection!");
		}
		
		
		
	
}

}


