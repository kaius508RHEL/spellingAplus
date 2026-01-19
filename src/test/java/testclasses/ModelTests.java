package src.test.java.testclasses;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import src.main.java.SpellCheckerModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;


public class ModelTests {
    @Test
    public void isWordTest1() {
        String word = "Hello";
        assertTrue(SpellCheckerModel.isWord(word));
    }

    @Test
    public void isWordTest2() {
        String notword = "he3l4-2";
        assertFalse(SpellCheckerModel.isWord(notword));
    }
}
