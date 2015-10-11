package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import chandler.CryptoUtils;
import java.sql.*;

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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		// Set response content type
		response.setContentType("text/html");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		PrintWriter pw = response.getWriter();
		try{
			String encryptPass = CryptoUtils.byteArrayToHexString(CryptoUtils.computeHash(password));
			String getPass = "SELECT password FROM users where email like '" + email + "'";
			Statement stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery(getPass);
			String passFromDB = "";
			//String role;
			while (rs.next()) {
				passFromDB = rs.getString("password");
				//role = rs.getString("role");
			}
			if(!passFromDB.equals("")){
				if(passFromDB.equals(encryptPass)){
					pw.println("SUCCESSS");
				}
				else{
					pw.println("ERROR: PASSWORD_MISMATCH");
				}
				
			}else{
				//No such user
				pw.println("ERROR: USER DOESN'T EXIST");
			}
		} catch (Exception e){
			//Primary Key Voilation
			pw.println(e.getMessage());
		}
		pw.close();
	}
}
