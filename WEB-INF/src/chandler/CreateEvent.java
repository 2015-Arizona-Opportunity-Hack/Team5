package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.ServletConfig;
import java.text.SimpleDateFormat;
import org.json.JSONObject;

public class CreateEvent extends HttpServlet {
		
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
		PrintWriter pw = response.getWriter();
		try{
			String eventName = request.getParameter("event_name");
			String eventDescription = request.getParameter("description");
			String eventLocation = request.getParameter("location");
			int organizerId = Integer.parseInt(request.getParameter("organizer_id"));
			String startTime = request.getParameter("start_time");
			String endTime = request.getParameter("end_time");
			String date= request.getParameter("event_date");
			String eventType= request.getParameter("event_type");
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			
			PreparedStatement insert = dbcon.prepareStatement(
				"INSERT INTO EVENT (name, decription,location, organizer, start_time, end_time, date, type) VALUES (?,?,?,?,?,?,?,?)");
			insert.setString(1, eventName);
			insert.setString(2, eventDescription);
			insert.setString(3, eventLocation);
			insert.setInt(4, organizerId);
			insert.setTime(5, new java.sql.Time((sdf.parse(startTime)).getTime()));
			insert.setTime(6, new java.sql.Time(sdf.parse(endTime).getTime()));
			insert.setDate(7, java.sql.Date.valueOf(date));
			insert.setString(8, eventType);
			insert.executeUpdate();

			JSONObject obj 	 = new JSONObject();
			obj.put("message", "SUCCESS");
			pw.println(obj);
			//pw.println(email + encryPtPass + firstName + lastName + address);
		}catch(Exception e){
			//pw.println(e.getMessage());
			//e.printStackTrace(pw);
			JSONObject obj 	 = new JSONObject();
			obj.put("message", "DATABASE_ERROR");
			pw.println(obj);
		}
		pw.close();
	}
	
	/*
     *  Delete all references.
     */
    public void destroy() {
        // Finalization code...
    	try {
			dbcon.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
