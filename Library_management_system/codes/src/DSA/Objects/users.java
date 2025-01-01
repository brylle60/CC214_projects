package DSA.Objects;

public class users {
    private String lastName;
    private String firstName;
    private String Gender;
    private int Id;
    private String email;
    private String pass;
    private int limit;


    public users(int Id, String lastName, String firstName, String pass, String email, String Gender, int limit){
        this.Id = Id;
        this.lastName = lastName;
        this.pass = pass;
        this.email = email;
        this.firstName = firstName;
        this.Gender= Gender;
        this.limit = limit;
    }


    public users(int Id, String lastName, String firstName, String email,String pass){
        this.Id = Id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.pass = pass;
    }
    public users(){
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return Gender;
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

    public String getLastName() {
        return lastName;
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

    public void setLastName(String lastname) {
        lastName = lastname;
    }


    public void setLimit(int limit) {
        this.limit = 3;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getLimit() {
        return limit;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

   
}
