
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDType0Font;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Slicer {


    public static final String COMMAND_KEY_WORD = "COMMAND";
    public static final String INVALID_ARGUMENTS = "Invalid args!";
    public static final String RESULT_KEY_WORD = "RESULT";
    public static final String FILE_KEY_WORD = "FILE";
    public static final String RESULT_SEQUENCE_SEPARATOR = "\\+";
    public static final String FILE_ALIAS_SEPARATOR = "=";
    public static final String COMMAND_MIX = "MIX";
    public static final String OUT_KEY_WORD = "OUT";
    public static final String COMMAND_ADD_TEXT_PAGE = "ADDINFOPAGE";
    public static final String ADD_TEXT_KEYWORD = "STRTEXT";
    public static final String POSITION_KEYWORD = "POS";


    static{
        try {
            System.setErr(new PrintStream(new File("err.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main (String args[]) throws IOException {

        //example  /command mix /file A=test.pdf /file B=logo.pdf /Result=A[1:1]+B[1:2]+A[2:4]+B[2] /out res.pdf

        // /command ADDINFOPAGE /file A=D:\\Projects\\pdfbox\\src\\cad.pdf  /STRTEXT=fuckingtest  /POS=1 /out D:\\Projects\\pdfbox\\src\\res.pdf

        if(args.length<1){ System.out.println(INVALID_ARGUMENTS);return; }

        StringBuilder sb = new StringBuilder();
        for(String arg:args){
            sb.append(arg.trim().replaceAll(" +", " "));
            sb.append(" ");
        }

        String command = sb.toString();

        //command = "/command ADDINFOPAGE /file A=cad.pdf  /STRTEXT тест1 /STRTEXT тест2 /POS=2 /out res.pdf";

        String commandArgs[] = command.split("/");

        HashMap<String,PDDocument> files = new HashMap<>();
        String action = null;
        String out = null;
        ResultPart[] resSequence = null;
        ArrayList<String> text= new ArrayList<>();
        int pos = 0;

        for(String word:commandArgs){

            if(word.toUpperCase().contains(COMMAND_KEY_WORD)){
                action = word.toUpperCase().replace(COMMAND_KEY_WORD,"");
            }
            if(word.toUpperCase().contains(FILE_KEY_WORD)){

                String fileParam[] = word.split(FILE_ALIAS_SEPARATOR);
                if(fileParam.length!=2){System.out.println(INVALID_ARGUMENTS);return;}
                String fAlias = fileParam[0].replace("file","").trim();
                String fName = fileParam[1].trim();
                files.put(fAlias,PDDocument.load(new File(fName)));
            }

            if(word.toUpperCase().contains(RESULT_KEY_WORD)){
                resSequence = getResultSequence(word);
            }

            if(word.toUpperCase().contains(OUT_KEY_WORD)){
                out = word.split("=")[1];
            }

            if(word.toUpperCase().contains(ADD_TEXT_KEYWORD)){
                text.add(word.split("=")[1]);
            }
            if(word.toUpperCase().contains(POSITION_KEYWORD)){

                pos = Integer.valueOf(word.split("=")[1].trim());
            }


        }


        switch (action.toUpperCase().trim()){
            case COMMAND_MIX:
                if (out!=null && !files.isEmpty() && resSequence!=null){
                    mix(files,resSequence,out);
                }
                else{
                    System.err.println("Not enough params for mix");
                }
                break;
            case COMMAND_ADD_TEXT_PAGE:
                if (out!=null && !files.isEmpty() && !text.equals("")){
                    add_text_page(files,text,pos,out);
                }
                else{
                    System.err.println("Not enough params for add text page");
                }
                break;
        }

    }

    //Result A[1]+B[1]+A[2:4]+B[2]
    //
    public static ResultPart[] getResultSequence(String resultToken){

        String resultTemplate = resultToken.split("=")[1].trim().replaceAll(" +", " ");
        String arrayPages[] = resultTemplate.split(RESULT_SEQUENCE_SEPARATOR);
        ResultPart res[] = new ResultPart[arrayPages.length];
        for(int i=0;i<arrayPages.length;i++){

           // String fileAlias = pagePart.re
            Pattern p = Pattern.compile("(.*)\\[(.*):(.*)\\]");
            Matcher m = p.matcher(arrayPages[i]);
            m.matches();
            res[i] = new ResultPart(m.group(1),Integer.valueOf(m.group(2)),Integer.valueOf(m.group(3)));

        }

        return res;

    }

    public static boolean mix(HashMap<String,PDDocument> files, ResultPart[] resSequence, String resultFileNAme){

        //
        PDDocument doc = new PDDocument();
        ResultSlicer rs = new ResultSlicer(files);
        boolean ret = false;
        for(int i=0;i<resSequence.length;i++){
            ret = rs.add(doc,resSequence[i].alias,resSequence[i].start,resSequence[i].end);
        }

        try {
            doc.save(new File(resultFileNAme));
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;

    }

    public static boolean add_text_page(HashMap<String,PDDocument> files, ArrayList<String> text, int pagePosition, String resultFileNAme ){

        for(PDDocument doc:files.values()){

            PDPage newpage = new PDPage();
            PDPageTree allPages = doc.getDocumentCatalog().getPages();
            allPages.insertBefore(newpage,doc.getPage(pagePosition-1));
            try {
                PDType0Font font = PDType0Font.load(doc, new File("C:\\Windows\\Fonts\\Arial.ttf"));
                PDPageContentStream contentStream = new PDPageContentStream(doc, newpage);

                contentStream.beginText();
                //contentStream.setFont(PDType1Font.COURIER_BOLD, 20);
                contentStream.setFont(font, 14);
                contentStream.setLeading(20.5f);

                contentStream.newLineAtOffset(20, 700);
                for(String str:text){

                    contentStream.showText(str);
                    contentStream.newLine();

                }
                contentStream.endText();
                contentStream.close();
                doc.save(new File(resultFileNAme));
                doc.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        return true;
    }



}



