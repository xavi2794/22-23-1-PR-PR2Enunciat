package uoc.ds.pr;

import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.OrderedVector;

import java.time.LocalDate;

public class SportEvents4ClubImpl implements SportEvents4Club {
    private DictionaryAVLImpl<String, Player> players;
    private int numPlayers;
    private Player mostActivePlayer;
    private HashTable<String, OrganizingEntity> organizingEntities;
    private int numOrganizingEntities;
    private PriorityQueue<File> files;
    private int numFiles;
    private int numRejectedFiles;
    private DictionaryAVLImpl<String, SportEvent> events;
    private OrderedVector<SportEvent> bestSportEvent;
    private Role roles[];
    private int numRoles;
    private HashTable<String, Worker> workers;



    public SportEvents4ClubImpl(){
        players = new DictionaryAVLImpl<String, Player>();
        numPlayers = 0;
        mostActivePlayer = null;
        organizingEntities = new HashTable<String, OrganizingEntity>();
        numOrganizingEntities = 0;
        files = new PriorityQueue<File>();
        numFiles = 0;
        numRejectedFiles = 0;
        events = new DictionaryAVLImpl<String, SportEvent>();
        bestSportEvent = new OrderedVector<SportEvent>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_V);
        workers = new HashTable<String, Worker>();
        roles = new Role[MAX_ROLES];
        numRoles = 0;
    }
    @Override
    public void addPlayer(String id, String name, String surname, LocalDate dateOfBirth) {
        Player player = this.getPlayer(id);

        if(player == null){
            this.players.put(id, new Player(id, name, surname, dateOfBirth));
        }else{
            player.setName(name);
            player.setSurname(surname);
            player.setBirthday(dateOfBirth);
        }
    }

    @Override
    public void addOrganizingEntity(String id, String name, String description) {
        OrganizingEntity org = this.getOrganizingEntity(id);

        if(org == null){
            this.organizingEntities.put(id, new OrganizingEntity(id, name, description));
        }else{
            org.setName(name);
            org.setDescription(description);
        }
    }

    @Override
    public void addFile(String id, String eventId, String orgId, String description, Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException {
        OrganizingEntity org = this.getOrganizingEntity(orgId);

        if(org == null){
            throw new OrganizingEntityNotFoundException();
        }

        files.add(new File(id, eventId, description, type, startDate, endDate, resources, max, org));
        numFiles++;
    }

    @Override
    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException {
        if(files.isEmpty()){
            throw new NoFilesException();
        }
        File file = files.poll();
        file.update(status, date, description);
        if(file.isEnabled()){
            SportEvent event = file.newSportEvent();
            events.put(event.getEventId(), event);
        }else{
            numRejectedFiles++;
        }
        return file;
    }

    @Override
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException {
        Player player = getPlayer(playerId);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        SportEvent event = getSportEvent(eventId);
        if(event == null){
            throw new SportEventNotFoundException();
        }
        player.addEvent(event);
        if (!event.isFull()){
            event.addEnrollment(player);
        }else {
            event.addEnrollmentAsSubstitute(player);
            throw new LimitExceededException();
        }
        updateMostActivePlayer(player);
    }

    @Override
    public double getRejectedFiles() {
        return (double) numRejectedFiles / numFiles;
    }

    @Override
    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException {
        OrganizingEntity org = getOrganizingEntity(organizationId);
        if(org == null || !org.hasActivities()){
            throw new NoSportEventsException();
        }
        return org.activities();
    }

    @Override
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException {
        Iterator<SportEvent> it = events.values();
        if (!it.hasNext()) throw new NoSportEventsException();
        return it;
    }

    @Override
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException {
        Player player = getPlayer(playerId);
        if(player == null || player.hasEvents()){
            throw new NoSportEventsException();
        }
        return player.getEvents();
    }

    @Override
    public void addRating(String playerId, String eventId, Rating rating, String message) throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException {
        SportEvent event = getSportEvent(eventId);
        if(event == null){
            throw new SportEventNotFoundException();
        }
        Player player = getPlayer(playerId);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        if(!player.isInSportEvent(eventId)){
            throw new PlayerNotInSportEventException();
        }
        event.addRating(rating, message, player);
        updateBestSportEvent(event);
    }

    private void updateBestSportEvent(SportEvent sportEvent) {
        bestSportEvent.delete(sportEvent);
        bestSportEvent.update(sportEvent);
    }

    @Override
    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException {
        SportEvent event = getSportEvent(eventId);
        if(event == null){
            throw new SportEventNotFoundException();
        }
        if(!event.hasRatings()){
            throw new NoRatingsException();
        }
        return event.ratings();
    }

    @Override
    public Player mostActivePlayer() throws PlayerNotFoundException {
        if (mostActivePlayer == null) {
            throw new PlayerNotFoundException();
        }
        return mostActivePlayer;
    }

    private void updateMostActivePlayer(Player player) {
        if (mostActivePlayer == null) {
            mostActivePlayer = player;
        }
        else if (player.numSportEvents() > mostActivePlayer.numSportEvents()) {
            mostActivePlayer = player;
        }
    }

    @Override
    public SportEvent bestSportEvent() throws SportEventNotFoundException {
        if(bestSportEvent.size() == 0){
            throw new SportEventNotFoundException();
        }
        return bestSportEvent.elementAt(0);
    }

    @Override
    public void addRole(String roleId, String description) {
        Role role = getRole(roleId);
        if(role == null){
            role = new Role(roleId, description);
            numRoles++;
            roles[numRoles] = role;
        }
    }


    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        Worker worker = getWorker(dni);
        if(worker == null){
            workers.put(dni, new Worker(dni, name, surname, birthDay, roleId));
        }else{
            worker.setName(name);
            worker.setSurname(surname);
            worker.setBirthday(birthDay);
            worker.setRoleId(roleId);
        }
    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {
        Worker worker = getWorker(dni);
        if(worker == null){
            throw new WorkerNotFoundException();
        }
        SportEvent event = getSportEvent(eventId);
        if(event == null){
            throw new SportEventNotFoundException();
        }
        for (Iterator<Worker> it = event.workers(); it.hasNext();){
            Worker workerIt = it.next();
            if(worker == workerIt){
                throw new WorkerAlreadyAssignedException();
            }
        }
        event.addWorker(worker.getDni(), worker.getName(), worker.getSurname(), worker.getBirthDay(), worker.getRoleId());

    }

    @Override
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException {
        SportEvent event = getSportEvent(eventId);
        if(event == null){
            throw new SportEventNotFoundException();
        }
        if(!event.hasWorkers()){
            throw new NoWorkersException();
        }
        return event.workers();
    }

    @Override
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
        Role role = getRole(roleId);
        if(!role.hasWorkers()){
            throw new NoWorkersException();
        }
        return role.workers();
    }

    @Override
    public Level getLevel(String playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return player.getLevel();
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        return null;
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {

    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
        return null;
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        return null;
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        return null;
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        return null;
    }

    @Override
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException {

    }

    @Override
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException {
        return null;
    }

    @Override
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException {
        return null;
    }

    @Override
    public int numPlayers() {
        return 0;
    }

    @Override
    public int numOrganizingEntities() {
        return 0;
    }

    @Override
    public int numFiles() {
        return 0;
    }

    @Override
    public int numRejectedFiles() {
        return 0;
    }

    @Override
    public int numPendingFiles() {
        return 0;
    }

    @Override
    public int numSportEvents() {
        return 0;
    }

    @Override
    public int numSportEventsByPlayer(String playerId) {
        return 0;
    }

    @Override
    public int numPlayersBySportEvent(String sportEventId) {
        return 0;
    }

    @Override
    public int numSportEventsByOrganizingEntity(String orgId) {
        return 0;
    }

    @Override
    public int numSubstitutesBySportEvent(String sportEventId) {
        return 0;
    }

    @Override
    public Player getPlayer(String playerId) {
        return null;
    }

    @Override
    public SportEvent getSportEvent(String eventId) {
        return null;
    }

    @Override
    public OrganizingEntity getOrganizingEntity(String id) {
        return null;
    }

    @Override
    public File currentFile() {
        return null;
    }

    @Override
    public int numRoles() {
        return 0;
    }

    @Override
    public Role getRole(String roleId) {
        return null;
    }

    @Override
    public int numWorkers() {
        return 0;
    }

    @Override
    public Worker getWorker(String dni) {
        return null;
    }

    @Override
    public int numWorkersByRole(String roleId) {
        return 0;
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        return 0;
    }

    @Override
    public int numRatings(String playerId) {
        return 0;
    }

    @Override
    public int numAttenders(String sportEventId) {
        return 0;
    }

    @Override
    public int numFollowers(String playerId) {
        return 0;
    }

    @Override
    public int numFollowings(String playerId) {
        return 0;
    }
}

