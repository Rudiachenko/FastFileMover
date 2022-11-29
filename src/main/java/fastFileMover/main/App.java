package fastFileMover.main;

import fastFileMover.service.FastFileMover;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@AllArgsConstructor
public class App {
    private static final int FIRST_TYPE_OF_MOVER = 1;
    private static final int SECOND_TYPE_OF_MOVER = 2;
    private static final int THIRD_TYPE_OF_MOVER = 3;
    private static final int FOURTH_TYPE_OF_MOVER = 4;
    private static final String EXIT = "e";
    private static final String DELIMITER = "\\";
    private final FastFileMover fileMover;

    public static void main(String[] args) {
        App app = new App(new FastFileMover());

        app.callFunction();
    }

    public void callFunction() {
        String typeOfMover;
        do {
            System.out.println("Please, select the version of mover or print 'e' to exit:" +
                    "\n1.It uses NIO 2 File API" +
                    "\n2.It uses FileStreams with buffer 100 kb" +
                    "\n3.It uses FileStreams without buffer" +
                    "\n4.It uses FileChannel");

            Scanner in = new Scanner(System.in);
            typeOfMover = in.nextLine();
            if (isCorrectCommand(typeOfMover)) {
                System.out.println("Please, provide path of file to move");
                String pathToFile = in.nextLine();

                System.out.println("Please, provide the path where to move the file.");
                String pathToTargetPackage = in.nextLine();

                if (!validatePaths(pathToFile, pathToTargetPackage)) {
                    System.out.println("Invalid path from/to move. Please, provide correct path");
                    continue;
                }

                invokeMover(typeOfMover, pathToFile, pathToTargetPackage);
                break;
            } else if (typeOfMover.equals(EXIT)) {
                break;
            } else {
                System.out.println("Incorrect number of typeOfMover. Please, try again");
            }
        }
        while (true);
    }

    private boolean isCorrectCommand(String command) {
        return StringUtils.isNumeric(command) && ((Integer.parseInt(command) >= 1 && Integer.parseInt(command) <= 4));
    }

    private void invokeMover(String typeOfMover, String filePath, String pathToTargetPackage) {
        String targetFile = pathToTargetPackage + DELIMITER + Paths.get(filePath).getFileName();
        if (Integer.parseInt(typeOfMover) == FIRST_TYPE_OF_MOVER) {
            fileMover.moveFileUsingNio(filePath, targetFile);
        } else if (Integer.parseInt(typeOfMover) == SECOND_TYPE_OF_MOVER) {
           fileMover.moveFileUsingFileStream(true, filePath, targetFile);
        } else if (Integer.parseInt(typeOfMover) == THIRD_TYPE_OF_MOVER) {
            fileMover.moveFileUsingFileStream(false, filePath, targetFile);
        } else if (Integer.parseInt(typeOfMover) == FOURTH_TYPE_OF_MOVER) {
           fileMover.moveFileUsingFileStream(filePath, targetFile);
        }
    }

    private boolean validatePaths(String filePath, String pathToMove) {
        return Files.exists(Path.of(filePath)) && (Files.exists(Path.of(pathToMove)));
    }
}
