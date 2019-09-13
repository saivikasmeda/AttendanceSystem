
package com.controller;

import com.beans.BatchDetails;
import com.beans.CodeDetails;
import com.beans.TraineeDetails;
import com.beans.UserBean;
import com.google.gson.Gson;
import com.services.AttendanceClientService;
import com.services.Login;
import com.services.HostServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


// Single controller servlet for whole application -- to hit the this servlet for diff url after 'Controller' string , * is used below in annotation
@WebServlet(name = "Controller", urlPatterns ={"/Controller","/Controller/*"})
public class Controller extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                doPost(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
            // To write to the o/p stream , get the instance of print writer
            PrintWriter pw = response.getWriter();
             
            // Once the servlet is hit, check for requested url match and call corresponding functionality
            String paths[] = request.getRequestURI().split("/");
            System.out.println("URL "+request.getRequestURI());
                for(String s : paths){
                    System.out.println("Path Tokens are : "+s);}
                    if(paths[3].equals("batchDetails")){
                        System.out.println("Getting batch details controller...");
                        try {
                            batchDetails(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("insertCode")){
                        System.out.println("inserting code details controller...");
                        try {
                            insertCode(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("statusChange")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            statusChange(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("getActiveCode")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            getActiveCode(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("insertTrainee")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            insertTrainee(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("discardAttendance")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            discardAttendance(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("manualTrainees")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            manualTrainees(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(paths[3].equals("updateManualAttendace")){
                        System.out.println("Changing the status of the code controller...");
                        try {
                            updateManualAttendace(request, response);
                        } catch (Exception ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
    }

 /*-----------------------------------------------------------------------------------------------------------------------------------*/
                                        // Functionality based methods
    
    //1. Getting All Valid BAtchDetails
    protected void batchDetails(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
                HostServer logdata=new HostServer();
		System.out.println("In batchDetails method..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);

		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
//		UserBean userdata = gson.fromJson(s, UserBean.class);

         
                 BatchDetails batch =  logdata.batchDetails(s);
                 String result = gson.toJson(batch);
                 pw.write(result);
  
    }
    
    //2. Insert attendance code
    protected void insertCode(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                HostServer logCode=new HostServer();
		System.out.println("In Inserting codedetails..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		CodeDetails codeData = gson.fromJson(s, CodeDetails.class);
                // batchname is not happening while insert
//                codeData.setBatchName("Jun18_lc1_AppleTechnology_RT1");
                System.out.println("Cocoa batchName: "+codeData.getBatchName());
                System.out.println("Cocoa code: "+codeData.getCode());
                 System.out.println("Cocoa strength: "+codeData.getStrength());
                System.out.println("Cocoa time: "+codeData.getTime());
                 System.out.println("Cocoa date: "+codeData.getDate());
               
                if (logCode.insertCode(codeData)){
                    pw.write("Successfully code inserted");
                }else{
                    pw.write("Fail");
                            
                }
                 
                 
              
                	
	}
    //3. Changing the status of code to inactive
      protected void statusChange(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                HostServer logCode=new HostServer();
		System.out.println("In status cahnge ..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		CodeDetails codeData = gson.fromJson(s, CodeDetails.class);
                System.out.println("Cocoa batchName: "+codeData.getBatchName());
                System.out.println("Cocoa code: "+codeData.getCode());
                System.out.println("Cocoa time: "+codeData.getTime());
                 System.out.println("Cocoa date: "+codeData.getDate());
               
                if (logCode.statusChange(codeData)){
                    pw.write("Success");
                }else{
                    pw.write("Fail");
                            
                }
                	
	}
    
      
        // 4. Discard attendance
      protected void discardAttendance(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                HostServer discardData=new HostServer();
		System.out.println("In validateLogin..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		CodeDetails discardDetails = gson.fromJson(s, CodeDetails.class);
                System.out.println("Cocoa date: "+discardDetails.getDate());
                System.out.println("Cocoa time: "+discardDetails.getTime());
                System.out.println("Cocoa batchname: "+discardDetails.getBatchName());
               if(discardData.discardAttendance(discardDetails)){
                   pw.write("Discard Success");
               }else{
                   pw.write("fail");
               }
               
                	
	}
      
        // 5. List of all Manual trainees
    protected void manualTrainees(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                HostServer manualData=new HostServer();
		System.out.println("In validateLogin..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
                ArrayList<TraineeDetails> list= manualData.manualTrainees();
                String result = gson.toJson(list);
                pw.write(result);
                
                	
	}
    
    // 6. uopdate manual attendance 
     protected void updateManualAttendace(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                HostServer logdata=new HostServer();
		System.out.println("In validateLogin..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
                ArrayList<TraineeDetails> traineeList = new ArrayList<>();
		ArrayList requestList = gson.fromJson(s, ArrayList.class);
                System.out.println("== "+requestList.get(0));
                System.out.println("** "+gson.toJson(requestList.get(0)));
               for(int i=0;i<requestList.size();i++){
                   TraineeDetails trainee = gson.fromJson(gson.toJson(requestList.get(0)), TraineeDetails.class);
                   traineeList.add(trainee);
               }
               System.out.println("trainees "+ traineeList);
              if ( logdata.updateManualAttendace(traineeList)){
                  pw.write("success");
              }else{
                  pw.write("fail");
              }
                	
	}
      
      // Attendance client
      //1. Get active code
      protected void getActiveCode(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                AttendanceClientService codeActive=new AttendanceClientService();
		System.out.println("In get active code..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		CodeDetails userdata = gson.fromJson(s, CodeDetails.class);
                
                System.out.println("Code Date: "+userdata.getDate());
                CodeDetails code = codeActive.getActiveCode(userdata.getDate());
            
                String result=gson.toJson(code);
                pw.write(result);

                	
	}
      
      
      // 2. Insert trainee into the DB
        protected void insertTrainee(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                AttendanceClientService logdata=new AttendanceClientService();
		System.out.println("In inserting trainee in DB..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		TraineeDetails trainee = gson.fromJson(s, TraineeDetails.class);
                System.out.println("Cocoa machine name: "+trainee.getMachineName());
                System.out.println("Cocoa batch name: "+trainee.getBatchName());
                
               if( logdata.insertTrainee(trainee)){
                   pw.write("Success");
               }
               else{
                   pw.write("fail");
               }

                	
	}
      
      
    // 1. Login validation
    protected void validateLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception 
	{
           
                Login logdata=new Login();
		System.out.println("In validateLogin..");
		PrintWriter pw = response.getWriter();
		/* Convert the stream of bytes to java string object */
		String s = this.convertStream(request);
		System.out.println("converted string : "+s);
		/* Read Json string and convert to Java Objects */
		Gson gson = new Gson();
		UserBean userdata = gson.fromJson(s, UserBean.class);
                System.out.println("Cocoa Username: "+userdata.getUsername());
                System.out.println("Cocoa Password: "+userdata.getPassword());
                if(logdata.validateLogin(userdata))
                {
                String result=gson.toJson(userdata);
                pw.write("Login Success");
                }
                else
                {
                pw.write("Login Failure");
                }
                	
	}
    
    
 /*-----------------------------------------------------------------------------------------------------------------------------------*/
                                        // Helper method for i/o stream conversion to string
    
    String convertStream(HttpServletRequest request) 
	 {

	        StringBuilder str = new StringBuilder();
	        BufferedReader buff = null;
	        String body = null;
	        try {
	            InputStream input = request.getInputStream();
	            if (input != null) {
	                buff = new BufferedReader(new InputStreamReader(input));
	                char[] charBuffer = new char[128];
	                int bytesRead = -1;
	                while ((bytesRead = buff.read(charBuffer)) > 0) {
	                    str.append(charBuffer, 0, bytesRead);
	                }
	            } else {
	                System.out.println("Input stream is empty... ");
	                str.append("");
	            }
	            body = str.toString();
	            System.out.println("Body : " + body);
	        } catch (Exception e) {	        }
			return body;
	 }
}
