package User;

public class users {
    private String Name;
    private int Id;

    public users(String Name, int id){
        this.Id = id;
        this.Name = Name;
    }
    public users(){
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }
}
