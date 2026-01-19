package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SpellCheckerView {

    /**
     * Generates and displays output based on the given list of changes, and returns
     * a file containing the displayed results.
     *
     * <p>The method formats the provided list of word changes and presents them to
     * the user (e.g., via console output, GUI component, or log). It also creates
     * a file containing the formatted output and returns a reference to that file.</p>
     *
     * @param changes a list of strings describing the changes to display; must not be {@code null}
     * @throws IllegalArgumentException if {@code changes} is {@code null}
     * @throws IOException              if an I/O error occurs while creating or writing the output file
     */

    public static void displayOutput(List<String> changes) throws IOException {
        if (changes == null) {
            throw new IllegalArgumentException("List of changes cannot be null");
        }
        if (changes.isEmpty()) {
            System.out.println("No corrections to list");
            return;
        }

        File changeList = new File("changelist.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(changeList))) {
            for (String c : changes) {
                System.out.println(c);
                writer.write(c);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Logging changes failed");
        }
    }

    /**
     * Stores the given file at the specified destination location.
     *
     * <p>This method copies the contents of {@code file} to the file system path
     * provided by {@code location}. Implementations may overwrite an existing file
     * at the destination unless otherwise specified.</p>
     *
     * @param file the file to be stored; must not be {@code null} and must exist
     * @param location the destination path where the file should be stored; must not be {@code null}
     * @throws IllegalArgumentException if {@code file} or {@code location} is {@code null},
     *         or if {@code file} does not exist
     * @throws IOException if an I/O error occurs while copying the file
     */

    public void storeFile(File file, String location) {
        if (file == null || location == null) {
            throw new IllegalArgumentException("File or location must not be null");
        }
        if (file.getAbsolutePath() == null) {
            throw new IllegalArgumentException("File or location does not exist");
        }

        File path = new File(location);

        try {
            FileReader i = new FileReader(file);
            FileWriter o = new FileWriter(path);
            int c = i.read();
            while (c != -1) {
                o.write(c);
                i.read();
            }
            i.close();
            o.close();
            System.out.println("Corrected file saved at " + path.getPath());
        } catch (IOException e) {
            System.out.println("File storage failed");
        }
    }
}
