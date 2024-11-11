

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class FindJobServlet
 */
@WebServlet("/FindJobServlet")
public class FindJobServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindJobServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);//Retrieves the current session without creating a new one 
        if (session == null) {
            System.out.println("Session is null. Redirecting to login page.");
            response.sendRedirect("login.jsp");
            return;
        }
        //Checks if the session exists and if the user is logged in
        Integer isLoggedIn = (Integer) session.getAttribute("is_loggedin");
        //Retrieves the user_id from the session.
        Integer userId = (Integer) session.getAttribute("user_id");
        
        System.out.println(userId);

        if (isLoggedIn == null || isLoggedIn != 1) {
            System.out.println("User is not logged in. Redirecting to login page.");
            response.sendRedirect("login.jsp");
            return;
        }

        if (userId == null) {
            System.out.println("User ID is not found in session. Redirecting to login page.");
            response.sendRedirect("login.jsp");
            return;
        }
        List<Map<String, Object>> findJob = fetchJobListFromDatabase(userId);

        request.setAttribute("findJob", findJob);

        request.getRequestDispatcher("findJob.jsp").forward(request, response);
    }
    private List<Map<String, Object>> fetchJobListFromDatabase(int userId) {
        String jdbcURL = "jdbc:mysql://localhost:3306/bid";
        String dbUser = "root";
        String dbPassword = "";
        System.out.println("Connection  Sucessful");
        List<Map<String, Object>> findJob = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
        	  System.out.println("Database connection successful.");
        	  
        	   String sql = "SELECT * FROM job WHERE bid_end_on >=  CURDATE()  AND created_by != ?";
        	   System.out.println(sql);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                System.out.println("USERID:"+userId);
                try (ResultSet result = pstmt.executeQuery()) {
                	
                	System.out.println(result);
                    while (result.next()) {

                         Map<String, Object> job = new HashMap<>();
                         job.put("id", result.getInt("id"));
                         job.put("job_title", result.getString("job_title"));
                         job.put("description", result.getString("description"));
                         job.put("created_at", result.getTimestamp("created_at"));
                         job.put("status", result.getString("status"));
                         job.put("final_bid_amount", result.getDouble("final_bid_amount"));
                         findJob.add(job);
                         
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
       
        return findJob;
    }
   
}
