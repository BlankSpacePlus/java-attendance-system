package user;

import java.io.Serializable;
import java.util.Calendar;

/**
 * EmployeeSystem's employee class, which defines a type of employee.
 * This class implements java.io.Serializable.
 * This class override some functions.
 */
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //An employee must have his own special ID number.
    private final String id;
    //An employee must have his own name, and his name may be same as others.
    private final String name;
    //An employee must be hired in someday.
    private final Calendar hireCalendar;
    //To tell the system whether an employee is a manager or not.
    private boolean isManager;
//    Not used
//    private boolean isRegister;
    
    public Employee(String id, String name, int year, int month, int date) {
        this.id = id;
        this.name = name;
        this.hireCalendar = Calendar.getInstance();
        this.hireCalendar.set(year, month, date);
        this.isManager = false;
//        this.isRegister = false;
    }
    
    //An employee's ID number shouldn't be changed unless he is fired.
    public String getId() {
        return this.id;
    }
    
    //An employee's name shouldn't be changed normally.
    //So we shouldn't allow anyone to change his name in this system.
    //If necessary, he can ask manager to try to renew his information.
    public String getName() {
        return this.name;
    }

//    //Not used
//    public void setRegister() {
//        if (this.isRegister == false) {
//            this.isRegister = true;
//        } else {
//            this.isRegister = false;
//        }
//    }
//    
//    //Not used
//    public boolean getRegister() {
//        return this.isRegister;
//    }
    
    public void setManager() {
        if (this.isManager == false) {
            this.isManager = true;
        } else {
            this.isManager = false;
        }
    }
    
    public boolean getIsManager() {
        return this.isManager;
    }
    
    //Not used
    public Calendar getHireCalendar() {
        return this.hireCalendar;
    }
    
    public int getWorkingYears() {
        var nowCalendar = Calendar.getInstance();
        var workingYears = nowCalendar.get(Calendar.YEAR) - hireCalendar.get(Calendar.YEAR);
        if (nowCalendar.get(Calendar.MONTH) < hireCalendar.get(Calendar.MONTH)) {
            workingYears--;
        } else if (nowCalendar.get(Calendar.MONTH) == hireCalendar.get(Calendar.MONTH) &&
                nowCalendar.get(Calendar.DATE) < hireCalendar.get(Calendar.DATE)) {
            workingYears--;
        }
        return workingYears;
    }
    
    @Override
    public String toString() {
        return "ID: " + id +", Name: " + name + " HireTime： " + hireCalendar.get(Calendar.DATE)
         + "/" + hireCalendar.get(Calendar.MONTH) + "/" +hireCalendar.get(Calendar.YEAR)
         + " WorkingYears: " + getWorkingYears();
    }
    
    //judge whether two employees are the same one by comparing their own ID number.
    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Employee && ((Employee)object).getId() == this.id) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
