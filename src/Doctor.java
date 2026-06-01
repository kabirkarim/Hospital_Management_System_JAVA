
class Doctor extends Person {

    private static int counter = 5000;

    private String spec;

    public Doctor(String name, String spec) {
        super(++counter, name);
        this.spec = spec;
    }

    public Doctor(int id, String name, String spec) {
        super(id, name);
        this.spec = spec;

        if (id > counter) counter = id;
    }

    public String getSpec() { return spec; }

    public void setSpec(String spec) { this.spec = spec; }

    @Override
    public String toFile() {
        return getId() + "," + getName() + "," + spec;
    }

    @Override
    public String getSummary() {
        return "Doctor   | ID: "           + getId()   +
               " | Name: "                 + getName() +
               " | Specialization: "       + spec;
    }

    public static Doctor from(String s) {
        String[] d = s.split(",", 3);
        return new Doctor(
            Integer.parseInt(d[0].trim()),
            d[1].trim(),
            d[2].trim()
        );
    }
}
