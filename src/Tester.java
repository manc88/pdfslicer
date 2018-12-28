import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester {

    public static void main(String[] args){


//        Pattern p = Pattern.compile("(.*)\\[(.*):(.*)\\]");
//        Matcher m = p.matcher("grt[1:1]");
//        m.matches();
//        System.out.println(m.group(1));
//        System.out.println(m.group(2));
//        System.out.println(m.group(3));
//
//
//        PDDocument d = PDDocument.load(new File("D:\\Projects\\pdfbox\\in\\cad.pdf"));
//        PDDocument n = new PDDocument();
//        n.addPage(d.getPage(1));
//        n.save("D:\\Projects\\pdfbox\\in\\tester.pdf");

        test();


    }


    private static void test(){
        String com = " /command mix  /file F0=data\\F0.pdf /file F1=data\\F1.pdf /file F2=data\\F2.pdf /file F3=data\\F3.pdf /file F4=data\\F4.pdf /file F5=data\\F5.pdf /file F6=data\\F6.pdf /file F7=data\\F7.pdf /file F8=data\\F8.pdf /Result=F0[1:-1]+F1[1:-1]+F2[1:-1]+F3[1:-1]+F4[1:-1]+F5[1:-1]+F6[1:-1]+F7[1:-1]+F8[1:-1] /out=data\\res.pdf";
        String[] arr = {com};
        Slicer.main(arr);


    }

}
