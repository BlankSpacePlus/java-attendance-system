package operator;

import static operator.Constant.*;

import java.io.*;
import java.util.*;

import user.*;

/**
 * EmployeeSystem's important class, which can control IO in this system.
 * When an employee registers, the file named "History" will be written an record about his information.
 * Save format:
 * ID#Year#Month#Day#StartHour#StartMinute#StartSecond#EndHour#EndMinute#EndSecond
 * When an employee is created, the file named "Staff" will be written an record about his information.
 * Save format:
 * ID#Name#Year#Month#Date#Identity(IsManager)
 * When a manager is created, the file named "ManagerInfo" will be written an record about his information.
 * Save format:
 * ID#Password
 * When changing a line in the file, it's very very very complex.
 * This class import static com.operator.IOController class.
 * @author BlankSpace
 * @version 2.0
 * @time 2019/7/13
 */
public class IOController {
    
    //restore staff's punching card to attend information.
    public static void writeStart(Employee employee) throws IOException {
        var calendar = Calendar.getInstance();
        var line = new String (employee.getId() + SPLIT_CHARACTER + calendar.get(Calendar.YEAR) + SPLIT_CHARACTER 
                    + calendar.get(Calendar.MONTH) + SPLIT_CHARACTER + calendar.get(Calendar.DATE) + SPLIT_CHARACTER 
                    + calendar.get(Calendar.HOUR_OF_DAY) + SPLIT_CHARACTER + calendar.get(Calendar.MINUTE) + SPLIT_CHARACTER
                    + calendar.get(Calendar.SECOND) + SPLIT_CHARACTER + calendar.get(Calendar.HOUR_OF_DAY) + SPLIT_CHARACTER 
                    + calendar.get(Calendar.MINUTE) + SPLIT_CHARACTER + calendar.get(Calendar.SECOND));
        try (var bufferedWriter = new BufferedWriter(new FileWriter(HISTORY_FILENAME, true))) {
            bufferedWriter.write(line + "\r\n");
        }
    }
    
    //restore staff's punching card to leave information.
    public static void writeEnd(Employee employee) throws IOException {
        var historyList = IOController.readHistory();
        try (   var overWriter = new BufferedWriter(new FileWriter(HISTORY_FILENAME));
                var bufferedWriter = new BufferedWriter(new FileWriter(HISTORY_FILENAME, true))) {
            var calendar = Calendar.getInstance();           
            var tempStringBuilder = new StringBuilder();
            var array = new String[10]; 
            overWriter.write("");
            for (var line : historyList) {
                array = line.split(SPLIT_CHARACTER);
                if (array[0].equals(employee.getId())) {
                    array[7] = Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).toString();
                    array[8] = Integer.valueOf(calendar.get(Calendar.MINUTE)).toString();
                    array[9] = Integer.valueOf(calendar.get(Calendar.SECOND)).toString();
                    tempStringBuilder.delete(0, tempStringBuilder.length());
                    for (var i = 0; i < 9; i++) {
                        tempStringBuilder.append(array[i] + "#");
                    }
                    tempStringBuilder.append(array[9]);
                    bufferedWriter.write(tempStringBuilder.toString() + "\r\n");
                    continue;
                }
                bufferedWriter.write(line + "\r\n");
            }
        }        
    }
    
    //to get all information in file.
    public static ArrayList<String> readHistory() throws IOException {
        try (var scanner = new Scanner(new FileReader(HISTORY_FILENAME))) {
            var historyList = new ArrayList<String>();
            while (scanner.hasNextLine()) {
                historyList.add(scanner.nextLine());
            }
            return historyList;
        }
    }
    
    //to get all information in file.
    private static ArrayList<String> readStaff() throws IOException {
        try (var scanner = new Scanner(new FileReader(EMPLOYEE_FILENAME))) {
            var staffList = new ArrayList<String>();
            while (scanner.hasNextLine()) {
                staffList.add(scanner.nextLine());
            }
            return staffList;
        }
    }
    
    //to get all information in file.
    private static ArrayList<String> readManager() throws IOException {
        try (var scanner = new Scanner(new FileReader(EMPLOYEE_FILENAME))) {
            var managerList = new ArrayList<String>();
            while (scanner.hasNextLine()) {
                managerList.add(scanner.nextLine());
            }
            return managerList;
        }
    }
    
    //to change an information in file.
    public static void writeEmployee(Employee employee) throws IOException {
        var calendar = Calendar.getInstance();
        var line = new String (employee.getId() + SPLIT_CHARACTER + employee.getName() + SPLIT_CHARACTER
                    + calendar.get(Calendar.YEAR) + SPLIT_CHARACTER + calendar.get(Calendar.MONTH) + SPLIT_CHARACTER 
                    + calendar.get(Calendar.DATE) + SPLIT_CHARACTER  + employee.getIsManager());
        try (var bufferedWriter = new BufferedWriter(new FileWriter(EMPLOYEE_FILENAME, true))) {
            bufferedWriter.write(line + "\r\n");
        }        
    }
    
    //to change an information in file.
    public static void removeEmployee(String id) throws IOException {
        var staffList = readStaff();
        try (   var overWriter = new BufferedWriter(new FileWriter(EMPLOYEE_FILENAME));
                var bufferedWriter = new BufferedWriter(new FileWriter(EMPLOYEE_FILENAME, true))) {           
            overWriter.write("");
            for (var line : staffList) {
                if (line.startsWith(id)) {
                    continue;
                }
                bufferedWriter.write(line + "\r\n");
            }
        }         
    }

    //to get an information in file.
    public static String readPassword(String id) throws IOException {
        try (var scanner = new Scanner(new FileReader(MANAGER_FILENAME))) {
            var array = new String[2];
            while (scanner.hasNextLine()) {
                array = scanner.nextLine().split(SPLIT_CHARACTER);
                if (id.equals(array[0])) {
                    return array[1];
                }
            }
            System.out.println("File_Save_Error!");
            return "";
        }
    }
    
    //to judge if the unchecked password is correct.
    public static boolean judgePassword(String unCheckedPassword, Manager manager) throws IOException {
        var array = new String[2];
        try (var scanner = new Scanner(new FileReader(MANAGER_FILENAME))) {
            var managerList = readManager();            
            for (String line : managerList) {
                array = line.split(SPLIT_CHARACTER);
                if (array[0].equals(manager.getId())) {
                    if (array[1].equals(unCheckedPassword)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            System.out.println("File_Save_Error!");
            return false;
        }        
    }
    
    //to change an information in file.
    public static void changePassword(String password, Manager manager) throws IOException {
        try (   var overWriter = new BufferedWriter(new FileWriter(MANAGER_FILENAME));
                var bufferedWriter = new BufferedWriter(new FileWriter(MANAGER_FILENAME, true))) {
            var managerList = readManager();
            var array = new String[2];
            overWriter.write("");
            for (var line : managerList) {
                if (array[0].equals(manager.getId())) {
                    continue;
                }
                bufferedWriter.write(line + "\r\n");
            }
        }         
    }

}
