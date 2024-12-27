package SettersAndGetters;

public class users {
    private String Name;
    private int Id;
    private String email;
    private String pass;


    public users(int Id, String Name, String pass, String email){
        this.Id = Id;
        this.Name = Name;
        this.pass = pass;
        this.email = email;
    }
    public users(){

    }

    public int getId() {
        return Id;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return Name;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        Name = name;
    }
}
