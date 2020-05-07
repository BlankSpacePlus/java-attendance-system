package operator;

import java.io.IOException;

import static operator.Constant.*;

import java.io.FileReader;
import java.util.Calendar;
import java.util.Scanner;

import user.*;

/**
 * EmployeeSystem's main class, which can control this system.
 * System will ensure your identity first.
 * enter "0" if you want to exit.
 * enter "1" if you are a normal employee.
 * enter "2" if you are a manager.
 * After registering, you can do all things what you can do based on your identity.
 * Note: employees don't have their own password, however, managers must use their own password to register.
 * When employee execute this program, he will be in a dead loop unless entering "0".
 * enter "1", employee can register.
 * enter "2", employee can quit(Not exiting from this system).
 * enter "3", employee can get his information about punching card.
 * If you are a manager, you can do more things:
 * enter "4", manager can view all employees' information.
 * enter "5", manager can view all employees' attendance information.
 * enter "6", manager can add one single employee.
 * enter "7", manager can remove one single employee.
 * enter "8", manager can change his password.
 * If you are a manager, you must register as a manager;
 * instead, if you aren't a manager and you are an employee, you must register as an employee.
 * In this class, most functions are defined as private because of encapsulation.
 * This class import static com.operator.IOController class.
 */
public class EmployeeSystem {
    
    //create a scanner to get user's input in the system.
    private Scanner scanner;
    //one-one relationship.
    private Company company;
    //create a calendar to get time and control time in this system.
    private Calendar calendar;
    //create an employee to restore an employee who has registered and hasn't been quit.
    private Employee landingEmployee;
    
