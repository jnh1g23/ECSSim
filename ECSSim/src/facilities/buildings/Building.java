package facilities.buildings;

public interface Building {
  int getLevel();

  void increaseLevel();

  int getUpgradeCost();

  int getCapacity();
}
