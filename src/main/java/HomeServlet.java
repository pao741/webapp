/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.mindrot.jbcrypt.BCrypt;
import service.DatabaseService;
import service.SecurityService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author gigadot
 */
public class HomeServlet extends HttpServlet implements Routable{

    private SecurityService securityService;

    private DatabaseService databaseService;

    public HomeServlet() throws SQLException, ClassNotFoundException {
        this.databaseService = new DatabaseService();
    }

    public void refreshTable(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        ArrayList<String> userList = databaseService.getAllUser();
//        System.out.println("user list " + userList);
        request.setAttribute("userList", userList);
        RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/home.jsp");
        rd.include(request, response);
    }

    @Override
    public String getMapping() {
        return "/index.jsp";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = false;
        try {
            authorized = securityService.isAuthorized(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (authorized) {
            // do MVC in here
            String username = (String) request.getSession().getAttribute("username");
            request.setAttribute("username", username);
            try {
                refreshTable(request,response);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            response.sendRedirect("/login");
        }
    }

    public void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        String username = request.getParameter("new_username");
        String hashedPassword = BCrypt.hashpw(request.getParameter("new_password"), BCrypt.gensalt());
        if(!username.isEmpty() && !hashedPassword.isEmpty()){
            if (!databaseService.containUser(username)){
                databaseService.createUser(username, hashedPassword);
            }else{
                // todo : says user already exist
            }
        }else{
            // todo: please fill in the blank
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        if(req.getParameter("add_user")!= null){
            String newUsername = req.getParameter("adding_username");
            String newPassword = req.getParameter("adding_password");
            String confirmPassword = req.getParameter("confirm_password");
            System.out.println(newPassword);
            System.out.println(confirmPassword);
            if (newPassword.compareTo(confirmPassword) == 0) {
                try {
                    databaseService.createUser(newUsername, newPassword);
                    refreshTable(req, resp);
                } catch (SQLException e) {
                    e.printStackTrace();
                    String error = e.getMessage();
                    req.setAttribute("adding_error", error);
                    RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/home.jsp");
                    rd.include(req, resp);
                }
            }else{
                String error = "Password doesn't match";
                req.setAttribute("adding_error", error);
                try {
                    refreshTable(req, resp);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else if (req.getParameter("removing_user")!=null){
            String user = req.getParameter("user_to_remove");
            System.out.println(user);
            try {
                databaseService.delUser(user);
                refreshTable(req, resp);
            } catch (SQLException e) {
                e.printStackTrace();
                String error = e.getMessage();
                req.setAttribute("removing_error", error);
                RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/home.jsp");
            }
        }

    }

    public void removeUser(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String username = request.getParameter("remove_username");
        databaseService.delUser(username);
    }
}
