package in.indekode.hrushi;

public class AppointmentProfile {

    public String patientname;
    public String drname;
    public String date;
    public String time;

    public AppointmentProfile() {
    }

    public AppointmentProfile(String patientname, String drname, String date, String time) {
        this.patientname = patientname;
        this.drname = drname;
        this.date = date;
        this.time = time;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getDrname() {
        return drname;
    }

    public void setDrname(String drname) {
        this.drname = drname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
