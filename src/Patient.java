class Patient extends Person {

    private static int counter = 1000;

    private int    age;
    private String disease;

    public int    getAge()     { return age; }
    public String getDisease() { return disease; }

    public void setAge(int age)            { this.age     = age; }
    public void setDisease(String disease) { this.disease = disease; }

    public Patient(String name, int age, String disease) {
        super(++counter, name);
        this.age     = age;
        this.disease = disease;
    }


    public Patient(int id, String name, int age, String disease) {
        super(id, name);
        this.age     = age;
        this.disease = disease;

        if (id > counter) counter = id;
    }



    @Override
    public String toFile() {
        return getId() + "," + getName() + "," + age + "," + disease;
    }

    @Override
    public String getSummary() {
        return "Patient  | ID: " + getId()   +
               " | Name: "      + getName()  +
               " | Age: "       + age        +
               " | Disease: "   + disease;
    }

    public static Patient from(String s) {
        String[] d = s.split(",", 4);
        return new Patient(
            Integer.parseInt(d[0].trim()),
            d[1].trim(),
            Integer.parseInt(d[2].trim()),
            d[3].trim()
        );
    }
}
