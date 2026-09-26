package com.library;
import java.util.*;
import java.sql.*;
import java.time.LocalDate;
public class crud_operations {
	
	
	// method for adding books
	static void addbooks(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter book title:");
			String title=sc.nextLine();
			System.out.println("Enter author name:");
			String author=sc.nextLine();
			System.out.println("Enter book category:");
			String category=sc.nextLine();
			if(validations.isempty(author) || validations.isempty(title))
			{
				System.out.println("Book title or Author name should not be empty");
				System.out.println("-----------------------------------------------");
				return;
			}
			
			String sql="insert into books(book_title,author,category) values(?,?,?)";
			try(PreparedStatement pinsert=conn.prepareStatement(sql))
			{
				pinsert.setString(1, title);
				pinsert.setString(2, author);
				pinsert.setString(3, category);
				int rows=pinsert.executeUpdate();
				if(rows>0)
				{
					conn.commit();
					System.out.println("Data inserted sucessfully");
					System.out.println("---------------------------");
				}
			}
			
			
		}
		catch(SQLException e)
		{
			try 
			{
				conn.rollback();
				System.out.println("Transactions are rollbacked");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}
	
	
	// method to add members
	static void addmembers(Connection con,Scanner sc)
	{
		try
		{
			System.out.println("Enter member name:");
			String mname=sc.nextLine();
			if(validations.isempty(mname))
			{
				System.out.println("Member name should not be empty");
				System.out.println("--------------------------------");
				return;
			}
			System.out.println("Enter email:");
			String email=sc.nextLine();
			if(!(validations.validateemail(email)))
			{
				System.out.println("Please enter valid email id");
				System.out.println("-----------------------------");
				return;
			}
			System.out.println("Enter mobile number:");
			String mnumber=sc.nextLine();
			if(!(validations.validmobilenumber(mnumber)))
			{
				System.out.println("Please enter valid 10 digit mobile number");
				System.out.println("-----------------------------");
				return;
			}
			if(validations.uniquemobile(con, mnumber))
			{
				System.out.println("This mobile number is already registered ");
				System.out.println("-----------------------------");
				return;
			}
			System.out.println("Enter join date(YYYY-MM-DD)");
			String date=sc.nextLine();
			LocalDate sdate=LocalDate.parse(date);
			
			if(!(validations.validatedate(sdate)))
			{
				return;
			}
			
			
			String sql="insert into members(member_name,email,mobile_number,join_date) values(?,?,?,?)";
			try(PreparedStatement p=con.prepareStatement(sql))
			{
				p.setString(1, mname);
				p.setString(2, email);
				p.setString(3, mnumber);
				p.setString(4, date);
				int rows=p.executeUpdate();
				if(rows>0)
				{
					con.commit();
					System.out.println("Data inserted successfully");
					System.out.println("----------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			try
			{
				con.rollback();
				System.out.println("Transactions are rollback");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
		catch(java.time.format.DateTimeParseException e)
		{
		    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
		    System.out.println("-----------------------------------------------");
		}
	}
	
	
	// method to show books list
	static void showbookslist(Connection conn,Scanner sc)
	{
		try
		{
			String query="select * from books";
			try(Statement s=conn.createStatement())
			{
				ResultSet rst=s.executeQuery(query);
				boolean found=false;
				while(rst.next())
				{
					found=true;
					System.out.println("Book id-"+rst.getString("book_id")+"| Book name-"+rst.getString("book_title")+"| Author-"+rst.getString("author")+"| Category-"+rst.getString("category"));
					
				}
				if(!found)
					System.out.println("No books in the library");
				System.out.println("-------------------------------------");
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	// method for searching books
	static void serachbooks(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter the Book ID you want to search:");
			int bid=sc.nextInt();
			sc.nextLine();
			String sql="select * from books where book_id=?";
			try(PreparedStatement p=conn.prepareStatement(sql))
			{
				p.setInt(1,bid);
				ResultSet rst=p.executeQuery();
				if(rst.next()) 
				{
						System.out.println("Book id-"+rst.getInt("book_id")+"| Book name- "+rst.getString("book_title")+"| Author name-"+rst.getString("author")+"| Category-"+rst.getString("category"));
						System.out.println("------------------------------");
				}
				else 
				{
					System.out.println("No book find with this ID");
					System.out.println("-------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	// method for issuing books
	static void issuebook(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter the book id:");
			int bid=sc.nextInt();
			if(!(validations.bookexists(conn, bid)))
			{
				System.out.println("No book found with this ID");
				System.out.println("------------------------------");
				return;
			}
			System.out.print("Enter member id:");
			int mid=sc.nextInt();
			if(!(validations.memberexists(conn, mid)))
			{
				System.out.println("No member exists with this ID");
				System.out.println("---------------------------------");
				return;
			}
				
			String check="select * from book_issues where book_id=? and return_date is null";
			try(PreparedStatement p=conn.prepareStatement(check))
			{
				p.setInt(1,bid);
				ResultSet rst=p.executeQuery();
				if(rst.next())
				{
					System.out.println("This book is already issued");
					System.out.println("----------------------------");
					return;
				}
			}
			String insert="insert into book_issues(book_id, member_id, issue_date, due_date) values(?,?,curdate(),curdate()+interval 14 day)";
			try(PreparedStatement pinsert=conn.prepareStatement(insert))
			{
				pinsert.setInt(1, bid);
				pinsert.setInt(2, mid);
				int rows=pinsert.executeUpdate();
				if(rows>0)
				{
					conn.commit();
					System.out.println("Book issued successfully. Please return it before due date");
					System.out.println("-------------------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			try
			{
				conn.rollback();
				System.out.println("Transactions are roolbacked");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}
	
	
	// method for returning book
	static void return_book(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter the book id:");
			int bid=sc.nextInt();
			if(!(validations.bookexists(conn, bid)))
			{
				System.out.println("No book exists with this book id");
				System.out.println("----------------------------------");
				return;
			}
			String check="select * from book_issues where book_id=? and return_date is null";
			try(PreparedStatement p=conn.prepareStatement(check))
			{
				p.setInt(1, bid);
				ResultSet rst=p.executeQuery();
				if(rst.next())
				{
					int issueid=rst.getInt("issue_id");
					String update = "update book_issues set return_date = curdate(),fine_amount = greatest(0, datediff(curdate(), due_date) * 5) where issue_id = ?";     
					try(PreparedStatement pupdate=conn.prepareStatement(update))
					{
						pupdate.setInt(1, issueid);
						int rows=pupdate.executeUpdate();
						if(rows>0)
						{
							conn.commit();
							System.out.println("Book returned successfully");
							String find="select fine_amount from book_issues where issue_id=?";
							try(PreparedStatement pst=conn.prepareStatement(find))
							{
								pst.setInt(1,issueid);
								ResultSet rsts=pst.executeQuery();
								if(rsts.next())
								{
									double fine=rsts.getDouble("fine_amount");
									if(fine>0) 
									{
										System.out.println("Fine amount:"+fine);
										System.out.println("-------------------------");
									}
										
									else
									{
										System.out.println("No fine - returned on time");
										System.out.println("-----------------------------");
									}
								}
								
							}
						}
					}
				}
				else 
				{
					System.out.println("This book is not issued");
					System.out.println("-----------------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			try
			{
				conn.rollback();
				System.out.println("Transactions are roolback");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}
	
	
	
	// method to show issued books list
	static void issuedbookslist(Connection con,Scanner sc)
	{
		try
		{
			String query="select * from book_issues where return_date is null";
			try(PreparedStatement pst=con.prepareStatement(query))
			{
				ResultSet rst=pst.executeQuery();
				boolean found=false;
				while(rst.next())
				{
					found=true;
					System.out.println("Issue id:"+rst.getInt(1)+"| Book id:"+rst.getInt(2)+"| Member id:"+rst.getInt(3)+"| Issue date:"+rst.getDate(4)+"| Due date:"+rst.getDate(5));
				}
				System.out.println("--------------------------------");
				if(!found)
				{
					System.out.println("No books issued");
					System.out.println("--------------------------------");
				}
			
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

}
