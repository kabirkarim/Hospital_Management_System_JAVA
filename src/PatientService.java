
import java.util.ArrayList;

class PatientService implements Service<Patient> {

    private static final String    FILE     = "patients.txt";
    private ArrayList<Patient>     patients = new ArrayList<>();

    public PatientService() {
        for (String line : FileUtil.read(FILE))
            patients.add(Patient.from(line));
    }


    @Override
    public ArrayList<Patient> getAll() { return patients; }

    @Override
    public Patient findById(int id) {
        for (Patient p : patients)
            if (p.getId() == id) return p;
        return null;
    }

    @Override
    public boolean delete(int id) {
        Patient p = findById(id);
        if (p == null) return false;
        patients.remove(p);
        saveAll();
        return true;
    }

    public String add(String name, String ageText, String disease) {
        if (name.isEmpty() || disease.isEmpty())
            return "Name and Disease cannot be empty.";
        int age;
        try { age = Integer.parseInt(ageText); }
        catch (NumberFormatException e) { return "Age must be a number."; }
        if (age <= 0 || age > 150)
            return "Please enter a valid age (1-150).";

        Patient p = new Patient(name, age, disease);
        patients.add(p);
        FileUtil.save(FILE, p.toFile());
        return null;
    }

    public String update(int id, String name, String ageText, String disease) {
        Patient p = findById(id);
        if (p == null) return "No patient found with ID " + id + ".";
        if (name.isEmpty() || disease.isEmpty())
            return "Name and Disease cannot be empty.";
        int age;
        try { age = Integer.parseInt(ageText); }
        catch (NumberFormatException e) { return "Age must be a number."; }
        if (age <= 0 || age > 150)
            return "Please enter a valid age (1-150).";

        p.setName(name);
        p.setAge(age);
        p.setDisease(disease);
        saveAll();
        return null;
    }

    private void saveAll() {
        ArrayList<String> lines = new ArrayList<>();
        for (Patient p : patients) lines.add(p.toFile());
        FileUtil.overwrite(FILE, lines);
    }
}
