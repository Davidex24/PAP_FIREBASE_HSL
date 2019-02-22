package com.hsl_firebase.alves.pap_firebase_hsl;

public class usertasks {
    public String taskname;
    public String taskdate;
    public String tasklocal;

    public usertasks(){

    }

    public usertasks(String taskname, String taskdate, String tasklocal) {
        this.taskname = taskname;
        this.taskdate = taskdate;
        this.tasklocal = tasklocal;

    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskdate() {
        return taskdate;
    }

    public void setTaskdate(String taskdate) {
        this.taskdate = taskdate;
    }

    public String getTasklocal() {
        return tasklocal;
    }

    public void setTasklocal(String tasklocal) {
        this.tasklocal = tasklocal;
    }
}
