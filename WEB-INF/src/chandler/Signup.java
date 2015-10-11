package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.JSONObject;

public class Signup extends HttpServlet {

	private Connection dbcon;  // Connection for scope of Application

    // Set up the Database Connection in Constructor
    public void init(ServletConfig config) throws ServletException
    {
        String loginUser = "postgres";
        String loginPasswd = "root";
        String loginUrl = "jdbc:postgresql://localhost/postgres";

        // Load the PostgreSQL driver
        try 
        {
              Class.forName("org.postgresql.Driver");
              dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch (ClassNotFoundException ex)
        {
               System.err.println("ClassNotFoundException: " + ex.getMessage());
               throw new ServletException("Class not found Error");
        }
        catch (SQLException ex)
        {
               System.err.println("SQLException: " + ex.getMessage());
        }
    }
	/*
	 * Handling HTTP POST Requests for Singup.
	 */
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
			
			PreparedStatement insert = dbcon.prepareStatement(
				"INSERT INTO USERS (email, password, first_name, last_name, address, phone) VALUES (?,?,?,?,?,?)");
			insert.setString(1, email);
			insert.setString(2, encryPtPass);
			insert.setString(3, firstName);
			insert.setString(4, lastName);
			insert.setString(5, address);
			insert.setString(6, phone);
			insert.executeUpdate();
			
			//Get UserID
			Statement stmt = dbcon.createStatement();
			String getUserId = "SELECT id FROM users WHERE email = '" + email + "'";
			ResultSet rs = stmt.executeQuery(getUserId);
			int id = -1;
			while(rs.next()){
				id = rs.getInt("id");
			}
			if(id == -1){
				pw.println("UNKNOWN_ERROR");
			}
			else{
				//Successfully added the user. 
				//Return User data in JSON
				JSONObject obj 	 = new JSONObject();
				obj.put("id", id);
				obj.put("email", email);
				obj.put("first_name", firstName);
				obj.put("last_name", lastName);
				obj.put("address", address);
				obj.put("phone", phone);
				pw.println(obj);
			}
		}catch(SQLException se){
			if(se.getErrorCode() == 0){
				//Duplicate ID. User already exist
				pw.println("USER_ALREADY_EXISTS");
			}
			else{
				pw.println(se.getMessage());
			}
			
		}catch(Exception e){
			pw.println(e.getMessage());
		}
		pw.close();
	}
	/*
	 * Destroy Method. Close the database Connection.
	 */
	public void destroy(){
		try{
			dbcon.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
}
