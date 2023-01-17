package uoc.ds.pr.model;

import java.time.LocalDate;

import edu.uoc.ds.adt.helpers.KeyValue;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import java.util.Comparator;
import edu.uoc.ds.adt.*;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import uoc.ds.pr.SportEvents4Club;


public class Player implements Comparable<Player>{
    public static final Comparator<KeyValue<String, Player>> CMP_K = (KeyValue<String, Player> p1, KeyValue<String, Player> p2) -> p1.getKey().compareTo(p2.getKey());
    private String id;
    private String name;
    private String surname;
    private List<SportEvent> events;
    private LocalDate birthday;
    private SportEvents4Club.Level level;

	public Player(String idUser, String name, String surname, LocalDate birthday) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.events = new LinkedList<>();
        this.level = SportEvents4Club.Level.ROOKIE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
    public boolean is(String playerID) {
        return id.equals(playerID);
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean isInSportEvent(String eventId) {
        boolean found = false;
        SportEvent sportEvent = null;
        Iterator<SportEvent> it = getEvents();
        while (it.hasNext() && !found) {
            sportEvent = it.next();
            found = sportEvent.is(eventId);
        }
        return found;
    }

    public int numSportEvents() {
        return events.size();
    }

    public Iterator<SportEvent> getEvents() {
        return events.values();
    }

    public boolean hasEvents() {
        return this.events.size()>0;
    }

    @Override
    public int compareTo(Player p) {
        return this.getId().compareTo(p.getId());
    }

    public SportEvents4Club.Level getLevel(){
        return this.level;
    }
}
