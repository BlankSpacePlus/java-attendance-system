package user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import operator.IOController;

/**
 * EmployeeSystem's company class, which has an ArrayList to save all employees' information and can control this collection(ArrayList).
 * As concurrency, we should use "synchronized" in most functions in this class.
 * This class implements java.io.Serializable.
 * This class uses singleton design pattern.
 * This class import static com.operator.IOController class.
 */
public class Company  implements Serializable {
    
    private static final long serialVersionUID = 3L;
    //Singleton design pattern
    private static Company company;
    //create a collection to save staff(all employee) in this company.
    private ArrayList<Employee> staff;
    
    //private constructor
    private Company() {
        staff = new ArrayList<>();
    }
    
    //to get a singleton by calling the class
    public static synchronized Company getInstance() {
        if (company == null) {
            company = new Company();
        }
        return company;
    }

    //use an String type id to search a special employee.
    public synchronized Employee getEmployee(String id) {
        for (Employee employee : staff) {
            if (employee.getId().equals(id)) {
                return employee;
            }
        }
        return null;
    }
    
    //An manager can use this function to remove a special employee in file and in collection.
    public synchronized void removeEmployee(String id) throws IOException {
        var tempEmployee = getEmployee(id);
        if (tempEmployee != null) {
            staff.remove(tempEmployee);
            IOController.removeEmployee(id);
            //tell the operator everything is OK.
            System.out.println("The employee has been successfully removed.");
        } else {
            //tell the operator something wrong happens.
            System.out.println("There is no such an employee.");
        }
    }
    
    //to add an employee in the collection.
    public synchronized void initializeEmployee(Employee employee) {
        var tempEmployee = getEmployee(employee.getId());
        if (tempEmployee == null) {
            staff.add(employee);
        }
    }
    
    //An manager can use this function to add a special employee in file and in collection.
    public synchronized void addNewEmployee(Employee employee) throws IOException {
        if (getEmployee(employee.getId()) == null) {
            staff.add(employee);
            IOController.writeEmployee(employee);
            System.out.println("The employee has been successfully added.");
        } else {
            System.out.println("That employee has existed.");
        }       
    }
    
    //system can get the collection by calling this function.
    public synchronized ArrayList<Employee> getList() {
        return this.staff;
    }
    
    //Not used now, but it will be useful in the future.
    public synchronized int getNumberOfStaff() {
        return staff.size();
    }

}
