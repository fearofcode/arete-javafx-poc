package org.wkh.arete;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class DatabaseBackupPOC {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("application.properties"));

        String backupFolderProperty = properties.getProperty("backupPath");
        String backupExecutablePath = properties.getProperty("pgDumpPath");
        String databaseName = properties.getProperty("databaseName");
        String databaseUserName = properties.getProperty("databaseUserName");

        if (backupFolderProperty == null || backupExecutablePath == null || databaseName == null
                || databaseUserName == null) {
            System.err.println("*Nightborne voice* SOMETHINGS NOT QUITE RIGHT");
            System.err.println("(Properties configuration not set properly)");
            return;
        }

        final Path backupFolderPath = Paths.get(backupFolderProperty);
        final Path backupPath = backupFolderPath.resolve("arete_backup.sql");

        final Path backupTempFile = Files.createTempFile("arete_backup", ".sql");

        final String pgDumpCommand = String.format(
                "\"%s\" -d %s -U %s -f \"%s\"",
                backupExecutablePath,
                databaseName,
                databaseUserName,
                backupTempFile.toString()
        );

        System.out.println("Dump command: ");
        System.out.println(pgDumpCommand);

        if (StdoutCommandRunner.runCommandToStdout(pgDumpCommand) != 0) {
            System.err.println("pgDump errored out, bailing");
            return;
        }

        Files.copy(backupTempFile, backupPath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Copied " + backupTempFile + " to " + backupPath);
    }

}
