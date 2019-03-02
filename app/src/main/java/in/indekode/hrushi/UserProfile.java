package in.indekode.hrushi;

public class UserProfile {
    public String Name1;
    public String Email1;
    public String Age1;
    public String Mobile1;
    public String Gender1;

    public UserProfile() {

    }

    public UserProfile(String name1, String email1, String age1, String mobile1, String gender1) {
        Name1 = name1;
        Email1 = email1;
        Age1 = age1;
        Mobile1 = mobile1;
        Gender1 = gender1;
    }

    public String getName1() {
        return Name1;
    }

    public void setName1(String name1) {
        Name1 = name1;
    }

    public String getEmail1() {
        return Email1;
    }

    public void setEmail1(String email1) {
        Email1 = email1;
    }

    public String getAge1() {
        return Age1;
    }

    public void setAge1(String age1) {
        Age1 = age1;
    }

    public String getMobile1() {
        return Mobile1;
    }

    public void setMobile1(String mobile1) {
        Mobile1 = mobile1;
    }

    public String getGender1() {
        return Gender1;
    }

    public void setGender1(String gender1) {
        Gender1 = gender1;
    }
}