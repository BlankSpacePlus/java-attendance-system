package user;

import java.io.IOException;

/**
 * EmployeeSystem's manager class, which defines a type of manager.
 * This class implements java.io.Serializable.
 * This class extends Employee, it means that Manager is a special type of Employee.
 * A manager can do all things what a normal employee can do, and he can do more powerful things.
 * @author BlankSpace
 * @version 2.0
 * @time 2019/7/13
 */
public class Manager extends Employee {
    //When superclass implements java.io.Serializable, 
    //its subclass will implements java.io.Serializable automatically
    private static final long serialVersionUID = 2L;
    
    //An manager must has his own password to ensure his identity.
    private String password;
    
    //one(company)-to-many(manager) relationship
    private Company company;
    
    public Manager(String id, String name, int year, int month, int date, String password) {
        super(id, name, year, month, date);
        this.password = password;
        this.company = Company.getInstance();
        //change variable isManager to true
        this.setManager();
    }
    
    //Although ID number can't change, manager's password should be changeable.
    public void setPassword(String password) {
        this.password = password;
    }
    
    //When manager change his password, system should get his password first.
    public String getPassword() {
        return this.password;
    }
    
    //An manager can fire someone.
    public void fireEmployee(String id) throws IOException {
        company.removeEmployee(id);
    }
    
    //An manager can view all employees' information
    public void viewAllEmployees() {
        for (var employee : company.getList()) {
            //class Employee's toString() has been overridden, 
            //so then all employee's information will be printed.
            System.out.println(employee);
        }
    }

    //An manager can add someone.
    public void addEmployee(Employee employee) throws IOException {
        company.addNewEmployee(employee);
    }
    
    //Override toString() again to show manager's identity.
    @Override
    public String toString() {
        return super.toString() + " Identity->Manager";
    }

}
