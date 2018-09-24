

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester {

    public static void main(String[] args) throws IOException{
        Pattern p = Pattern.compile("(.*)\\[(.*):(.*)\\]");
        Matcher m = p.matcher("grt[1:1]");
        m.matches();
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));


        PDDocument d = PDDocument.load(new File("D:\\Projects\\pdfbox\\in\\cad.pdf"));
        PDDocument n = new PDDocument();
        n.addPage(d.getPage(1));
        n.save("D:\\Projects\\pdfbox\\in\\tester.pdf");


    }

}
