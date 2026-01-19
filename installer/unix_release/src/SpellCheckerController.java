package src;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpellCheckerController {
    private static List<String> whitelist = new ArrayList<>();
    private static int automationConfig;
    private static String backupPath;

    public static void main(String[] args) throws IOException {
        readConfigFile(new File("config.txt"));
        boolean backup = backupPath != null && !backupPath.isEmpty();

        if (args.length < 1) {
            throw new IOException("Invalid arguments, no file name passed in");
        }

        String fileName = args[0];
        try{
            int automation = automationConfig;
            if (args.length == 2) {
                automation = Integer.parseInt(args[1]);
            }
            File fileObj = new File(fileName);
            if (!fileObj.isAbsolute()) {
                fileObj = new File(System.getProperty("user.dir"), fileName);
            }
            if (fileObj.isFile()){
                SpellCheckerModel a = new SpellCheckerModel(fileObj, automation, new ArrayList<>(whitelist));
                try {
                    if (backup) {
                        try {
                            File c = makeBackup(fileObj);
                            System.out.println("Backup file saved at " + c.getPath());
                        } catch (Exception e) {
                            System.out.println("ERROR: File backup failed!");
                        }
                    }
                    File b = a.spellCheck();
                    System.out.println("Corrected file saved at " + b.getPath());
                } catch (Exception e) {
                    System.out.println("ERROR: Spell check for file failed!");
                }
            }
            else {
                System.out.print("ERROR: " + fileName + " not found!");
            }
        } catch (Exception e){
            System.out.println("Invalid automation value!");
        }


    }

    private static void readConfigFile(File configFile) throws IOException {
        String text = Files.readString(configFile.toPath());

        // Backup path (backupPath=/folder/folder/folder)
        Pattern backupPattern = Pattern.compile("backupPath\\s*=\\s*([^\\r\\n]+)");
        Matcher backupMatcher = backupPattern.matcher(text);
        if (backupMatcher.find()) {
            backupPath = backupMatcher.group(1).trim();
        }

        // Whitelist block (whitelist { word1 word2 word3 }
        Pattern whitelistPattern = Pattern.compile("whitelist\\s*\\{([^}]*)\\}");
        Matcher whitelistMatcher = whitelistPattern.matcher(text);
        if (whitelistMatcher.find()) {
            String inside = whitelistMatcher.group(1).trim();
            if (!inside.isEmpty()) {
                whitelist = Arrays.stream(inside.split("\\s+"))
                                    .map(String::toUpperCase)
                                    .collect(Collectors.toList());
            }
        }

        // Automation number (automation=value)
        Pattern automationPattern = Pattern.compile("automation\\s*=\\s*(\\d+)");
        Matcher automationMatcher = automationPattern.matcher(text);
        if (automationMatcher.find()) {
            automationConfig = Integer.parseInt(automationMatcher.group(1));
        }
    }

    private static File makeBackup(File inputFile) throws IOException {
        File backupFolder = new File(backupPath);
        if (!backupFolder.isAbsolute()) {
            backupFolder = new File(System.getProperty("user.dir"), backupPath);
        }
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            throw new IOException("Could not create backup folder: " + backupFolder.getAbsolutePath());
        }

        String originalName = inputFile.getName();
        File backupFile = new File(backupFolder, originalName + ".bak");
        int counter = 0;
        while (backupFile.exists()) {
            backupFile = new File(backupFolder, originalName + ".bak." + String.format("%03d", counter));
            counter++;
        }

        Files.copy(inputFile.toPath(), backupFile.toPath());
        return backupFile;
    }
}
