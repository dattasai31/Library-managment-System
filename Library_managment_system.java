package com.library;
import java.sql.*;
import java.util.Scanner;

public class Library_managment_system {
	static final String url="jdbc:mysql://localhost:3306/jdbc";
	static final String uname="root";
	static final String pwd="Dattasai@78";
	static Connection con=null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection(url,uname,pwd);
			con.setAutoCommit(false);
			createtablebooks(con);// creating books table
			createtablemembers(con);// creating members table
			createtablebookissues(con);// creating book issues table
			Scanner sc=new Scanner(System.in);
			int choice;
			do
			{
				System.out.println("1.Add books");
				System.out.println("2.Add member");
				System.out.println("3.Display books");
				System.out.println("4.Serach for a book");
				System.out.println("5.Issue a book");
				System.out.println("6.Return book");
				System.out.println("7.Show issued books");
				System.out.println("8.Enter 0 to exit");
				System.out.println("Enter your choice: ");
				choice=sc.nextInt();
				sc.nextLine();
				switch(choice)
				{
				case 1:
					crud_operations.addbooks(con, sc);
					break;
				case 2:
					crud_operations.addmembers(con, sc);
					break;
				case 3:
					crud_operations.showbookslist(con, sc);;
					break;
				case 4:
					crud_operations.serachbooks(con, sc);
					break;
				case 5:
					crud_operations.issuebook(con, sc);
					break;
				case 6:
					crud_operations.return_book(con, sc);
					break;
				case 7:
					crud_operations.issuedbookslist(con, sc);
					break;
				case 0:
					System.out.println("Exiting...");
					break;
				default:
					System.out.println("Enter valid choice");
					break;
				}
			}while(choice!=0);
			
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(con!=null)
				{
					con.close();
				}
			}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	
	
	// method for creating books table
	static void createtablebooks(Connection con) throws SQLException
	{
		String query="""
						create table if not exists books(
						book_id int primary key auto_increment,
						book_title varchar(50) not null,
						author varchar(50) not null,
						category varchar(50))""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Books table created successfully with constraints");
		}
	}
	
	
	// method for creating members table
	static void createtablemembers(Connection con) throws SQLException
	{
		String query="""
						create table if not exists members(
						member_id int primary key auto_increment,
						member_name varchar(50) not null,
						email varchar(50) not null,
						mobile_number varchar(12) unique,
						join_date date not null)""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Members table created succesfully");
			
		}		
	}
	
	
	// method for creating book issues table
	static void createtablebookissues(Connection con) throws SQLException
	{
		String query="""
						create table if not exists book_issues(
						issue_id int primary key auto_increment,
						book_id int,
						member_id int,
						issue_date date not null,
						due_date date not null,
						return_date date,
						fine_amount decimal(6,2),
						foreign key (book_id) references books(book_id),
						foreign key (member_id) references members(member_id))""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Book Issues table created successfully");
		}
	}

}
