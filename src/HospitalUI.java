
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HospitalUI extends JFrame {

    private final PatientService     patientService     = new PatientService();
    private final DoctorService      doctorService      = new DoctorService();
    private final AppointmentService appointmentService = new AppointmentService();

    private final DefaultTableModel pModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Age", "Disease"}, 0);
    private final DefaultTableModel dModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Specialization"}, 0);
    private final DefaultTableModel aModel = new DefaultTableModel(
            new String[]{"AppointmentID", "PatientID", "DoctorID", "DateTime"}, 0);

    public HospitalUI() {
        setTitle("Hospital Management System");
        setSize(900, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Patients",     patientPanel());
        tabs.add("Doctors",      doctorPanel());
        tabs.add("Appointments", appointmentPanel());

        add(tabs);
        setVisible(true);
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 130, 25);
        p.add(l);
    }

    int askForId(String prompt) {
        String input = JOptionPane.showInputDialog(this, prompt);
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID must be a number.");
            return -1;
        }
    }

    boolean showIfError(String msg) {
        if (msg != null) {
            JOptionPane.showMessageDialog(this, msg);
            return true;
        }
        return false;
    }
    JPanel patientPanel() {
        JPanel p = new JPanel(null);

        JTextField name = new JTextField();
        JTextField age  = new JTextField();
        JTextField dis  = new JTextField();

        addLabel(p, "Name",    20, 20);  name.setBounds(150, 20,  150, 25); p.add(name);
        addLabel(p, "Age",     20, 60);  age.setBounds(150,  60,  150, 25); p.add(age);
        addLabel(p, "Disease", 20, 100); dis.setBounds(150,  100, 150, 25); p.add(dis);

        JButton add    = new JButton("Add");
        JButton search = new JButton("Search");
        JButton delete = new JButton("Delete");
        JButton update = new JButton("Update");

        add.setBounds(20,  150, 90, 30);
        search.setBounds(120, 150, 90, 30);
        delete.setBounds(220, 150, 90, 30);
        update.setBounds(320, 150, 90, 30);

        p.add(add); p.add(search); p.add(delete); p.add(update);

        JTable table = new JTable(pModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 210, 840, 280);
        p.add(sp);

        for (Patient pt : patientService.getAll()) {
            pModel.addRow(new Object[]{
                pt.getId(), pt.getName(), pt.getAge(), pt.getDisease()
            });
        }

        add.addActionListener(e -> {
            String err = patientService.add(
                name.getText().trim(),
                age.getText().trim(),
                dis.getText().trim()
            );
            if (showIfError(err)) return;

            Patient pt = patientService.getAll().get(patientService.getAll().size() - 1);
            pModel.addRow(new Object[]{pt.getId(), pt.getName(), pt.getAge(), pt.getDisease()});
            JOptionPane.showMessageDialog(this, "Patient Added. ID = " + pt.getId());
            name.setText(""); age.setText(""); dis.setText("");
        });

        search.addActionListener(e -> {
            int id = askForId("Enter Patient ID to search:");
            if (id == -1) return;

            Patient pt = patientService.findById(id);
            if (pt == null) {
                JOptionPane.showMessageDialog(this, "No patient found with ID " + id + ".");
                return;
            }

            for (int i = 0; i < pModel.getRowCount(); i++) {
                if (Integer.parseInt(pModel.getValueAt(i, 0).toString()) == id) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    break;
                }
            }

            name.setText(pt.getName());
            age.setText(String.valueOf(pt.getAge()));
            dis.setText(pt.getDisease());

            JOptionPane.showMessageDialog(this,
                "Patient found:\n" +
                "ID: "      + pt.getId()      + "\n" +
                "Name: "    + pt.getName()    + "\n" +
                "Age: "     + pt.getAge()     + "\n" +
                "Disease: " + pt.getDisease()
            );
        });

        delete.addActionListener(e -> {
            int id = askForId("Enter Patient ID to delete:");
            if (id == -1) return;

            Patient pt = patientService.findById(id);
            if (pt == null) {
                JOptionPane.showMessageDialog(this, "No patient found with ID " + id + ".");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete patient: " + pt.getName() + " (ID=" + id + ")?\n" +
                "This will also cancel all their appointments.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;


            for (int i = 0; i < pModel.getRowCount(); i++) {
                if (Integer.parseInt(pModel.getValueAt(i, 0).toString()) == id) {
                    pModel.removeRow(i); break;
                }
            }

            patientService.delete(id);


            appointmentService.deleteByPatient(id);
            for (int i = aModel.getRowCount() - 1; i >= 0; i--) {
                if (aModel.getValueAt(i, 1).toString().equals(String.valueOf(id)))
                    aModel.removeRow(i);
            }

            JOptionPane.showMessageDialog(this, "Patient and their appointments deleted.");
        });

        update.addActionListener(e -> {
            int id = askForId("Enter Patient ID to update:");
            if (id == -1) return;

            Patient pt = patientService.findById(id);
            if (pt == null) {
                JOptionPane.showMessageDialog(this, "No patient found with ID " + id + ".");
                return;
            }

            JTextField newName = new JTextField(pt.getName());
            JTextField newAge  = new JTextField(String.valueOf(pt.getAge()));
            JTextField newDis  = new JTextField(pt.getDisease());

            JPanel form = new JPanel(new java.awt.GridLayout(3, 2, 5, 8));
            form.add(new JLabel("Name:"));    form.add(newName);
            form.add(new JLabel("Age:"));     form.add(newAge);
            form.add(new JLabel("Disease:")); form.add(newDis);

            int result = JOptionPane.showConfirmDialog(this, form,
                "Update Patient ID " + id, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String err = patientService.update(
                id,
                newName.getText().trim(),
                newAge.getText().trim(),
                newDis.getText().trim()
            );
            if (showIfError(err)) return;

            for (int i = 0; i < pModel.getRowCount(); i++) {
                if (Integer.parseInt(pModel.getValueAt(i, 0).toString()) == id) {
                    pModel.setValueAt(pt.getName(),    i, 1);
                    pModel.setValueAt(pt.getAge(),     i, 2);
                    pModel.setValueAt(pt.getDisease(), i, 3);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Patient ID " + id + " updated successfully.");
        });

        return p;
    }

    JPanel doctorPanel() {
        JPanel p = new JPanel(null);

        JTextField name = new JTextField();
        JTextField spec = new JTextField();

        addLabel(p, "Name",           20, 20); name.setBounds(150, 20, 150, 25); p.add(name);
        addLabel(p, "Specialization", 20, 60); spec.setBounds(150, 60, 150, 25); p.add(spec);

        JButton add    = new JButton("Add");
        JButton search = new JButton("Search");
        JButton delete = new JButton("Delete");
        JButton update = new JButton("Update");

        add.setBounds(20,  120, 90, 30);
        search.setBounds(120, 120, 90, 30);
        delete.setBounds(220, 120, 90, 30);
        update.setBounds(320, 120, 90, 30);

        p.add(add); p.add(search); p.add(delete); p.add(update);

        JTable table = new JTable(dModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 180, 840, 300);
        p.add(sp);

        for (Doctor d : doctorService.getAll()) {
            dModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpec()});
        }

        add.addActionListener(e -> {
            String err = doctorService.add(
                name.getText().trim(),
                spec.getText().trim()
            );
            if (showIfError(err)) return;

            Doctor d = doctorService.getAll().get(doctorService.getAll().size() - 1);
            dModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpec()});
            JOptionPane.showMessageDialog(this, "Doctor Added. ID = " + d.getId());
            name.setText(""); spec.setText("");
        });

        search.addActionListener(e -> {
            int id = askForId("Enter Doctor ID to search:");
            if (id == -1) return;

            Doctor d = doctorService.findById(id);
            if (d == null) {
                JOptionPane.showMessageDialog(this, "No doctor found with ID " + id + ".");
                return;
            }

            for (int i = 0; i < dModel.getRowCount(); i++) {
                if (Integer.parseInt(dModel.getValueAt(i, 0).toString()) == id) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    break;
                }
            }

            name.setText(d.getName());
            spec.setText(d.getSpec());

            JOptionPane.showMessageDialog(this,
                "Doctor found:\n" +
                "ID: "             + d.getId()   + "\n" +
                "Name: "           + d.getName() + "\n" +
                "Specialization: " + d.getSpec()
            );
        });

        delete.addActionListener(e -> {
            int id = askForId("Enter Doctor ID to delete:");
            if (id == -1) return;

            Doctor d = doctorService.findById(id);
            if (d == null) {
                JOptionPane.showMessageDialog(this, "No doctor found with ID " + id + ".");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete doctor: " + d.getName() + " (ID=" + id + ")?\n" +
                "This will also cancel all their appointments.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            for (int i = 0; i < dModel.getRowCount(); i++) {
                if (Integer.parseInt(dModel.getValueAt(i, 0).toString()) == id) {
                    dModel.removeRow(i); break;
                }
            }

            doctorService.delete(id);

            appointmentService.deleteByDoctor(id);
            for (int i = aModel.getRowCount() - 1; i >= 0; i--) {
                if (aModel.getValueAt(i, 2).toString().equals(String.valueOf(id)))
                    aModel.removeRow(i);
            }

            JOptionPane.showMessageDialog(this, "Doctor and their appointments deleted.");
        });

        update.addActionListener(e -> {
            int id = askForId("Enter Doctor ID to update:");
            if (id == -1) return;

            Doctor d = doctorService.findById(id);
            if (d == null) {
                JOptionPane.showMessageDialog(this, "No doctor found with ID " + id + ".");
                return;
            }

            JTextField newName = new JTextField(d.getName());
            JTextField newSpec = new JTextField(d.getSpec());

            JPanel form = new JPanel(new java.awt.GridLayout(2, 2, 5, 8));
            form.add(new JLabel("Name:"));           form.add(newName);
            form.add(new JLabel("Specialization:")); form.add(newSpec);

            int result = JOptionPane.showConfirmDialog(this, form,
                "Update Doctor ID " + id, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String err = doctorService.update(
                id,
                newName.getText().trim(),
                newSpec.getText().trim()
            );
            if (showIfError(err)) return;

            for (int i = 0; i < dModel.getRowCount(); i++) {
                if (Integer.parseInt(dModel.getValueAt(i, 0).toString()) == id) {
                    dModel.setValueAt(d.getName(), i, 1);
                    dModel.setValueAt(d.getSpec(), i, 2);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Doctor ID " + id + " updated successfully.");
        });

        return p;
    }


    JPanel appointmentPanel() {
        JPanel p = new JPanel(null);

        JTextField pid = new JTextField();
        JTextField did = new JTextField();

        addLabel(p, "Patient ID",  20, 20);  pid.setBounds(150, 20,  150, 25); p.add(pid);
        addLabel(p, "Doctor ID",   20, 60);  did.setBounds(150, 60,  150, 25); p.add(did);
        addLabel(p, "Date & Time", 20, 100);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        Date minDate = cal.getTime();

        SpinnerDateModel model = new SpinnerDateModel(minDate, minDate, null, java.util.Calendar.MINUTE);
        JSpinner dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(editor);
        dateSpinner.setBounds(150, 100, 180, 25);
        p.add(dateSpinner);

        JButton book   = new JButton("Book");
        JButton search = new JButton("Search");
        JButton delete = new JButton("Delete");

        book.setBounds(20,   150, 90, 30);
        search.setBounds(120, 150, 90, 30);
        delete.setBounds(220, 150, 90, 30);

        p.add(book); p.add(search); p.add(delete);

        JTable table = new JTable(aModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 210, 840, 280);
        p.add(sp);

        for (Appointment a : appointmentService.getAll()) {
            aModel.addRow(new Object[]{a.getId(), a.getPid(), a.getDid(), a.getDate()});
        }

        book.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            Date now          = new Date();

            if (selectedDate.before(now)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot book an appointment in the past.\nPlease select a future date and time.");
                java.util.Calendar resetCal = java.util.Calendar.getInstance();
                resetCal.set(java.util.Calendar.SECOND, 0);
                resetCal.set(java.util.Calendar.MILLISECOND, 0);
                dateSpinner.setValue(resetCal.getTime());
                return;
            }

            String selectedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selectedDate);

            String err = appointmentService.book(
                pid.getText().trim(),
                did.getText().trim(),
                selectedDateTime,
                patientService,
                doctorService
            );
            if (showIfError(err)) return;

            ArrayList<Appointment> all = appointmentService.getAll();
            Appointment a = all.get(all.size() - 1);
            aModel.addRow(new Object[]{a.getId(), a.getPid(), a.getDid(), a.getDate()});
            JOptionPane.showMessageDialog(this, "Appointment Booked. ID = " + a.getId());
            pid.setText(""); did.setText("");
        });

        search.addActionListener(e -> {
            int id = askForId("Enter Appointment ID to search:");
            if (id == -1) return;

            Appointment a = appointmentService.findById(id);
            if (a == null) {
                JOptionPane.showMessageDialog(this, "No appointment found with ID " + id + ".");
                return;
            }

            for (int i = 0; i < aModel.getRowCount(); i++) {
                if (Integer.parseInt(aModel.getValueAt(i, 0).toString()) == id) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    break;
                }
            }

            JOptionPane.showMessageDialog(this,
                "Appointment found:\n" +
                "Appointment ID: " + a.getId()   + "\n" +
                "Patient ID: "     + a.getPid()  + "\n" +
                "Doctor ID: "      + a.getDid()  + "\n" +
                "Date & Time: "    + a.getDate()
            );
        });

        delete.addActionListener(e -> {
            int id = askForId("Enter Appointment ID to delete:");
            if (id == -1) return;

            Appointment a = appointmentService.findById(id);
            if (a == null) {
                JOptionPane.showMessageDialog(this, "No appointment found with ID " + id + ".");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel appointment ID " + id + " on " + a.getDate() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            appointmentService.delete(id);

            for (int i = 0; i < aModel.getRowCount(); i++) {
                if (Integer.parseInt(aModel.getValueAt(i, 0).toString()) == id) {
                    aModel.removeRow(i); break;
                }
            }

            JOptionPane.showMessageDialog(this, "Appointment deleted.");
        });

        return p;
    }
}
