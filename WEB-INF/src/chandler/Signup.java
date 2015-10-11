package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;


public class Signup extends HttpServlet {
	private String message;
	
	public void init() throws ServletException{
	    // Do required initialization 
		message = "Hello World";
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter pw = response.getWriter();
		try{
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String encryPtPass = CryptoUtils.byteArrayToHexString(CryptoUtils.computeHash(password));
			String firstName = request.getParameter("first_name");
			String lastName = request.getParameter("last_name");
			String address = request.getParameter("address");
			String phone = request.getParameter("phone");
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/postgres","postgres", "root");
			PreparedStatement insert = connection.prepareStatement(
				"INSERT INTO USERS (email, password, first_name, last_name, address, phone) VALUES (?,?,?,?,?,?)");
			insert.setString(1, email);
			insert.setString(2, encryPtPass);
			insert.setString(3, firstName);
			insert.setString(4, lastName);
			insert.setString(5, address);
			insert.setString(6, phone)
			insert.executeQuery();
			connection.close();
			pw.println("SUCCESS");
			//pw.println(email + encryPtPass + firstName + lastName + address);
		}catch(Exception e){
			pw.println("ERROR");
		}
	}
}
