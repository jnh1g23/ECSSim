package university;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HumanResource {
  private HashMap<Staff, Float> staffSalary = new HashMap<>();

  public void addStaff(
      Staff
          staff) { // Finds salary by multipying random percentage between 9.5% and 10.5% to the
                   // skill level of the staff member. Then adds the staff member to the university
                   // by adding their staff object and salary to a Hashmap
    Float salary = staff.skill * getRandomNumber(0.095, 0.105);
    staffSalary.put(staff, salary);
  }

  public float getRandomNumber(
      double min, double max) { // Generates a random float within the range provided
    Random random = new Random();
    float randomFloat = (random.nextFloat()) * ((float) max - (float) min) + (float) min;
    return randomFloat;
  }

  public Iterator<Staff> getStaff() { // Return an iterator containing all the current staff members
    return (staffSalary.keySet()).iterator();
  }

  public float
      getTotalSalary() { // returns the total salary of the staff members in the hashmap. This is
                         // done through the use of an iterator and the .get method of a hashmap
    float totalsalary = 0;
    Iterator<Staff> iterator = getStaff();
    while (iterator.hasNext()) {
      Staff currentstaffmember = iterator.next();
      totalsalary += staffSalary.get(currentstaffmember);
    }
    return totalsalary;
  }

  public void removeStaff(Staff staff) {
    staffSalary.remove(staff);
  } // removes staff member from the university
}
