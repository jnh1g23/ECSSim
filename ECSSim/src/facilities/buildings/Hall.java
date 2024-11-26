package facilities.buildings;

import facilities.Facility;

public class Hall extends Facility implements Building {
  private int level;

  public Hall(String name) {
    super(name);
    level = 1;
  }

  public int getLevel() {
    return this.level;
  }

  public void increaseLevel() {
    if (this.level < 4) {
      this.level += 1;
    } else {
      System.out.println("Building is already at maximum level");
    }
  }

  public int getUpgradeCost() {
    if (this.level < 4) {
      return (100 * (this.level + 1));
    } else {
      return -1;
    }
  }

  public int getCapacity() {
    return (6 * ((int) java.lang.Math.pow(2, this.level - 1)));
  }
}
