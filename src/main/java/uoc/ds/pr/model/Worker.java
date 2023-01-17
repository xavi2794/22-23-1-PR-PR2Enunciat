package uoc.ds.pr.model;


import java.time.LocalDate;

public class Worker{
    private String dni;
    private String name;
    private String surname;

    private LocalDate birthDay;
    private String roleId;
    public Worker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        setDni(dni);
        setName(name);
        setSurname(surname);
        setBirthday(birthDay);
        setRoleId(roleId);
    }

    public void setDni(String dni){
        this.dni = dni;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public void setBirthday(LocalDate birthDay){
        this.birthDay = birthDay;
    }

    public void setRoleId(String roleId){
        this.roleId = roleId;
    }

    public String getDni(){
        return dni;
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public LocalDate getBirthDay(){
        return birthDay;
    }

    public String getRoleId(){
        return roleId;
    }
}
