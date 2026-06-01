
import java.util.ArrayList;

class DoctorService implements Service<Doctor> {

    private static final String FILE    = "doctors.txt";
    private ArrayList<Doctor>   doctors = new ArrayList<>();

    public DoctorService() {
        for (String line : FileUtil.read(FILE))
            doctors.add(Doctor.from(line));
    }
    @Override
    public ArrayList<Doctor> getAll() { return doctors; }

    @Override
    public Doctor findById(int id) {
        for (Doctor d : doctors)
            if (d.getId() == id) return d;
        return null;
    }

    @Override
    public boolean delete(int id) {
        Doctor d = findById(id);
        if (d == null) return false;
        doctors.remove(d);
        saveAll();
        return true;
    }

    public String add(String name, String spec) {
        if (name.isEmpty() || spec.isEmpty())
            return "Name and Specialization cannot be empty.";

        Doctor d = new Doctor(name, spec);
        doctors.add(d);
        FileUtil.save(FILE, d.toFile());
        return null;
    }

    public String update(int id, String name, String spec) {
        Doctor d = findById(id);
        if (d == null) return "No doctor found with ID " + id + ".";
        if (name.isEmpty() || spec.isEmpty())
            return "Name and Specialization cannot be empty.";

        d.setName(name);
        d.setSpec(spec);
        saveAll();
        return null;
    }

    private void saveAll() {
        ArrayList<String> lines = new ArrayList<>();
        for (Doctor d : doctors) lines.add(d.toFile());
        FileUtil.overwrite(FILE, lines);
    }
}
