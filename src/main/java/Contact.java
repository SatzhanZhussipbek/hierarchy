public class Contact {

    private String name;


    private String groupName;


    private String phoneNumber;

    public Contact (String name, String groupName, String phoneNumber) {
        this.name = name;
        this.groupName = groupName;
        this.phoneNumber = phoneNumber;
    }
  public String getName() {return name;}

    public void setName() {this.name = name;}

  public String getGroupName() {return groupName;}

    public void setGroupName() {this.groupName = groupName;}

  public String getPhoneNumber() {return phoneNumber;}

    public void setPhoneNumber() {this.phoneNumber = phoneNumber;}

    @Override
    public String toString() {
        return name + " (" + phoneNumber + ")";
    }

}
