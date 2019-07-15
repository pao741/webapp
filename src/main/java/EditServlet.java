import models.User;
import service.DatabaseService;
import service.SecurityService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/edit")
public class EditServlet extends HttpServlet implements Routable{


    private SecurityService securityService;

    private DatabaseService databaseService;


    public EditServlet() throws SQLException, ClassNotFoundException {
        this.databaseService = new DatabaseService();
    }

    @Override
    public String getMapping() {
        return "/edit";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean authorized = false;
        try {
            authorized = securityService.isAuthorized(req);
        } catch(SQLException ex) {
        }
        if(authorized){
            String username = (String)req.getSession().getAttribute("editing_user");
            try {
                User user = databaseService.getUser(username);
                req.setAttribute("username", user.getUsername());
                req.setAttribute("first_name", user.getFirstName());
                req.setAttribute("last_name", user.getLastName());

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/edit.jsp");
            rd.include(req,resp);
        } else resp.sendRedirect("/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = (String)req.getSession().getAttribute("editing_user");
        if (req.getParameter("edit_username")!= null){
            String newUsername = req.getParameter("new_username");
            try {
                if (databaseService.containUser(newUsername)){
                    req.setAttribute("error","User already exist");
                }else {
                    databaseService.updateUsername(newUsername, username);
                    username = newUsername;
                }
                req.getSession().setAttribute("editing_user",username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if (req.getParameter("edit_password") != null){
            String newPassword = req.getParameter("new_password");
            String confirmPassword = req.getParameter("confirm_password");
            if (newPassword.compareTo(confirmPassword) != 0){
                String error = "Password doesn't match";
                req.setAttribute("password_error", error);
            }else {
                try {
                    databaseService.updatePassword(newPassword, username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else if (req.getParameter("edit_first_name") != null){
            String newFirstname = req.getParameter("new_first_name");
            try {
                databaseService.updateFirstName(newFirstname,username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(req.getParameter("edit_last_name") != null){
            String newLastName = req.getParameter("new_last_name");
            try {
                databaseService.updateLastName(newLastName,username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            User user = databaseService.getUser(username);
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            req.setAttribute("username", username);
            req.setAttribute("first_name", firstName);
            req.setAttribute("last_name", lastName);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/edit.jsp");
        rd.include(req, resp);
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
