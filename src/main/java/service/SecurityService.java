/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gigadot
 */
public class SecurityService {

    private DatabaseService databaseService;


    public SecurityService() throws SQLException, ClassNotFoundException {
        databaseService = new DatabaseService();
    }
    
    public boolean isAuthorized(HttpServletRequest request) throws SQLException {
        String username = (String) request.getSession()
                .getAttribute("username");
        // do checking
       return (username != null && databaseService.containUser(username));
    }
    
    public boolean authenticate(String username, String password, HttpServletRequest request) throws SQLException {
        boolean isMatched = databaseService.authenticate(username,password);
        if (isMatched) {
            request.getSession().setAttribute("username", username);
            return true;
        } else {
            return false;
        }
    }
    
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }
    
}
