package src.test.java.testclasses;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import src.main.java.SpellCheckerController;
import src.main.java.SpellCheckerModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Tests {
    @Test
    public void class1Test() throws IOException{
        File c1 = new File("src/test/java/testfiles/class1.txt");
        SpellCheckerController.main(new String[]{c1.toString(), "10"});

        File corrected = new File("backups/class1.txt.bak");

        File c2 = new File("src/test/java/testfiles/class1expected.txt");
        Assertions.assertNotEquals(Files.readAllLines(corrected.toPath()), Files.readAllLines(c2.toPath()));

    }
}
