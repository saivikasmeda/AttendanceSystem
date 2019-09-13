/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.services;

import com.beans.BatchDetails;
import com.beans.CodeDetails;
import com.beans.TraineeDetails;
import com.dbconnectivity.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author bros
 */
public class AttendanceClientService {
    public CodeDetails getActiveCode(String date) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();
        System.out.println("service"+date);
        PreparedStatement ps = con.prepareStatement("select * from attncodedetails where attndate=?1 and status = 'active'");
        ps.setString(1, date);

        ResultSet rs = ps.executeQuery();
        
      
        CodeDetails details = new CodeDetails();
          while (rs.next()) {
       
       details.setCode(rs.getString("attncode"));
       details.setBatchName(rs.getString("BATCHNAME"));
          }
     
        rs.close();
        ps.close();
        con.close();

        return details;

    }
    
    
    //2. insert trainee
    public boolean insertTrainee(TraineeDetails trainee) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();
//        System.out.println("service"+date);
        PreparedStatement ps = con.prepareStatement("select tablename from batchdetails where batchname =?1 and status = 'RUNNING'");
        ps.setString(1, trainee.getBatchName());

        ResultSet rs = ps.executeQuery();
        String tableName = "";
        while(rs.next()){
            tableName = rs.getString("tablename");
            System.out.println("table name is :" + tableName);
            System.out.println("batch name :"+ trainee.getBatchName());
        }
        rs.close();
        ps.close();
        
        PreparedStatement ps2 = con.prepareStatement("select * from "+tableName+" where machineid = ?1");
        ps2.setString(1, trainee.getMachineName());
        ResultSet rs2 = ps2.executeQuery();
        
       
        while (rs2.next()) {
            trainee.setEmpId(rs2.getString("empid"));
            trainee.setEmpName(rs2.getString("empname"));
            trainee.setMailId(rs2.getString("mailid"));
            
          }
        rs2.close();
        ps2.close();
        
        LocalDate ld = LocalDate.now();
        Calendar cl = Calendar.getInstance();
        Date dat = Date.valueOf(ld);
        System.out.println("date datatype"+ dat);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(dat);
        System.out.println("date : "+ date);
         SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String time = tf.format(cl.getTime());
       
        System.out.println("time: "+ time + "EMP ANME "+ trainee.getEmpId());
        
//        PreparedStatement ps3 = con.prepareStatement("insert into traineeattendance (empid,mailid,batchname,attndate,attntime,status) values(?1,?2,?3,?4,?5,?6)");
//        ps3.setString(1, trainee.getEmpId());
//        ps3.setString(2, trainee.getMailId());
//        ps3.setString(3, trainee.getBatchName());
//        ps3.setString(4, date);
//        ps3.setString(5, time);
//        ps3.setString(6, "PRESENT");
        PreparedStatement ps3 = con.prepareStatement("update  traineeattendance set status = 'PRESENT' where empid =?1 and batchname = ?2 and ATTNdate =?3 and editstatus = 'OPEN' ");
        ps3.setInt(1, Integer.parseInt(trainee.getEmpId()));
        ps3.setString(2, trainee.getBatchName());
        ps3.setString(3, date);
//        ps3.setString(4, time);
  
        
        Integer rows = ps3.executeUpdate();
     
        
        ps3.close();
        con.close();
        if (rows > 0 ){
            System.out.println("if block");
        return true;
        }
        else{
            return false;
        }
    }

   
    
    
}
