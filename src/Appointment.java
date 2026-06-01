
class Appointment {

    private static int counter = 9000;

    private int    id;
    private int    pid;
    private int    did;
    private String date;
    public Appointment(int pid, int did, String date) {
        this.id   = ++counter;
        this.pid  = pid;
        this.did  = did;
        this.date = date;
    }

    public Appointment(int id, int pid, int did, String date) {
        this.id   = id;
        this.pid  = pid;
        this.did  = did;
        this.date = date;

        if (id > counter) counter = id;
    }

    public int    getId()   { return id; }
    public int    getPid()  { return pid; }
    public int    getDid()  { return did; }
    public String getDate() { return date; }

    public String toFile() {
        return id + "," + pid + "," + did + "," + date;
    }

    public static Appointment from(String s) {
        String[] d = s.split(",", 4);
        return new Appointment(
            Integer.parseInt(d[0].trim()),
            Integer.parseInt(d[1].trim()),
            Integer.parseInt(d[2].trim()),
            d[3].trim()
        );
    }
}
