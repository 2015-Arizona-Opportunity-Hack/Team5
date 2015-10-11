package chandler;

import chandler.CryptoUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.JSONObject;

public class Signin extends HttpServlet {
	private String message;
	
	private Connection dbcon;  // Connection for scope of ShowBedrock

    // "init" sets up a database connection
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
	 * Method to handle HTTP POST Requests
	 */ 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		// Set response content type
		response.setContentType("text/html");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		PrintWriter pw = response.getWriter();
		try{
			String encryptPass = CryptoUtils.byteArrayToHexString(CryptoUtils.computeHash(password));
			String getPass = "SELECT id,password, first_name, last_name, address, phone FROM users where email like '" + email + "'";
			Statement stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery(getPass);
			String passFromDB = "";

			JSONObject obj 	 = new JSONObject();
			while (rs.next()) {
				
				passFromDB = rs.getString("password");
				obj.put("id", rs.getInt("id"));
				obj.put("email", email);
				obj.put("first_name", rs.getString("first_name"));
				obj.put("last_name", rs.getString("last_name"));
				obj.put("address", rs.getString("address"));
				obj.put("phone", rs.getString("phone"));
			}
			if(!passFromDB.equals("")){
				//User Found. Return credentials.
				if(passFromDB.equals(encryptPass)){
					pw.println(obj);
				}
				else{
					//Password doesn't match.
					pw.println("WRONG_PASSWORD");
				}
				
			}else{
				//User not found.
				pw.println("USER_NOT_FOUND");
			}
		} catch (Exception e){
			// Other Database Error's
			pw.println(e.getMessage());
		}
		pw.close();
	}
	
	/*
	 * Destroy class. Close Database connection.
	 */
	public void destroy(){
		try{
			dbcon.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}