    //initialization block
    {
        calendar = Calendar.getInstance();
        scanner = new Scanner(System.in);
        company = Company.getInstance();
        landingEmployee = null;
        try (var reader = new Scanner(new FileReader(EMPLOYEE_FILENAME))) {
            var line = reader.nextLine();
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                var array = line.split("#");
                Employee newEmployee;
                if (Boolean.parseBoolean(array[array.length-1]) == true) {
                    newEmployee = new Manager(array[0], array[1], Integer.parseInt(array[2]),
                            Integer.parseInt(array[3]), Integer.parseInt(array[4]),
                            IOController.readPassword(array[0]));
                } else {
                    newEmployee = new Employee(array[0], array[1], Integer.parseInt(array[2]),
                            Integer.parseInt(array[3]), Integer.parseInt(array[4]));                    
                }
                company.initializeEmployee(newEmployee);
            }
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    //main()
    public static void main(String[] args) throws IOException, InterruptedException {
        var system = new EmployeeSystem();
        system.run();
        system.scanner.close();
    }
    
    /**
     * The main() call this function to run.
     * The execution is in a dead cycle unless input special things.
     * @throws IOException
     */
    public void run() throws IOException {
        for(var identity = this.getIdentity(); identity != 0; identity = this.getIdentity()) {
            register(identity);
            if (landingEmployee != null) {
                runUser(identity);                
            }
        }        
    }
    
    //to ensure the user's identity.
    private int getIdentity() throws IOException {
        while(true) {
            try {
                System.out.println();
                printIdentityChoiceMenu();
                var choice = Integer.parseInt(scanner.next());
                System.out.println();
                if (0 <= choice && choice <= 2) {
                    return choice;
                }
                System.out.println("Invalid choice:  " + choice);
            } catch (NumberFormatException exception) {
                System.out.println(exception);
            }
        }
    }
    
    private void runUser(int identity) throws IOException {
        //use flag to ensure we can break outer cycle easily.
        flag:
        for(var choice = this.getChoice(identity); ; choice = this.getChoice(identity)) {
            switch (choice) {
                case 0:
                    this.landingEmployee = null;
                    break flag;
                case 1:
                    this.attend();
                    break;
                case 2:
                    this.quit();
                    break;
                case 3:
                    this.getInformation();
                    break;
                case 4:
                    this.viewInformation();
                    break;
                case 5:
                    this.viewAttendInformation();
                    break;
                case 6:
                    this.addNewEmployee();
                    break;
                case 7:
                    this.removeEmployee();
                    break;
                case 8:
                    this.changePassword();
                    break;
            }
        }        
    }
    
    private int getChoice(int identity) throws IOException {
        while(true) {
            try {
                System.out.println();
                int choice = 0;
                printUserMenu(identity);
                choice = Integer.parseInt(scanner.next());
                System.out.println();
                if (identity == 1 && !(landingEmployee instanceof Manager)) {
                    if (0 <= choice && choice <= 3) {
                        return choice;
                    }                  
                } else if (identity == 2 && landingEmployee instanceof Manager) {
                    if (0 <= choice && choice <= 8) {
                        return choice;
                    }                      
                } else {
                    System.err.println("Warning: Invalid Operation!");
                    return 0;
                }
                System.out.println("Invalid choice:  " + choice);
            } catch (NumberFormatException exception) {
                System.out.println(exception);
            }
        }
    }
    
    private void register(int identity) throws IOException {    
        try {
            for (var i = 2; i >= 0; i--) {
                System.out.print("ID>");
                var id = scanner.next(); 
                var employee = company.getEmployee(id);
                if (identity ==1 && employee != null && !(employee instanceof Manager)) {
                    landingEmployee = employee;
                    System.out.println("Register successfully.");  
                    return;
                } else if (identity == 2 && employee != null && employee instanceof Manager) {
                    System.out.print("Password>");
                    var password = scanner.next();
                    if (((Manager)employee).getPassword().equals(password)){
                        landingEmployee = employee;
                        System.out.println("Register successfully.");  
                        return;                            
                    }
                } else {
                    System.out.println("Fail to register. You have " + (i) +" times to try.");
                }                
            }

        } catch (NumberFormatException exception) {
            System.out.println(exception);
        }
    }
   
    private void attend() throws IOException {
        if (judgePunchingCard()) {
            System.out.println("Exception: You have attended today.");     
            return;
        }
        IOController.writeStart(landingEmployee);                
    }
    
    private void quit() throws IOException {
        if (!judgePunchingCard()) {
            System.out.println("Exception: You haven't attended today.");      
            return;
        }
        IOController.writeEnd(landingEmployee);                      
    }
    
    private void getInformation() throws IOException {    
        if (judgePunchingCard()) {
            System.out.println("Nice: You have attended today.");                    
        } else {
            System.out.println("Warning: You haven't attended today.");
        }   
    }
    
    private boolean judgePunchingCard()  throws IOException {
        var historyList = IOController.readHistory();
        this.calendar = Calendar.getInstance();
        for (var line : historyList) {
            var array = line.split("#");
            if ((array[0].equals(landingEmployee.getId())) && 
                    (this.calendar.get(Calendar.DATE) == Integer.parseInt(array[3]))) {
                return true;
            }
        }
        return false;
    }
      
    private void viewInformation() {
        if (landingEmployee instanceof Manager) {
            ((Manager) landingEmployee).viewAllEmployees();
        }
    }
    
    private void viewAttendInformation() throws IOException {
        var array = new String[10];
        for (String line : IOController.readHistory()) {
            array = line.split(SPLIT_CHARACTER);
            System.out.println(array[0] + ": " + array[2] + "/" + array[3] + "/" + array[1]
                    + ", Start Time: " + array[4] + ":" + array[5] + ":" + array[6]
                    + ", End Time: " + array[7] + ":" + array[8] + ":" + array[9]);
        }
    }
    
    private void addNewEmployee() throws IOException {
        try {
            System.out.print("Please enter the newer's ID Number.\nNumber>");
            var id = scanner.next();
            System.out.print("Please enter the newer's Name.\nName>");
            var name = scanner.next();
            var employee = new Employee(id, name, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            ((Manager) landingEmployee).addEmployee(employee);
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }       
    }
    
    private void removeEmployee() throws IOException {
        try {
            System.out.print("Please enter the newer's ID Number.\nNumber>");
            ((Manager) landingEmployee).fireEmployee(scanner.next());
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }       
    }
  
    private void changePassword() throws IOException {
        try {
            System.out.print("Please enter your password.\nOld Password>");
            IOController.judgePassword(scanner.next(), (Manager)landingEmployee);
            System.out.print("Please enter your new password.\nNew Password>");
            var newString1 = scanner.next();
            System.out.print("Please enter your new password Again.\nNew Password>");
            var newString2 = scanner.next();
            if (newString1.equals(newString2)) {
                IOController.changePassword(newString1, (Manager)landingEmployee);
            } else {
                System.out.println("Error! Input inconsistency!");
            }
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }       
    }

    private void printUserMenu(int identity) {
        System.out.print("[0] Go Back Main Menu\n"
                + "[1] Register by punching card\n"
                + "[2] Quit by punching card\n"
                + "[3] View Check-in Information\n");
        if (identity == 2) {
            System.out.print("[4] View all employees' information\n"
                    + "[5] View all employees' information\n"
                    + "[6] Add an employee in this company\n"
                    + "[7] Fire an employee in this company\n"
                    + "[8] Change password\n");
        }
        System.out.print("Choice>");
    }

    private void printIdentityChoiceMenu() {
        System.out.print("[0] Exit the System\n"
                + "[1] I'm an normal employee\n"
                + "[2] I'm an manager\n"
                + "Choice>");    
    }

}
