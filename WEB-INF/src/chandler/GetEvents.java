package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import javax.json.Json;
//import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;


public class GetEvents extends HttpServlet{

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
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
    	PrintWriter pw = response.getWriter();
    	String date = request.getParameter("curr_date");
    	try{
    		Statement stmt = dbcon.createStatement();
    		String getEvents = "SELECT id,name, description, location,"
    				+ " organizer, start_time, end_time, date, type"
    				+ " FROM event WHERE date >= '" + date + "'";
    		//pw.println(getEvents);
		ResultSet rs = stmt.executeQuery(getEvents);
    		
    		ArrayList<JSONObject> list = new ArrayList<JSONObject>(); 		
    		while(rs.next()){
    			JSONObject obj 	 = new JSONObject();
    			obj.put("id",rs.getInt("id") );
    			obj.put("name",rs.getString("name") );
    			obj.put("description",rs.getString("description") );
    			obj.put("location",rs.getString("location") );
    			obj.put("organizer",rs.getString("organizer") );
    			obj.put("start_time",rs.getString("start_time") );
    			obj.put("end_time",rs.getString("end_time") );
    			obj.put("date",rs.getString("date") );
    			obj.put("type",rs.getString("type") );
    			list.add(obj);
    		}
    		if(list.size() == 0){
    			pw.println("NO_EVENTS");
    		}
    		else{
    			pw.println(list);
    		}
    		
    	}catch(Exception e){
    		pw.println(e.getMessage());
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
