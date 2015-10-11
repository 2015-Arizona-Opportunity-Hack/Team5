package chandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;


public class CreateEvent extends HttpServlet {
	private String message;
	
	public void init() throws ServletException{
	    // Do required initialization 
		message = "Hello World";
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
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/postgres","postgres", "root");
			PreparedStatement insert = connection.prepareStatement(
				"INSERT INTO EVENT (name, decription,location, organizer, start_time, end_time, date, type) VALUES (?,?,?,?,?,?,?,?)");
			insert.setString(1, eventName);
			insert.setString(2, eventDescription);
			insert.setString(3, eventLocation);
			insert.setInt(4, organizerId);
			insert.setString(5, startTime);
			insert.setString(6, endTime);
			insert.setString(7, date);
			insert.setString(8, eventType);
			insert.executeQuery();
			connection.close();
			pw.println("SUCCESS");
			//pw.println(email + encryPtPass + firstName + lastName + address);
		}catch(Exception e){
			pw.println(e.getMessage());
			//e.printStackTrace(pw);
			pw.println("ERROR");
		}
		pw.close();
	}
}
