import java.io.*;
import java.util.*;

class FileUtil {

    static void save(String file, String data) {
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(data + "\n");
        } catch (Exception e) {
            System.err.println("[FileUtil] Error saving to " + file + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    static ArrayList<String> read(String file) {
        ArrayList<String> list = new ArrayList<>();
        File f = new File(file);

        if (!f.exists()) {
            try { f.createNewFile(); }
            catch (Exception e) {
                System.err.println("[FileUtil] Could not create " + file + ": " + e.getMessage());
            }
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String l;
            while ((l = br.readLine()) != null) {
                if (!l.trim().isEmpty()) list.add(l);
            }
        } catch (Exception e) {
            System.err.println("[FileUtil] Error reading " + file + ": " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    static void overwrite(String file, ArrayList<String> data) {
        try (FileWriter fw = new FileWriter(file)) {
            for (String line : data) fw.write(line + "\n");
        } catch (Exception e) {
            System.err.println("[FileUtil] Error overwriting " + file + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
