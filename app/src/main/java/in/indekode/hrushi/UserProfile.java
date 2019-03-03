package in.indekode.hrushi;

public class UserProfile {
    public String name;
    public String age;
    public String mobile;
    public String gender;
    public String ehelp;

    public UserProfile() {

    }

    public UserProfile(String name, String age, String mobile, String gender, String ehelp) {
        this.name = name;
        this.age = age;
        this.mobile = mobile;
        this.gender = gender;
        this.ehelp = ehelp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEhelp() {
        return ehelp;
    }

    public void setEhelp(String ehelp) {
        this.ehelp = ehelp;
    }
}