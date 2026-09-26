package com.library;

import java.sql.*;
import java.time.LocalDate;


public class validations {
	
	
	// validation method to check email
	static boolean validateemail(String s)
	{
		if(s!=null && !(s.isEmpty()))
		{
			if(s.contains("@") && s.contains("."))
			{
				return true;
			}
		}
		return false;
	}
	
	
	// validation method to check whether date is valid or not
	static boolean validatedate(LocalDate date)
	{
		LocalDate today=LocalDate.now();
		if(date.isBefore(today))
		{
			System.out.println("You cannot enter date before today ");
			System.out.println("---------------------------------------------");
			return false;
		}
		return true;
	}
	
	
	// validation method to check whether the mobile number is valid or not
	static boolean validmobilenumber(String s)
	{
		if(s==null || s.isEmpty())
		{
			return false;
		}
		return s.matches("\\d{10}");
	}
	
	
	
	// validation method to check whether the mobile number is unique or not
	static boolean uniquemobile(Connection con,String s) throws SQLException
	{
		String query="select * from members where mobile_number=?";
		try(PreparedStatement pst=con.prepareStatement(query))
		{
			pst.setString(1,s);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
	
	
	// validation method to check whether the string is empty or not
	static boolean isempty(String s)
	{
	    if(s == null || s.isEmpty())
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	
	// validation method to check whether bokk exists or not
	static boolean bookexists(Connection con,int bookid) throws SQLException
	{
		
		String query="select * from books where book_id=?";
		try(PreparedStatement pst=con.prepareStatement(query))
		{
			pst.setInt(1,bookid);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
	
	
	// validation method to check whether member exists or not
	static boolean memberexists(Connection con,int memberid) throws SQLException
	{
		String query="select * from members where member_id=?";
		try(PreparedStatement pst=con.prepareStatement(query))
		{
			pst.setInt(1, memberid);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
}

