package chandler;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class RegisterEvent extends HttpServlet{

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
    
    //Handle POST Request.
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		String userId = request.getParameter("user_id");
		String eventId = request.getParameter("event_id");
		String action = request.getParameter("action");
		String actionInfo = request.getParameter("action_info");
		PrintWriter pw = response.getWriter();
		try {
			Statement stmt = dbcon.createStatement();
			float amount = 0;
			if (action.equals("DONATE")){
				amount = Float.parseFloat(request.getParameter("amount"));
			}
			PreparedStatement regForEvent = dbcon.prepareStatement("INSERT INTO event_attend (event_id,user_id,type,amount,info)"
					+ "VALUES(?,?,?,?,?)");
			regForEvent.setLong(1, Integer.parseInt(userId));
			regForEvent.setLong(2, Integer.parseInt(eventId));
			regForEvent.setString(3, action);
			regForEvent.setFloat(4,amount);
			regForEvent.setString(5,actionInfo);
			regForEvent.executeUpdate();
			//dbcon.commit();
			pw.println("SUCCESS");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if(e.getErrorCode() == 1062){
				JSONObject obj 	 = new JSONObject();
				obj.put("message", "SUCCESS");
				pw.println(obj);
			}
			else{
				JSONObject obj 	 = new JSONObject();
				obj.put("message", "ERROR");
				pw.println(obj);
			}
			
		}catch (Exception e){
			JSONObject obj 	 = new JSONObject();
			obj.put("message", "DATBASE_ERROR");
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
