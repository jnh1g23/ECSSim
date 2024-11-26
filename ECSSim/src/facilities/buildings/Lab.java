package facilities.buildings;

import facilities.Facility;

public class Lab extends Facility implements Building {
  private int level;

  public Lab(String name) {
    super(name);
    level = 1;
  }

  public int getLevel() {
    return this.level;
  }

  public void increaseLevel() {
    if (this.level < 5) {
      this.level += 1;
    } else {
      System.out.println("Building is already at maximum level");
    }
  }

  public int getUpgradeCost() {
    if (this.level < 5) {
      int upgradecost = 300 * (this.level + 1);
      return upgradecost;
    } else {
      return -1;
    }
  }

  public int getCapacity() {
    int capacity = 5 * ((int) java.lang.Math.pow(2, this.level - 1));
    return capacity;
  }
}
