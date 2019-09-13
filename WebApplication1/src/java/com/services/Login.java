package com.services;

import com.beans.UserBean;
import com.dbconnectivity.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Login {

    public boolean validateLogin(UserBean userdata) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();

        PreparedStatement ps = con.prepareStatement("select * from LOGIN where username=?1 and password=?2");
        ps.setString(1, userdata.getUsername());
        ps.setString(2, userdata.getPassword());

        ResultSet rs = ps.executeQuery();
        ArrayList arr = new ArrayList();

        while (rs.next()) {
            userdata.setUsername(rs.getString("username"));
            userdata.setPassword(rs.getString("PASSWORD"));

            arr.add(userdata);
            System.out.println("Java Username: " + userdata.getUsername());
            System.out.println("Java Password: " + userdata.getPassword());
        }
        if (arr.size() > 0) {
            isValid = true;
        } else {
            isValid = false;
        }
        rs.close();
        ps.close();
        con.close();

        return isValid;

    }

}
