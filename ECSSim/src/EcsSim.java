import facilities.Facility;
import facilities.buildings.*;
import university.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class EcsSim {
  private University university = new University(2000);
  private ArrayList<Staff> staffMarket = new ArrayList<>();
  private BufferedReader reader;

  public String getLine() { // This method gets a line of string using bufferedReader
    try {
      return reader.readLine();
    } catch (IOException a) {
      return null;
    }
  }

  public void initialiseBufferedReader(String filename) { // initialise bufferedReader to read a
    // certain text file
    try {
      reader = new BufferedReader(new FileReader(filename));
    } catch (FileNotFoundException a) {
      System.err.println("File not Found");
    }
  }

  public void addStaffConfiguration(String filename) { // Uses the bufferedReader to read the
    // staff file one line at a time and splits the name and skill level. It then creates a
    // new staff using these details and adds them to staff market
    initialiseBufferedReader(filename);
    String currentline;
    while ((currentline = getLine()) != null) {
      String[] staffConfiguration = currentline.split("\\((.*?)\\)");
      String staffConfigurationString = currentline;
      String staffName = staffConfiguration[0].strip();
      int staffSkill =
          Integer.valueOf(
              staffConfigurationString.substring(
                  staffConfigurationString.indexOf("(") + 1,
                  staffConfigurationString.indexOf(")")));
      staffMarket.add(new Staff(staffName, staffSkill));
    }
  }

  public static void main(String[] arguments) {
    EcsSim ecsSim = new EcsSim();
    if (arguments.length != 3) {
      System.err.println("Not enough arguments provided");
    } else {
      ecsSim.addStaffConfiguration(arguments[0]);
      ecsSim.university.setBudget(Integer.valueOf(arguments[1]));
      ecsSim.simulate(Integer.valueOf(arguments[2]));
    }
  }

  public void simulate() {
    simulateBeginning();
    simulateDuringTheYear();
    simulateEnd();
  }

  public void simulate(int years) {
    addStaffConfiguration("staff.txt");
    for (int i = 0; i < years; i++) {
      try {
        Thread.sleep(5);
        simulate();
        System.out.println("Year " + (i + 1));
        System.out.println("Budget " + university.getBudget());
        System.out.println("Students" + university.getEstate().getNumberOfStudents());
        System.out.println("Staff " + findSizeOfIterator(university.getHumanResource().getStaff()));
        System.out.println("Reputation " + university.getReputation());
      } catch (InterruptedException e) {
        System.exit(0);
      }
    }
  }

  public void simulateBeginning() {
    BuildOrUpgradeFacilities();
    increaseBudgetBasedOnStudents();
    hireAdditionalStaff();
  }

  public void hireAdditionalStaff() throws IndexOutOfBoundsException { // as 21 is the maximum
    // number of students a staff can instruct without losing overall stamina in the year,
    // every time numofstudents/21 is greater than the number of staff, a new staff is hired
    // if available
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    int sizeOfIterator = findSizeOfIterator(staffIterator);
    try {
      if (0 < university.getEstate().getNumberOfStudents()
          && university.getEstate().getNumberOfStudents() < 21) {
        university.getHumanResource().addStaff(staffMarket.get(0));
        staffMarket.remove(0);
      } else {
        while (sizeOfIterator < university.getEstate().getNumberOfStudents() / 21) {

          university.getHumanResource().addStaff(staffMarket.get(0));
          staffMarket.remove(0);
          staffIterator = university.getHumanResource().getStaff();
          sizeOfIterator = findSizeOfIterator(staffIterator);
        }
      }
    } catch (IndexOutOfBoundsException a) {
      System.err.println("No staff left to hire");
      System.exit(0);
    }
  }

  public void simulateDuringTheYear() { // This method simulates the instructing of students
    // throughout the year, if there are less than 21 students, a single staff member will
    // instruct them. Each staff member will instruct 21 students
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    int sizeOfIterator = findSizeOfIterator(staffIterator);
    int numOfStudentsPerStaff = 0;
    try {
      numOfStudentsPerStaff = 21 / sizeOfIterator;
    } catch (ArithmeticException a) {
      System.out.println(sizeOfIterator);
      System.err.println("No staff are hired.");
      System.exit(0);
    }
    staffIterator = university.getHumanResource().getStaff();
    int reputationPoints;
    while (staffIterator.hasNext()) {
      if (university.getEstate().getNumberOfStudents() < 21) {
        Staff staff = staffIterator.next();
        reputationPoints = staff.instruct(university.getEstate().getNumberOfStudents());
      } else {
        Staff staff = staffIterator.next();
        reputationPoints = staff.instruct(21);
      }
      university.increaseReputation(reputationPoints);
    }
  }

  public void deductReputationPoints() { // As each staff member instructs 21 students, the
    // number of uninstructed students is the numofstudents % 21. This number will then be
    // deducted from the universities reputation
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    int sizeOfIterator = findSizeOfIterator(staffIterator);
    int numOfExtraStudents = 0;
    try {
      if (university.getEstate().getNumberOfStudents() > 21) {
        numOfExtraStudents = (university.getEstate().getNumberOfStudents()) % 21;
      }
    } catch (ArithmeticException a) {
    }
    university.decreaseReputation(numOfExtraStudents);
  }

  public int findSizeOfIterator(Iterator<Staff> iterator) { // This method finds the size of a
    // Staff iterator that is passed through. The size is found through a while loop
    int i = 0;
    while (iterator.hasNext()) {
      i += 1;
      iterator.next();
    }
    return i;
  }

  public void BuildOrUpgradeFacilities() { // The method firsts check if there are any possible
    // upgrades that can be done to the existing facilities in the estate. Then it checks if
    // facilities can be built, while prioritising the buildings that can be built to maximum
    // level since they are the most cost efficient.
    // Buildings are checked if they can be built by calculating if the income after building
    // the building is greater than the outcomes after building it. It is also checked if you
    // can currently buy the building with the budget you have.
    // In order to increase the number of students, the type of building with the lowest
    // number of students is prioritised through the use of a function which returns a string
    // containing the lowest capacity building
    int income = (university.getEstate().getNumberOfStudents()) * 10;
    Boolean buildOrUpgrade = true;
    Facility[] array = university.getEstate().getFacilities();
    while (buildOrUpgrade && university.getBudget() > 0) {
      int upgradeReady = 1;
      while (upgradeReady > 0) {
        upgradeReady = getFacilityWorthUpgrading(getCapacityOfFacility(0));
        if (upgradeReady < 0) {
          buildOrUpgrade = false;
        }
      }
      String lowestCapacityFacility = getCapacityOfFacility(0);
      if (lowestCapacityFacility.equals("Hall")) {
        if (((university.getBudget() + income + (6 * 10))
                >= (100
                    + getEndOfYearCosts("Hall", 1)
                    + (((university.getEstate().getNumberOfStudents() + 6) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 100)) {
          university.build("Hall", "hall");
          buildOrUpgrade = true;
        }
      } else if (lowestCapacityFacility.equals("Lab")) {
        if (((university.getBudget() + income + (80 * 10))
                >= (4500
                    + getEndOfYearCosts("Lab", 5)
                    + (((university.getEstate().getNumberOfStudents() + 80) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 4500)) {
          university.build("Lab", "lab");
          for (Facility facility : array) {
            if (facility instanceof Lab) {
              if (((Lab) facility).getLevel() == 1) {
                try {
                  university.upgrade((Lab) facility);
                  university.upgrade((Lab) facility);
                  university.upgrade((Lab) facility);
                  university.upgrade((Lab) facility);
                } catch (Exception a) {
                  System.err.println(a);
                }
              }
            }
          }
          buildOrUpgrade = true;
        } else if (((university.getBudget() + income + (5 * 10))
                >= (300
                    + getEndOfYearCosts("Lab", 1)
                    + (((university.getEstate().getNumberOfStudents() + 5) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 300)) {
          university.build("Lab", "lab");
          buildOrUpgrade = true;
        }
      }
      if (lowestCapacityFacility.equals("Theatre")) {
        if (((university.getBudget() + income + (320 * 10))
                >= (4200
                    + getEndOfYearCosts("Theatre", 6)
                    + (((university.getEstate().getNumberOfStudents() + 320) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 4200)) {
          university.build("Theatre", "theatre");
          for (Facility facility : array) {
            if (facility instanceof Theatre) {
              if (((Theatre) facility).getLevel() == 1) {
                try {
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                } catch (Exception a) {
                  System.err.println(a);
                }
              }
            }
          }
          buildOrUpgrade = true;
        } else if (((university.getBudget() + income + (160 * 10))
                >= (3000
                    + getEndOfYearCosts("Theatre", 5)
                    + (((university.getEstate().getNumberOfStudents() + 160) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 3000)) {
          for (Facility facility : array) {
            if (facility instanceof Theatre) {
              if (((Theatre) facility).getLevel() == 1) {
                try {
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                  university.upgrade((Theatre) facility);
                } catch (Exception a) {
                  System.err.println(a);
                }
              }
            }
          }
          university.build("Theatre", "theatre");
          buildOrUpgrade = true;
        } else if (((university.getBudget() + income + (10 * 10))
                >= (200
                    + getEndOfYearCosts("Theatre", 1)
                    + (((university.getEstate().getNumberOfStudents() + 10) / 21) * 0.105 * 100)))
            && (university.getBudget() >= 200)) {
          university.build("Theatre", "theatre");
          buildOrUpgrade = true;
        }
      }
    }
  }

  public int getFacilityWorthUpgrading(String facilityType) { // This method loops through the
    // current facilities and determines whether the university can upgrade a building. Halls
    // are not worth upgrading at all as the increase in students is the same if you are
    // building it with the same price. Also, if you build halls, you gain more reputation
    // instead of upgrading
    // Labs are only worth upgrading to level 5 as the increase in students is more
    // cost-efficient than building a separate lab. Level 4 provides the same increaase in
    // students for the same cost but upgrading gives less reputation.
    // Theatres are worth upgrading to level 5 and 6 for the same reasons as the labs.
    // To calculate if the university can afford to upgrade the facility, we need to check if
    // the income after building the facility is greater tha the costs after building the
    // facility, we also need to check if the current budget is greater than the
    // manufacturing/upgrade costs
    // A facility type is passed into the function, which is the facility with the current
    // lowest capacity, then the method loops the facilities and upgrades facilities when
    // necessary
    Facility[] array = university.getEstate().getFacilities();
    int income = (university.getEstate().getNumberOfStudents()) * 10;
    if (facilityType.equals("Hall")) {
      return -1;
      /*
      for (Facility facility : array){

          if (facility instanceof Hall){
              if (((Hall) facility).getLevel()==3 && (university.getBudget()+income+(24*10)
               ) >= (400+getEndOfYearCosts("Hall",4))){
                  try{
                      university.upgrade((Hall)facility);
                      return 1;
                      }
                  catch (Exception a){
                      System.err.println(a);
                  }
              }
              else if (((Hall) facility).getLevel()==2 && (university.getBudget()+income+
              (36*10))  >= (700+getEndOfYearCosts("Hall",4))){
                  try{
                      university.upgrade((Hall)facility);
                      university.upgrade((Hall)facility);
                      return 1;
                  }
                  catch (Exception a){
                      System.err.println(a);
                  }
              }
              else if (((Hall) facility).getLevel()==1 && (university.getBudget()+income+
              (42*10)) >= (900+getEndOfYearCosts("Hall",4))){
                  try{
                      university.upgrade((Hall)facility);
                      university.upgrade((Hall)facility);
                      university.upgrade((Hall)facility);
                      return 1;
                  }
                  catch (Exception a){
                      System.err.println(a);
                  }
              }
          }
      }
      */
    }
    if (facilityType.equals("Lab")) {
      for (Facility facility : array) {
        if (facility instanceof Lab) {
          if (((Lab) facility).getLevel() == 4
              && ((university.getBudget() + income + (40 * 10))
                  >= (1500
                      + getEndOfYearCosts("Lab", 5)
                      + (((university.getEstate().getNumberOfStudents() + 40) / 21) * 0.105 * 100)))
              && (university.getBudget() >= 1500)) {
            try {
              university.upgrade((Lab) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Lab) facility).getLevel() == 3
              && ((university.getBudget() + income + (60 * 10))
                  >= (2700
                      + getEndOfYearCosts("Lab", 5)
                      + (((university.getEstate().getNumberOfStudents() + 60) / 21) * 0.105 * 100)))
              && (university.getBudget() >= 2700)) { // l3 to l5
            try {
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Lab) facility).getLevel() == 2
              && ((university.getBudget() + income + (70 * 10))
                  >= (3600
                      + getEndOfYearCosts("Lab", 5)
                      + (((university.getEstate().getNumberOfStudents() + 70) / 21) * 0.105 * 100)))
              && (university.getBudget() >= 3600)) { // l2 to l5
            try {
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Lab) facility).getLevel() == 1
              && ((university.getBudget() + income + (75 * 10))
                  >= (4200
                      + getEndOfYearCosts("Lab", 5)
                      + (((university.getEstate().getNumberOfStudents() + 75) / 21) * 0.105 * 100)))
              && (university.getBudget() >= 4200)) {
            try {
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              university.upgrade((Lab) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          }
        }
      }
    }
    if (facilityType.equals("Theatre")) {
      for (Facility facility : array) {
        if (facility instanceof Theatre) {
          if (((Theatre) facility).getLevel() == 5
              && ((university.getBudget() + income + (160 * 10))
                  >= (1200
                      + getEndOfYearCosts("Theatre", 6)
                      + (((university.getEstate().getNumberOfStudents() + 160) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 1200)) {
            try {
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 4
              && ((university.getBudget() + income + (240 * 10))
                  >= (2200
                      + getEndOfYearCosts("Theatre", 6)
                      + (((university.getEstate().getNumberOfStudents() + 240) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 2200)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 3
              && ((university.getBudget() + income + (280 * 10))
                  >= (3000
                      + getEndOfYearCosts("Theatre", 6)
                      + (((university.getEstate().getNumberOfStudents() + 280) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 3000)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 2
              && ((university.getBudget() + income + (300 * 10))
                  >= (3600
                      + getEndOfYearCosts("Theatre", 6)
                      + (((university.getEstate().getNumberOfStudents() + 300) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 3600)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 1
              && ((university.getBudget() + income + (310 * 10))
                  >= (4000
                      + getEndOfYearCosts("Theatre", 6)
                      + (((university.getEstate().getNumberOfStudents() + 310) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 4000)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 3
              && ((university.getBudget() + income + (120 * 10))
                  >= (1800
                      + getEndOfYearCosts("Theatre", 5)
                      + (((university.getEstate().getNumberOfStudents() + 120) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 1800)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 2
              && ((university.getBudget() + income + (140 * 10))
                  >= (2400
                      + getEndOfYearCosts("Theatre", 5)
                      + (((university.getEstate().getNumberOfStudents() + 140) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 2400)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          } else if (((Theatre) facility).getLevel() == 1
              && ((university.getBudget() + income + (150 * 10))
                  >= (2800
                      + getEndOfYearCosts("Theatre", 5)
                      + (((university.getEstate().getNumberOfStudents() + 150) / 21)
                          * 0.105
                          * 100)))
              && (university.getBudget() >= 2800)) {
            try {
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              university.upgrade((Theatre) facility);
              return 1;
            } catch (Exception a) {
              System.err.println(a);
            }
          }
        }
      }
    }
    return -1;
  }

  public String getCapacityOfFacility(int position) { // This method returns the facility with
    // the lowest capacity. This is done by getting the array of the number of students per
    // type of facility.
    // The array is then sorted in increasing order. The lowest number is then compared with
    // each element in the unsorted array and if they match the method will return the
    // facility that matches the index of the lowest number.
    int[] numberOfStudentsArray = university.getEstate().getNumberOfStudentsArray();
    int[] sortedNumberOfStudentsArray = university.getEstate().getNumberOfStudentsArray();
    int zeroCounter = 0;
    Arrays.sort(sortedNumberOfStudentsArray);
    String facility = "";
    for (int n : numberOfStudentsArray) {
      if (n == 0) {
        zeroCounter += 1;
      }
    }
    for (int i = 0; i < numberOfStudentsArray.length; i++) {
      if (numberOfStudentsArray[i] == sortedNumberOfStudentsArray[position]) {
        if (i == 0) {
          facility = "Hall";
        } else if (i == 1) {
          facility = "Theatre";
        } else {
          facility = "Lab";
        }
      }
    }
    if (zeroCounter == 3) {
      return ("Hall");
    } else {
      return facility;
    }
  }

  public float getEndOfYearCosts(String type, int level) { // Returns the end of year costs as a
    // float after building a certain facility. This cost is calculated by adding the current
    // maintenance cost of the estate, total staff salary, and the maintenance cost of the
    // building that is going to be built (passed into the method).
    float totalStaffSalary = university.getHumanResource().getTotalSalary();
    float maintenanceCost = university.getEstate().getMaintenanceCost();
    if (type.equals("Hall")) {
      maintenanceCost += ((6 * ((int) java.lang.Math.pow(2, level - 1))) * 0.1);
    } else if (type.equals("Lab")) {
      maintenanceCost += ((5 * ((int) java.lang.Math.pow(2, level - 1))) * 0.1);
    } else if (type.equals("Theatre")) {
      maintenanceCost += ((10 * ((int) java.lang.Math.pow(2, level - 1))) * 0.1);
    }
    return (totalStaffSalary + maintenanceCost);
  }

  public void simulateEnd() {
    payEstateMaintenance();
    payStaffSalary();
    increaseYearsOfStaff();
    deductReputationPoints();
    staffLeave();
    replenishStaffStamina();
  }

  public void payEstateMaintenance() { // This method obtains the total maintenance costs of the
    // estate using another method then runs another method to decrease the budget of the
    // university by the maintenance cost
    float maintenanceCost = university.getEstate().getMaintenanceCost();
    university.changeBudget((maintenanceCost), false);
  }

  public void payStaffSalary() { // This method obtains the total staff salary then runs
    // another method to minus the total salary from the current university budget
    float totalStaffSalary = university.getHumanResource().getTotalSalary();
    university.changeBudget((totalStaffSalary), false);
  }

  public void increaseYearsOfStaff() { // This method uses an iterator to loop through all the
    // current staff members and increase their years of teaching by one
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    while (staffIterator.hasNext()) {
      Staff staff = staffIterator.next();
      staff.increaseYearsOfTeaching();
    }
  }

  public void staffLeave() {
    staffLeaveOver30();
    staffLeaveOverSkill();
  }

  public void staffLeaveOver30() { // This method uses an iterator to loop through the current
    // staff members and if their years of teaching is over 30, they are removed from the
    // university
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    ArrayList<Staff> staffArray = new ArrayList<>();
    while (staffIterator.hasNext()) {
      Staff staff = staffIterator.next();
      if (staff.getYearsOfTeaching() >= 30) {
        staffArray.add(staff);
      }
    }
    for (Staff staff : staffArray) {
      university.getHumanResource().removeStaff(staff);
    }
  }

  public void staffLeaveOverSkill() { // This method uses an iterator to loop through the
    // current staff members, and based on their skill level are determined whether they
    // leave the university or not. This is achieved through the Random class as it finds a
    // random float between 0 and 1.
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    ArrayList<Staff> staffArray = new ArrayList<>();
    while (staffIterator.hasNext()) {
      Staff staff = staffIterator.next();
      if (Math.random() > (staff.getSkill() * 0.01)) {
        staffArray.add(staff);
      }
    }
    for (Staff staff : staffArray) {
      university.getHumanResource().removeStaff(staff);
    }
  }

  public void replenishStaffStamina() { // This method uses an iterator  to loop through all the
    // staff members to replenish their stamina for the end of the year
    Iterator<Staff> staffIterator = university.getHumanResource().getStaff();
    while (staffIterator.hasNext()) {
      Staff staff = staffIterator.next();
      staff.replenishStamina();
    }
  }

  public void increaseBudgetBasedOnStudents() { // This method first finds out the number of
    // students at the university, it then increases the budget by 10 ECS Coins for each student
    int[] numOfStudentsArray = university.getEstate().getNumberOfStudentsArray();
    int zeroCounter = 0;
    int numofStudents = 0;
    for (int n : numOfStudentsArray) {
      if (n == 0) {
        zeroCounter += 1;
      }
    }
    Arrays.sort(numOfStudentsArray);
    if (zeroCounter == 3) {
      System.err.println("There are no students");
      System.exit(0);
    } else if (zeroCounter == 2) {
      numofStudents = numOfStudentsArray[2];
    } else if (zeroCounter == 1) {
      numofStudents = numOfStudentsArray[1];
    } else if (zeroCounter == 0) {
      numofStudents = numOfStudentsArray[0];
    }
    for (int i = 0; i < numofStudents; i++) {
      university.changeBudget(10, true);
    }
  }
}
