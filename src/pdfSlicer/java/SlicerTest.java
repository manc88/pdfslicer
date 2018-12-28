package pdfSlicer.java;

import java.io.*;

import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SlicerTest {

    @Test
    public void test() throws IOException {

        System.out.println("Running test...");

        String com = " /command mix  /file F0=data\\F0.pdf /file F1=data\\F1.pdf /file F2=data\\F2.pdf /file F3=data\\F3.pdf /file F4=data\\F4.pdf /file F5=data\\F5.pdf /file F6=data\\F6.pdf /file F7=data\\F7.pdf /file F8=data\\F8.pdf /Result=F0[1:-1]+F1[1:-1]+F2[1:-1]+F3[1:-1]+F4[1:-1]+F5[1:-1]+F6[1:-1]+F7[1:-1]+F8[1:-1] /out=data\\res.pdf";
        String[] arr = {com};
        Slicer.main(arr);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String file1 = "data\\res.pdf";
        String file2 = "data\\expResult.pdf";
        File f1 = new File(file1);
        File f2 = new File(file2);

        assertEquals(f1.getTotalSpace(), f2.getTotalSpace());
        assertEquals(f1.getUsableSpace(), f2.getUsableSpace());

    }
}