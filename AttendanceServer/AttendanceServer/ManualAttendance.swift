//
//  ManualAttendance.swift
//  AttendanceServer
//
//  Created by Bros on 25/10/18.
//  Copyright © 2018 Apple Enterprise. All rights reserved.
//

import Cocoa

class ManualAttendance: NSViewController,NSTableViewDelegate,NSTableViewDataSource ,NSURLConnectionDataDelegate,NSWindowDelegate{

    @IBOutlet weak var absenteeCountOutlet: NSTextField!
    @IBOutlet weak var tableviewOutlet: NSTableView!
    
    var attnDate = ""
    var attntime = ""
    var traineeArray = NSArray()
    var presentTraineesIndexes = NSMutableArray()
    var presentTraineeArray = NSMutableArray()
    var presentTraineeData = Data()
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do view setup here.
        
    }
    
    override func viewDidAppear() {
        self.view.window?.delegate = self
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/manualTrainees")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
    }
    
    @IBAction func actionSave(_ sender: NSButton) {
        print("sender trainee " , presentTraineesIndexes)
        for i in presentTraineesIndexes{
            presentTraineeArray.add(traineeArray.object(at: i as! Int))
        }
        
        var alert = NSAlert()
        alert.messageText = "To save the details"
        alert.informativeText = "Confirm"
        alert.addButton(withTitle: "Save")
        alert.addButton(withTitle: "Cancel")
        var button = alert.runModal()
        if button.rawValue == 1000{
        do{
           presentTraineeData = try JSONSerialization.data(withJSONObject: presentTraineeArray, options: .init(rawValue: 0))
        }catch{
            print("Error while converting mutable array to data")
        }
        var url = URL(string: "http://localhost:8080/WebApplication1/Controller/updateManualAttendace")
        var request = URLRequest(url: url!)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        request.httpBody = presentTraineeData
        var connection = NSURLConnection(request: request, delegate: self)
        connection?.start()
        }else{
            
        }
        
    }
    func numberOfRows(in tableView: NSTableView) -> Int {
        return traineeArray.count
    }
    func tableView(_ tableView: NSTableView, heightOfRow row: Int) -> CGFloat {
        return CGFloat.init(22)
    }
    
   @objc func getTrainee(_ sender:NSButton){
    if sender.state.rawValue == 1 {
        presentTraineesIndexes.add(sender.tag)
    }else{
        if presentTraineesIndexes.contains(sender.tag){
            presentTraineesIndexes.removeObject(at: presentTraineesIndexes.index(of: sender.tag))
        }
    }
        print(sender.tag)
    print(sender.state)
    }
    
    func tableView(_ tableView: NSTableView, viewFor tableColumn: NSTableColumn?, row: Int) -> NSView? {
        var view = NSView();
        var check = NSButton(checkboxWithTitle: "", target: self, action: #selector(getTrainee(_:)))
       check.tag = row
        
        if (tableColumn?.identifier)!.rawValue == "check"{
            view.addSubview(check)
            
        }else if tableColumn?.identifier.rawValue == "name"{
            var label = NSTextField(string: (traineeArray[row] as AnyObject).value(forKey: "empName") as! String)
            view.addSubview(label)
            return label.viewWithTag(0)
        }
        else if tableColumn?.identifier.rawValue == "empId"{
            let label = NSTextField(string: (traineeArray[row] as AnyObject).value(forKey: "empId") as! String)
            view.addSubview(label)
            return label.viewWithTag(0)
        }
        return view
    }
    
    func connection(_ connection: NSURLConnection, didReceive data: Data) {
        do {
            traineeArray = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments) as! NSArray
            absenteeCountOutlet.stringValue = String(traineeArray.count)
            tableviewOutlet.reloadData()
            print("trainee array",traineeArray)
        }catch{
            print("Error while converting data to array of dictionaries")
            var msg = String.init(data: data, encoding: .ascii) as! String
            if msg == "success" {
                var alert = NSAlert()
                alert.informativeText = "Details saved successfully"
                alert.addButton(withTitle: "Okay")
                alert.runModal()
                self.view.window?.close()
            }
        }
        
    }
    
    func windowShouldClose(_ sender: NSWindow) -> Bool {
        return false
    }
    func connectionDidFinishLoading(_ connection: NSURLConnection) {
        
    }
    func connection(_ connection: NSURLConnection, didFailWithError error: Error) {
        
    }
    
    
    
}
