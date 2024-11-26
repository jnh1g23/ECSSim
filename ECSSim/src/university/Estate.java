package university;

import facilities.Facility;
import facilities.buildings.Hall;
import facilities.buildings.Lab;
import facilities.buildings.Theatre;

import java.util.*;

public class Estate {
  private ArrayList<Facility> facilities;

  public Estate() {
    facilities = new ArrayList<>();
  }

  public Facility[] getFacilities() { // Returns the arraylist of facilities as an array
    Facility[] facilityarray = new Facility[facilities.size()];
    facilityarray = facilities.toArray(facilityarray);
    /* manual conversion
    int counter = 0;
    for (Facility facility : facilities){
        facilityarray[counter] = facility;
        counter += 1;
        }
     */
    return facilityarray;
  }

  public Facility addFacility(
      String type,
      String
          name) { // Adds a facility to the arraylist using the type and name provided in the
                  // parameters
    if (type.equals("Hall")) {
      Hall newfacility = new Hall(name);
      facilities.add(newfacility);
      return newfacility;
    } else if (type.equals("Lab")) {
      Lab newfacility = new Lab(name);
      facilities.add(newfacility);
      return newfacility;
    } else if (type.equals("Theatre")) {
      Theatre newfacility = new Theatre(name);
      facilities.add(newfacility);
      return newfacility;
    } else {
      return null;
    }
  }

  public float
      getMaintenanceCost() { // Calculates the total maintenance cost of all the facilities in the
                             // estate using a for loop
    float maintenancecost = 0;
    for (Facility facility : facilities) {
      if (facility instanceof Hall) {
        maintenancecost += (((Hall) facility).getCapacity()) * 0.1;
      } else if (facility instanceof Theatre) {
        maintenancecost += (((Theatre) facility).getCapacity()) * 0.1;
      } else if (facility instanceof Lab) {
        maintenancecost += (((Lab) facility).getCapacity()) * 0.1;
      }
    }
    return maintenancecost;
  }

  public int
      getNumberOfStudents() { // Once the unsorted number of students is obtained, it is sorted in
                              // ascending order, the first element that is not 0 will then become
                              // the number of students
    int[] numOfStudentsArray = getNumberOfStudentsArray();
    int zeroCounter = 0;
    Arrays.sort(numOfStudentsArray);
    return numOfStudentsArray[0];
  }

  public int[]
      getNumberOfStudentsArray() { // It cycles through the facilities array list and adds the
                                   // capacity of the facility to the corresponding index of the
                                   // type of facility
    // E.g. Hall is index 0, lab is index 1, etc. When the loop is finished, it will return the
    // unsorted array of number of students
    int[] numOfStudentsArray = new int[3];
    for (Facility facility : facilities) {
      if (facility instanceof Hall) {
        numOfStudentsArray[0] += ((Hall) facility).getCapacity();
      } else if (facility instanceof Theatre) {
        numOfStudentsArray[1] += ((Theatre) facility).getCapacity();
      } else if (facility instanceof Lab) {
        numOfStudentsArray[2] += ((Lab) facility).getCapacity();
      }
    }
    return numOfStudentsArray;
  }
}
