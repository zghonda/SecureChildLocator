package project.sirs.scl;

public class User {


    private final String name,email;
    private final String password;

    public User(String name,String email,String password){
        this.name = name;
        this.email=email;
        this.password = password;

    }

   public String Name(){
        return name;
   }


   public String email(){
        return email;
   }

   public String password(){
        return password;
   }

}
