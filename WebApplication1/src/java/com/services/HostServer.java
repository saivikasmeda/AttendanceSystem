/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.services;

import com.beans.BatchDetails;
import com.beans.CodeDetails;
import com.beans.TraineeDetails;
import com.beans.UserBean;
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
public class HostServer {
     public BatchDetails batchDetails(String date) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();

        PreparedStatement ps = con.prepareStatement("select * from BATCHDETAILS");


        ResultSet rs = ps.executeQuery();
        
        ArrayList<BatchDetails> arr = new ArrayList();
     
      
        BatchDetails batch = new BatchDetails();
        while (rs.next()) {
            System.out.println("inside whiel");
            String start = rs.getString("startdate");
            String end = rs.getString("enddate");
//            System.out.println("strart" +start + "asd"+Date.valueOf(LocalDate.now()));
//            Date dat = new Date;
          System.out.println(start);
            System.out.println("end"+end);
            System.out.println(date.compareTo(start));
            System.out.println("asd"+date.compareTo(end));
            System.out.println("status"+ rs.getString("status"));
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0 && rs.getString("status").contains("RUNNING")){
                System.out.println("inside if");
                   
                batch.setBatchName(rs.getString("batchname"));
                batch.setStrength(rs.getString("strength"));
                
            }

        }
    
        rs.close();
        ps.close();
        con.close();

        return batch;

    }

      public boolean insertCode(CodeDetails code) throws Exception {
          boolean isValid;
        Connection con = DBConnection.getdbConnection();
        
          System.out.println("batchname"+code.getBatchName());
          System.out.println("batchname"+code.getCode());
          System.out.println("batchname"+code.getDate());
          System.out.println("batchname"+code.getTime());
          System.out.println("batchname"+code.getStrength());
          
            PreparedStatement ps2 = con.prepareStatement("select tablename from batchdetails where batchname =?1 and status = 'RUNNING'");
            ps2.setString(1, code.getBatchName());

            ResultSet rs2 = ps2.executeQuery();
            String tableName = "";
            while(rs2.next()){
                tableName = rs2.getString("tablename");
                System.out.println("table name is :" + tableName);
//            System.out.println("batch name :"+ trainee.getBatchName());
            }
            rs2.close();
            ps2.close();
         System.out.println("before rs3");
        PreparedStatement ps3 = con.prepareStatement("select * from "+tableName+ " where status = 'active'");
        ResultSet rs3 = ps3.executeQuery();
          System.out.println("after rs3");
       ArrayList<TraineeDetails> arrayTrainee = new ArrayList<>();
        while (rs3.next()) {
            TraineeDetails trainee = new TraineeDetails();
            trainee.setEmpId(rs3.getString("empid"));
            trainee.setEmpName(rs3.getString("empname"));
            trainee.setMailId(rs3.getString("mailid"));
            trainee.setBatchName(code.getBatchName());
            trainee.setMachineName(rs3.getString("machineid"));
             arrayTrainee.add(trainee);
            
          }
        rs3.close();
        ps3.close();
        
        for (int i =0; i< arrayTrainee.size();i++){
            TraineeDetails trainee = arrayTrainee.get(i);
             PreparedStatement ps4 = con.prepareStatement("insert into traineeattendance (empid,mailid,batchname,attndate,attntime,status) values(?1,?2,?3,?4,?5,?6)");
             ps4.setString(1, trainee.getEmpId());
             ps4.setString(2, trainee.getMailId());
             ps4.setString(3, trainee.getBatchName());
             ps4.setString(4, code.getDate());
            ps4.setString(5, code.getTime());
             ps4.setString(6, "ABSENT");
             
             Integer rows = ps4.executeUpdate();
             System.out.println("number rows inserted is "+rows);
             ps4.close();
        }
        
        
        PreparedStatement ps = con.prepareStatement("insert into attncodedetails (batchname,attndate,time,attncode,strength) values(?1,?2,?3,?4,?5)");
        ps.setString(1, code.getBatchName());
        ps.setString(2, code.getDate());
        ps.setString(3, code.getTime());
        ps.setString(4, code.getCode());
        ps.setInt(5, Integer.parseInt(code.getStrength()));

        Integer row = ps.executeUpdate();

        if (row ==1 ){
            isValid = true;
        }
        else{
            isValid = false;
        }
        ps.close();
        con.close();

        return isValid;
      }
      
      
      public boolean statusChange(CodeDetails code) throws Exception {
          boolean isValid;
        Connection con = DBConnection.getdbConnection();

        PreparedStatement ps = con.prepareStatement("update attncodedetails set status = 'inacitve' where batchname = ?1 and attndate = ?2 and time = ?3 and attncode = ?4");
        ps.setString(1, code.getBatchName());
        ps.setString(2, code.getDate());
        ps.setString(3, code.getTime());
        ps.setString(4, code.getCode());
     

        Integer row = ps.executeUpdate();
        
        LocalDate ld = LocalDate.now();
        Calendar cl = Calendar.getInstance();
        Date dat = Date.valueOf(ld);
        System.out.println("date datatype"+ dat);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(dat);
        PreparedStatement ps2 = con.prepareStatement("update  traineeattendance set editstatus = 'CLOSE' where ATTNdate =?1 and editstatus = 'OPEN' ");
        ps2.setString(1, date);
        
        Integer rows = ps2.executeUpdate();
         System.out.println("status changed too close for this many rows "+rows);
        
        if (row ==1 ){
            isValid = true;
        }
        else{
            isValid = false;
        }
        ps.close();
        con.close();
        isValid = true;
        return isValid;
      }

      
      public boolean discardAttendance(CodeDetails discard) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();
