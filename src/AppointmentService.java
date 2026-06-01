
import java.util.ArrayList;

class AppointmentService implements Service<Appointment> {

    private static final String      FILE         = "appointments.txt";
    private ArrayList<Appointment>   appointments = new ArrayList<>();

    public AppointmentService() {
        for (String line : FileUtil.read(FILE))
            appointments.add(Appointment.from(line));
    }

    @Override
    public ArrayList<Appointment> getAll() { return appointments; }

    @Override
    public Appointment findById(int id) {
        for (Appointment a : appointments)
            if (a.getId() == id) return a;
        return null;
    }

    @Override
    public boolean delete(int id) {
        Appointment a = findById(id);
        if (a == null) return false;
        appointments.remove(a);
        saveAll();
        return true;
    }

    public String book(String pidText, String didText, String dateTime,
                       PatientService patientService, DoctorService doctorService) {

        if (pidText.isEmpty() || didText.isEmpty())
            return "Patient ID and Doctor ID cannot be empty.";

        int pidVal, didVal;
        try {
            pidVal = Integer.parseInt(pidText);
            didVal = Integer.parseInt(didText);
        } catch (NumberFormatException e) {
            return "Patient ID and Doctor ID must be numbers.";
        }

        if (patientService.findById(pidVal) == null)
            return "Patient ID " + pidVal + " does not exist.";

        if (doctorService.findById(didVal) == null)
            return "Doctor ID " + didVal + " does not exist.";

        for (Appointment a : appointments) {
            if (a.getDid() == didVal && a.getDate().equals(dateTime)) {
                Doctor d = doctorService.findById(didVal);
                String docName = (d != null) ? d.getName() : "Doctor " + didVal;
                return "Dr. " + docName + " is already booked at " + dateTime +
                       ".\nPlease choose a different time slot.";
            }
        }

        for (Appointment a : appointments) {
            if (a.getPid() == pidVal && a.getDid() == didVal && a.getDate().equals(dateTime))
                return "This exact appointment already exists!";
        }

        Appointment a = new Appointment(pidVal, didVal, dateTime);
        appointments.add(a);
        FileUtil.save(FILE, a.toFile());
        return null;
    }

    public void deleteByPatient(int pid) {
        appointments.removeIf(a -> a.getPid() == pid);
        saveAll();
    }

    public void deleteByDoctor(int did) {
        appointments.removeIf(a -> a.getDid() == did);
        saveAll();
    }

    private void saveAll() {
        ArrayList<String> lines = new ArrayList<>();
        for (Appointment a : appointments) lines.add(a.toFile());
        FileUtil.overwrite(FILE, lines);
    }
}
