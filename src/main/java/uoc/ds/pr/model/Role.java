package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;

public class Role {
    private String roleId;
    private String description;
    private LinkedList workers;

    public Role(String roleId, String description){
        setRoleId(roleId);
        setDescription(description);
        this.workers = new LinkedList<Worker>();
    }

    public void setRoleId(String roleId){
        this.roleId = roleId;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public boolean hasWorkers(){
        return workers.size()>0;
    }

    public Iterator<Worker> workers(){
        return workers.values();
    }
}
