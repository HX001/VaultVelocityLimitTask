package com.coding.vaulthometask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OutputComparator {
    /**
     * Compares the contents of two files, line by line, to check if they are identical.
     *
     * @param expectedFilePath the path of the expected output file (e.g., "outputProcess.txt")
     * @param actualFilePath   the path of the actual output file (e.g., "outputActual.txt")
     * @return true if both files have the same number of lines and each line matches exactly; otherwise, false
     * @throws IOException if an error occurs while reading the files
     */
    public static boolean compareLineByLine(Path expectedFilePath, Path actualFilePath) throws IOException {
        List<String> expectedLines = Files.readAllLines(expectedFilePath);
        List<String> actualLines = Files.readAllLines(actualFilePath);

        if (expectedLines.size() != actualLines.size()) {
            System.out.println("File lines are not equal: "
                    + "expected=" + expectedLines.size()
                    + ", actual=" + actualLines.size());
            return false;
        }

        for (int i = 0; i < expectedLines.size(); i++) {
            String expectedLine = expectedLines.get(i).trim();
            String actualLine = actualLines.get(i).trim();
            if (!expectedLine.equals(actualLine)) {
                System.out.println("Line " + (i + 1) + " does not match:");
                System.out.println("Expected: " + expectedLine);
                System.out.println("Actual: " + actualLine);
                return false;
            }
        }

        System.out.println("Processed Result Matched Actual Result！");
        return true;
    }

    public static void compareResult() throws IOException {
        Path actualFile = Path.of("outputActual.txt");
        Path processedFile = Path.of("outputProcess.txt");

        boolean isSame = compareLineByLine(actualFile, processedFile);
        System.out.println("Result matches: " + isSame);
    }
}
