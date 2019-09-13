//
//  ViewController.swift
//  AttendanceServer
//
//  Created by Apple Enterprise on 23/10/18.
//  Copyright © 2018 Apple Enterprise. All rights reserved.
//

import Cocoa

class ViewController: NSViewController,NSURLConnectionDataDelegate,NSWindowDelegate{

    var codeData = Data()
    var codeTime = ""
    var codeDate = ""
    @IBOutlet weak var strengthOutlet: NSTextField!
    @IBOutlet weak var timerOutlet: NSTextField!
    @IBOutlet weak var codeOutlet: NSTextField!
   
    @IBOutlet weak var batchNameOutlet: NSTextField!
    @IBOutlet weak var startButtonOutlet: NSButton!
    
    @IBOutlet weak var stopButtonOutlet: NSButton!
    var manualObj = NSStoryboard(name: NSStoryboard.Name(rawValue: "Main"), bundle: nil).instantiateController(withIdentifier: NSStoryboard.SceneIdentifier(rawValue: "manual")) as! ManualAttendance
    var batchArray = NSMutableArray()
    var timer :Timer?
    var time = 60
    override func viewDidLoad() {
        super.viewDidLoad()
        timerOutlet.stringValue = ""
        codeOutlet.stringValue = "Get ready"
        strengthOutlet.stringValue = ""
        codeOutlet.sizeToFit()
        batchNameOutlet.stringValue = ""
        stopButtonOutlet.isEnabled = false
        // Do any additional setup after loading the view.
    }
    
    
   
    
    override func viewDidAppear() {
//        self.view.window?.delegate = self
//        self.view.window?.contentView = manualObj.view
        
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/batchDetails")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        var format = DateFormatter()
        format.dateFormat = "yyyy-MM-dd"
        codeDate = format.string(from: Date())
        

        request.httpBody = codeDate.data(using: .ascii)
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
    }
    
    
    func windowShouldClose(_ sender: NSWindow) -> Bool {
        var alert = NSAlert()
        alert.messageText = "Get ready for manual"
        alert.addButton(withTitle: "Save")
        alert.addButton(withTitle: "Manual")
        alert.addButton(withTitle: "Discard")
        
        if !startButtonOutlet.isEnabled {
            var selection = alert.runModal()
            if selection.rawValue == 1000{
                stopAcceptingAutoAttendance()
                
            }else if selection.rawValue == 1001{
                print("get all manual trainne list")
            }else{
                print("discard previous data")
                discardAttendance()
            }
        }
        
        return true
    }
    
   @objc func incrementTime(){
        if time > 0{
            timerOutlet.stringValue = String("00:\(time)")
            timerOutlet.sizeToFit()
            time -= 1
        }
        else if time == 0{
            timerOutlet.stringValue = String("00:00")
            timerOutlet.sizeToFit()
            time -= 1
            codeOutlet.stringValue = "Get ready for next time"
        }
        else{
            timer?.invalidate()
            stopAcceptingAutoAttendance()
        }
    }
    
    func randomString(){
        var letters = ["HELP","SELP","ZERO","SENT","ASDL","CARL","MAPT","QWEP","YIPE","HALE","YALE"] as? Array<String>
        var randomString = ""
        randomString = letters![Int(arc4random_uniform(11))]
            
        codeOutlet.stringValue = randomString
    }
    
    @IBAction func startAction(_ sender: NSButton) {
       
        timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(incrementTime), userInfo: nil, repeats: true)
         randomString()
        sender.isEnabled = false
        stopButtonOutlet.isEnabled = true
        //httpBody
        var format = DateFormatter()
        format.dateFormat =  "HH:mm"
        codeTime =  format.string(from: Date())
        print("batchname:",batchNameOutlet.stringValue)
        var dict  = NSDictionary(objects: [codeTime,codeDate,codeOutlet.stringValue,batchNameOutlet.stringValue,strengthOutlet.stringValue], forKeys: ["time" as NSCopying,"date" as NSCopying,"code" as NSCopying , "batchName" as NSCopying,"strength" as NSCopying])
        do{
        codeData = try JSONSerialization.data(withJSONObject: dict, options: .init(rawValue: 0))
        }catch{
            print("error while sending code details to backend")
        }
        //Connection
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/insertCode")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"

        request.httpBody = codeData
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
        
    }
    
    
    
    func stopAcceptingAutoAttendance(){
        // this method will change the status of code to inactive.
        // http body
        var dict = NSDictionary(objects: [codeDate,codeTime,codeOutlet.stringValue,batchNameOutlet.stringValue], forKeys: ["date" as NSCopying,"time" as NSCopying,"code" as NSCopying , "batchName" as NSCopying])
        var data = Data()
        do{
            data = try JSONSerialization.data(withJSONObject: dict, options: .init(rawValue: 0))
        }catch{
            print("error while converting into data for disabling the codedetails status")
        }
        // connection for inactive status of code
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/statusChange")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        request.httpBody = data
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
       
    }
    
    
    
    
    @IBAction func stopAction(_ sender: NSButton) {
       timerOutlet.stringValue = ""
        timer?.invalidate()
        var alert = NSAlert()
        alert.messageText = "Get ready for manual"
        alert.addButton(withTitle: "Save")
        alert.addButton(withTitle: "Manual")
        alert.addButton(withTitle: "Discard")
        var selection = alert.runModal()
        if selection.rawValue == 1000{
            stopAcceptingAutoAttendance()
            
        }else if selection.rawValue == 1001{
            print("get all manual trainne list")
            self.view.window?.contentView = manualObj.view
        }else{
            print("discard previous data")
            discardAttendance()
        }
     
    }
    
    
    func discardAttendance(){
        var dict = NSDictionary(objects: [codeDate,codeTime,batchNameOutlet.stringValue], forKeys: ["date" as NSCopying,"time" as NSCopying, "batchName" as NSCopying])
        var data = Data()
        do{
            data = try JSONSerialization.data(withJSONObject: dict, options: .init(rawValue: 0))
        }catch{
            print("error while converting into data for disabling the codedetails status")
        }
        // connection for inactive status of code
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/discardAttendance")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        request.httpBody = data
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
    }
    
    
    func connection(_ connection: NSURLConnection, didReceive data: Data) {
        
        do{


        var dict = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments) as? NSDictionary
        print("dict ", dict)
            if (dict?.count)!>0{
            strengthOutlet.stringValue = dict?.value(forKey: "strength") as! String
                batchNameOutlet.stringValue = dict?.value(forKey: "batchName") as! String
            }
        }catch{
//            print("error while converting by to array")
            print("Insertion: ", String.init(data: data, encoding: .ascii))
            var msg = String.init(data: data, encoding: .ascii) as! String
            if msg == "Success" {
                var alert = NSAlert()
                alert.informativeText = "Details saved successfully"
                alert.addButton(withTitle: "Okay")
                alert.runModal()
                self.view.window?.close()
            }
            else if msg == "Discard Success"{
                startButtonOutlet.isEnabled = true
                stopButtonOutlet.isEnabled = false
                
            }
        }
        
        
        
    }
    func connection(_ connection: NSURLConnection, didReceive response: URLResponse) {
        print("response")
    }
    func connectionDidFinishLoading(_ connection: NSURLConnection) {
        print("loading")
    }
    func connection(_ connection: NSURLConnection, didFailWithError error: Error) {
        print("error",error.localizedDescription)
    }
    


}

