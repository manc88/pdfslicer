package pdfSlicer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class SlicerTest {


    private String expResultFileName;
    private String resultFileName;
    private HashMap<String,String> files;
    private String resultFormula;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        HashMap<String,String> files = new HashMap<>();
        files.put("F0","data\\F0.pdf");
        files.put("F1","data\\F1.pdf");
        files.put("F2","data\\F2.pdf");
        files.put("F3","data\\F3.pdf");

       return Arrays.asList(new Object[][]{
                {
                "data\\expResult0.pdf",
                "data\\testRes0.pdf",
                files,
                "Result=F0[1:-1]+F1[1:-1]"
                },
               {
                        "data\\expResult1.pdf",
                       "data\\testRes1.pdf",
                       files,
                       "Result=F2[1:1]+F3[1:1]"
               }
           });
    }

    public SlicerTest(String expResultFileName, String resultFileName, HashMap<String, String> files, String resultFormula) {

        this.expResultFileName = expResultFileName;
        this.resultFileName = resultFileName;
        this.files = files;
        this.resultFormula = resultFormula;
    }

    @Test(timeout = 2000)
    public void mixTest() throws IOException{

        Files.deleteIfExists(Paths.get(resultFileName));
        Slicer slicer = new Slicer();
        slicer.mix(files,resultFormula,resultFileName);

        File f1 = new File(resultFileName);
        File f2 = new File(expResultFileName);

        assertTrue(f1.exists());
        assertTrue(f2.exists());
        assertEquals(f1.getTotalSpace(), f2.getTotalSpace());
        assertEquals(f1.getUsableSpace(), f2.getUsableSpace());

    }
}