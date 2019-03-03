package in.indekode.hrushi;

public class DoctorProfile {

    public String dname;
    public String demail;
    public  String dmobile;
    public String speciality;
    public String location;

    public DoctorProfile(String dname, String demail, String dmobile, String speciality, String location) {
        this.dname = dname;
        this.dmobile = dmobile;
        this.speciality = speciality;
        this.location = location;
        this.demail = demail;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDemail() {
        return demail;
    }

    public void setDemail(String demail) {
        this.demail = demail;
    }

    public String getDmobile() {
        return dmobile;
    }

    public void setDmobile(String dmobile) {
        this.dmobile = dmobile;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
