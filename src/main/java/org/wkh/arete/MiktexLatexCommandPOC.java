package org.wkh.arete;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class MiktexLatexCommandPOC {
    private final static String latexSubDirectory = "arete_latex_processing";
    private Path latexBinDirectory;
    private boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    private Path tempBase = Paths.get(System.getProperty("java.io.tmpdir")).resolve(latexSubDirectory);

    private final String markup;
    private final Path latexPath;
    private final Path dviPngPath;

    private final int fontSize;

    public MiktexLatexCommandPOC(String markup, int fontSize, String latexBinDirectory) throws IOException {
        this.markup = markup;
        this.fontSize = fontSize;

        this.latexBinDirectory = Paths.get(latexBinDirectory);

        latexPath = pathForExecutableName("latex");
        dviPngPath = pathForExecutableName("dvipng");

        if (!Files.exists(tempBase)) {
            Files.createDirectory(tempBase);
        }
    }

    public Path pathForExecutableName(String executable) throws FileNotFoundException {
        Path fullPath = latexBinDirectory.resolve(executable + (isWindows ? ".exe" : ""));

        if (!Files.isExecutable(fullPath)) {
            throw new FileNotFoundException(
                    String.format("Path '%s' does not exist. Check your configuration.", fullPath.toString())
            );
        }

        return fullPath;
    }

    public String generateWrappedMarkup() {
        return "\\documentclass{article}\n" +
                "\\begin{document}\n" +
                markup + "\n" +
                "\\end{document}";
    }

    public double getDesiredDPI() {
        // http://www.nongnu.org/dvipng/dvipng_4.html#Option-details
        return ((double)fontSize) * 72.27 / 10;
    }



    public String process() throws IOException, InterruptedException {
        final String wrappedMarkup  = generateWrappedMarkup();
        final byte[] markupBytes = wrappedMarkup.getBytes(StandardCharsets.UTF_8);
        Checksum checksum = new CRC32();
        checksum.update(markupBytes, 0, markupBytes.length);
        long checksumValue = checksum.getValue();

        /* TODO check if existing checksum file already exists and avoid processing if so */

        final Path tempLatexFile = tempBase.resolve(checksumValue + ".tex");

        if (!Files.exists(tempLatexFile)) {
            Files.createFile(tempLatexFile);
        }

        Files.write(tempLatexFile, markupBytes);

        System.out.println(tempLatexFile.toString());

        /* TODO notice that there are no quotes around aux-directory and output-directory. LaTeX appears to shit the bed
        when you provide quotes. I tried it with quotes first and it didn't work. It didn't work until I removed the
        quotes. I'll have to check the MikTeX source to figure out WTF is going on.
         */
        final String latexCmd = String.format(
                "\"%s\" -halt-on-error -aux-directory=%s -output-directory=%s \"%s\"",
                latexPath.toString(),
                tempBase.toString(),
                tempBase.toString(),
                tempLatexFile.toString()
        );

        System.out.println(latexCmd);
        int result = StdoutCommandRunner.runCommandToStdout(latexCmd);

        if (result != 0) {
            System.out.println("Process failed with status: " + result);
            return null;
        }

        String expectedDviPath = tempLatexFile.toString().replace(".tex", ".dvi");

        if (Files.notExists(Paths.get(expectedDviPath))) {
            System.err.println("Expected path '" + expectedDviPath + "' did not exist");
        }

        String expectedPngPath = tempLatexFile.toString().replace(".tex", ".png");

        /* http://www.nongnu.org/dvipng/dvipng_4.html */
        final String dviPngCommand = String.format(
                "\"%s\" -T tight -bg Transparent -D %g -o \"%s\" \"%s\"",
                dviPngPath.toString(),
                getDesiredDPI(),
                expectedPngPath,
                expectedDviPath
        );

        result = StdoutCommandRunner.runCommandToStdout(dviPngCommand);

        if (result != 0) {
            System.out.println("dvipng failed with status: " + result);
            return null;
        }

        return expectedPngPath;
    }
}
