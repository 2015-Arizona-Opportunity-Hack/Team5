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

public class CreateAppointment extends HttpServlet {

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
			String date = request.getParameter("date");
			String time = request.getParameter("time");
			String userId = request.getParameter("user_id");
			String text = request.getParameter("subject");
			PreparedStatement insert = dbcon.prepareStatement(
				"INSERT INTO appointment (user_id, text, date, time) VALUES (?,?,?,?)");
			insert.setLong(1, Integer.parseInt(userId));
			insert.setString(2, text);
			insert.setDate(3, java.sql.Date.valueOf(date));
			insert.setLong(4, Integer.parseInt(time));
			insert.executeUpdate();
			JSONObject obj 	 = new JSONObject();
			obj.put("message", "SUCCESS");
			pw.println(obj);
		}catch(SQLException se){
			if(se.getErrorCode() == 0){
				//Duplicate ID. User already exist
				JSONObject obj 	 = new JSONObject();
				obj.put("message", "APPOINTMENT_NOT_AVAILABLE");
				pw.println(obj);
			}
			else{
				JSONObject obj 	 = new JSONObject();
				obj.put("message", "ERROR");
				pw.println(obj);
				pw.println(obj);
			}
			
		}catch(Exception e){
			JSONObject obj 	 = new JSONObject();
			obj.put("message", "DATABASE_ERROR");
			pw.println(obj);
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
