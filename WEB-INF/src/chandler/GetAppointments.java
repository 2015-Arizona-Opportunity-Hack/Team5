package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class GetAppointments extends HttpServlet{

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
		String month = date.split("-")[1];
    	try{
    		Statement stmt = dbcon.createStatement();
    		String getAppointments = "select date, time "
				+ "from appointment where date_part('month', date) = "+ month +" and "
				+ "to_char(date, 'dy') in ('mon', 'tue', 'wed', 'thu', 'fri') and "
				+ "date >= '"+date+"'"; 
    		ResultSet rs = stmt.executeQuery(getAppointments);
    		
    		ArrayList<JSONObject> list = new ArrayList<JSONObject>(); 		
    		while(rs.next()){
    			JSONObject obj 	 = new JSONObject();
    			obj.put("date", rs.getDate("date") );
				obj.put("time", rs.getInt("time"));
    			list.add(obj);
    		}
    		if(list.size() == 0){
    			pw.println("NO_APPOINTMENTS");
    		}
    		else{
    			pw.println(list);
    		}
    		
    	}catch(Exception e){
    		pw.println("ERROR");
    	}
    	
    	
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
