package university;

import facilities.Facility;
import facilities.buildings.*;

import java.util.Arrays;

public class University {
  private HumanResource humanResource = new HumanResource();
  private float budget;
  private Estate estate = new Estate();
  private int reputation;

  public University(int funding) {
    this.budget = funding;
  }

  public Facility build(
      String type,
      String
          name) { // Passes in the type of bulding and name of building then builds that building,
                  // adding the reputation and minusing the building cost from the budget
    if (type.equals("Hall") & budget >= 100) {
      budget -= 100;
      reputation += 100;
      return estate.addFacility(type, name);
    } else if (type.equals("Theatre") & budget >= 200) {
      budget -= 200;
      reputation += 100;
      return estate.addFacility(type, name);
    } else if (type.equals("Lab") & budget >= 300) {
      budget -= 300;
      reputation += 100;
      return estate.addFacility(type, name);
    } else {
      return null;
    }
  }

  public void upgrade(Building building)
      throws
          Exception { // This method is used to upgrade buildings, buildings can only be upgraded if
                      // they are below their maximum level. If upgraded, the level and reputation
                      // is increased, and the costs are taken away from the budget
    Facility[] facilityarray = (estate.getFacilities());
    if ((building.getUpgradeCost() == -1)) {
      throw new Exception("Building is at maximum level");
    } else if (Arrays.asList(facilityarray).contains(building) == false) {
      throw new Exception("Building is not a part of the University");
    } else {
      budget -= building.getUpgradeCost();
      building.increaseLevel();
      reputation += 50;
    }
  }

  public float getBudget() {
    return budget;
  } // Method used to return current budget

  public int getReputation() {
    return reputation;
  } // method used to return current reputation

  public Estate getEstate() {
    return estate;
  } // Method used to return Estate of the university

  public void changeBudget(
      float changeAmount,
      Boolean increase) { // Method used to either increase or decrease the budget,
    if (increase) {
      this.budget = budget + changeAmount;
    } else if (increase == false) {
      this.budget = budget - changeAmount;
    }
  }

  public void setBudget(
      int
          initialBudget) { // Method used to set the budget of the university (Used to set up
                           // initial budget)
    this.budget = initialBudget;
  }

  public HumanResource getHumanResource() {
    return humanResource;
  } // Returns humanresource object of the university

  public void increaseReputation(
      int points) { // used to increase the reputation points of the university
    this.reputation += points;
  }

  public void decreaseReputation(int points) {
    this.reputation -= points;
  } // Used to decrease the reputation points of the university
}
