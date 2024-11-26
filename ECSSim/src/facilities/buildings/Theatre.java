package facilities.buildings;

import facilities.Facility;

public class Theatre extends Facility implements Building {
  private int level;

  public Theatre(String name) {
    super(name);
    level = 1;
  }

  public int getLevel() {
    return this.level;
  }

  public void increaseLevel() {
    if (this.level < 6) {
      this.level += 1;
    } else {
      System.out.println("Building is already at maximum level");
    }
  }

  public int getUpgradeCost() {
    if (this.level < 6) {
      int upgradecost = 200 * (this.level + 1);
      return upgradecost;
    } else {
      return -1;
    }
  }

  public int getCapacity() {
    int capacity = 10 * ((int) java.lang.Math.pow(2, this.level - 1));
    return capacity;
  }
}
