package university;

public class Staff {
  String name;
  int skill;
  int yearsOfTeaching;
  int stamina;

  public Staff(String name, int skill) {
    this.name = name;
    this.skill = skill;
    this.yearsOfTeaching = 0;
    this.stamina = 100;
  }

  public int instruct(
      int
          numberOfStudents) { // Used to make staff instruct the students, after a staff instructs a
                              // student their stamina is decreased. This method returns the change
                              // in reputation points
    if (skill < 100) {
      this.skill += 1;
    }
    this.stamina = (int) (stamina - Math.ceil((double) numberOfStudents / (20 + this.skill)) * 20);
    int reputationPoints = ((100 * skill) / (100 + numberOfStudents));
    return reputationPoints;
  }

  public void
      replenishStamina() { // Used to replenish the stamina of staff, If their stamina is below 80
                           // it is increased by 20, but is greater than 80, it will become 100
    if (this.stamina >= 80) {
      this.stamina = 100;
    } else {
      this.stamina += 20;
    }
  }

  public void increaseYearsOfTeaching() { // Increases years of teaching of the staff member by 1
    this.yearsOfTeaching += 1;
  }

  public int getYearsOfTeaching() {
    return yearsOfTeaching;
  } // Returns years of teaching of the staff member

  public int getSkill() {
    return skill;
  } // Returns skill level of staff member
}