//        System.out.println("service"+date);

        PreparedStatement ps2 = con.prepareStatement("delete from attncodedetails where attndate=?1 and batchname = ?2 and time = ?3");
        ps2.setString(1, discard.getDate());
        ps2.setString(2, discard.getBatchName());
        ps2.setString(3, discard.getTime());
        Integer coderows = ps2.executeUpdate();
         System.out.println("code details rows deleted "+ coderows);
        
         System.out.println("get time "+ discard.getTime());
        PreparedStatement ps = con.prepareStatement("delete from traineeattendance where attndate=?1 and batchname = ?2 and attntime >= ?3 and editstatus = 'OPEN'");
        ps.setString(1, discard.getDate());
        ps.setString(2, discard.getBatchName());
        ps.setString(3, discard.getTime());
        Integer rows = ps.executeUpdate();
        
        ps2.close();
        ps.close();
        con.close();
        if (rows > 0 ){
        return true;
        }
        else{
            return false;
        }
    }
      
      
      
      
        public ArrayList<TraineeDetails> manualTrainees( ) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();
//        System.out.println("service"+date);

        PreparedStatement ps = con.prepareStatement("select * from traineeattendance where editstatus = 'OPEN'");

       ResultSet rs = ps.executeQuery();
            ArrayList<TraineeDetails> traineeList = new ArrayList<>();
            while(rs.next()){
                TraineeDetails trainee = new TraineeDetails();
                trainee.setEmpId(rs.getString("empid"));
                trainee.setEmpName(rs.getString("mailid"));
                trainee.setBatchName(rs.getString("batchname"));
                trainee.setDate(rs.getString("attndate"));
                trainee.setTime(rs.getString("attntime"));
                traineeList.add(trainee);
            }
      
        ps.close();
        con.close();
       return traineeList;
    }
        public boolean updateManualAttendace(ArrayList<TraineeDetails> traineeList ) throws Exception {
        boolean isValid;
        Connection con = DBConnection.getdbConnection();
//        System.out.println("service"+date);
        for (TraineeDetails i : traineeList) {
            PreparedStatement ps = con.prepareStatement("update traineeattendance set status = 'PRESENT' where editstatus = 'OPEN' and mailid = ?1");
            ps.setString(1, i.getEmpName());
           Integer row = ps.executeUpdate();
            System.out.println("rows updated are "+ row);
           ps.close();
        }
        
         PreparedStatement ps2 = con.prepareStatement("select attncode from attncodedetails where status = 'active'");
         ResultSet rs2 = ps2.executeQuery();
         String attncode = "";
         while(rs2.next()){
             attncode = rs2.getString("attncode");
         }
            System.out.println("code is "+ attncode);
        CodeDetails code = new CodeDetails();
        code.setBatchName(traineeList.get(0).getBatchName());
        code.setDate(traineeList.get(0).getDate());
        code.setTime(traineeList.get(0).getTime());
        code.setCode(attncode);
        this.statusChange(code);
       
        con.close();
       return true;
    }
}
