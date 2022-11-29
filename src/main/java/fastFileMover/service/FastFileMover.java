package fastFileMover.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FastFileMover {
    private static final int HUNDRED_KB_IN_BYTES = 102400;
    private static final int START_OFFSET = 0;

    public void moveFileUsingNio(String from, String to) {
        try {
            Path path = Files.move
                    (Paths.get(from),
                            Paths.get(to));
            System.out.println("File moved successfully: " + path);

        } catch (IOException e) {
            throw new RuntimeException("Failed to move the file ", e);
        }
    }

    public void moveFileUsingFileStream(boolean withBuffer, String from, String to) {
        try {
            File inFile = new File(from);
            File outFile = new File(to);
            FileInputStream ins = new FileInputStream(inFile);
            FileOutputStream outs = new FileOutputStream(outFile);
            byte[] buffer = new byte[HUNDRED_KB_IN_BYTES];

            if (withBuffer) {
                writeWithBuffer(ins, outs, buffer);
            } else {
                writeWithoutBuffer(ins, outs);
            }

            ins.close();
            outs.close();
            inFile.delete();

            System.out.println("File moved successfully");
        } catch (IOException e) {
            throw new RuntimeException("Failed to move the file ", e);
        }
    }

    public void moveFileUsingFileStream(String from, String to) {
        {
            try (FileInputStream fis = new FileInputStream(from);
                 FileOutputStream fos = new FileOutputStream(to);
                 FileChannel inputChannel = fis.getChannel();
                 WritableByteChannel targetChannel = fos.getChannel()) {
                inputChannel.transferTo(START_OFFSET, inputChannel.size(), targetChannel);
                System.out.println("File was moved successfully");
                new File(from).delete();
            } catch (IOException e) {
                throw new RuntimeException("Failed to move the file ", e);
            }
        }
    }

    private void writeWithBuffer(FileInputStream ins, FileOutputStream outs, byte[] buffer) throws IOException {
        int length;
        while ((length = ins.read(buffer)) > 0) {
            outs.write(buffer, START_OFFSET, length);
        }
    }

    private void writeWithoutBuffer(FileInputStream ins, FileOutputStream outs) throws IOException {
        int length;
        while ((length = ins.read()) > 0) {
            outs.write(length);
        }
    }
}
