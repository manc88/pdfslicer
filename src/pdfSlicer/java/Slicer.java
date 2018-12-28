
package pdfSlicer.java;

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


    private static final String COMMAND_KEY_WORD = "COMMAND";
    private static final String INVALID_ARGUMENTS = "Invalid args!";
    private static final String RESULT_KEY_WORD = "RESULT";
    private static final String FILE_KEY_WORD = "FILE";
    private static final String RESULT_SEQUENCE_SEPARATOR = "\\+";
    private static final String FILE_ALIAS_SEPARATOR = "=";
    private static final String COMMAND_MIX = "MIX";
    private static final String OUT_KEY_WORD = "OUT";
    private static final String COMMAND_ADD_TEXT_PAGE = "ADDINFOPAGE";
    private static final String ADD_TEXT_KEYWORD = "STRTEXT";


    static{
        try {
            System.setErr(new PrintStream(new File("err.txt")));
            System.setOut(new PrintStream(new File("stacktrace.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void test(String[] args){
        if(args.length<1){ System.out.println(INVALID_ARGUMENTS);return; }

        StringBuilder sb = new StringBuilder();
        for(String arg:args){
            sb.append(arg.trim().replaceAll(" +", " "));
            sb.append(" ");
        }

        String command = sb.toString();
        String[] commandArgs = command.split("/");

        HashMap<String,PDDocument> files = new HashMap<>();
        String action = null;
        String out = null;
        ResultPart[] resSequence = null;
        ArrayList<String> text= new ArrayList<>();

        for(String word:commandArgs){

            if(word.toUpperCase().contains(COMMAND_KEY_WORD)){
                action = word.toUpperCase().replace(COMMAND_KEY_WORD,"");
            }
            if(word.toUpperCase().contains(FILE_KEY_WORD)){

                String[] fileParam = word.split(FILE_ALIAS_SEPARATOR);
                if(fileParam.length!=2){System.out.println(INVALID_ARGUMENTS);return;}
                String fAlias = fileParam[0].replace("file","").trim();
                String fName = fileParam[1].trim();

                try {
                    files.put(fAlias,PDDocument.load(new File(fName)));
                } catch (IOException e) {
                    System.err.println(String.format("Error loading file: %s", fName));
                    e.printStackTrace();
                    return;
                }

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


        }


        switch (action.toUpperCase().trim()){
            case COMMAND_MIX:
                if (out!=null && !files.isEmpty() && resSequence!=null){
                    mix(files,resSequence,out);
                }
                else{
                    System.err.println(INVALID_ARGUMENTS);
                }
                break;
            case COMMAND_ADD_TEXT_PAGE:
                break;
        }
    }

    //command mix /file A=test.pdf /file B=logo.pdf /Result=A[1:1]+B[1:1]+A[2:4]+B[2:2] /out=res.pdf
    public static void main (String[] args) {

        if(args.length<1){ System.out.println(INVALID_ARGUMENTS);return; }

        StringBuilder sb = new StringBuilder();
        for(String arg:args){
            sb.append(arg.trim().replaceAll(" +", " "));
            sb.append(" ");
        }

        String command = sb.toString();
        String[] commandArgs = command.split("/");

        HashMap<String,PDDocument> files = new HashMap<>();
        String action = null;
        String out = null;
        ResultPart[] resSequence = null;
        ArrayList<String> text= new ArrayList<>();

        for(String word:commandArgs){

            if(word.toUpperCase().contains(COMMAND_KEY_WORD)){
                action = word.toUpperCase().replace(COMMAND_KEY_WORD,"");
            }
            if(word.toUpperCase().contains(FILE_KEY_WORD)){

                String[] fileParam = word.split(FILE_ALIAS_SEPARATOR);
                if(fileParam.length!=2){System.out.println(INVALID_ARGUMENTS);return;}
                String fAlias = fileParam[0].replace("file","").trim();
                String fName = fileParam[1].trim();

                try {
                    files.put(fAlias,PDDocument.load(new File(fName)));
                } catch (IOException e) {
                    System.err.println(String.format("Error loading file: %s", fName));
                    e.printStackTrace();
                    return;
                }

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


        }


        switch (action.toUpperCase().trim()){
            case COMMAND_MIX:
                if (out!=null && !files.isEmpty() && resSequence!=null){
                    mix(files,resSequence,out);
                }
                else{
                    System.err.println(INVALID_ARGUMENTS);
                }
                break;
            case COMMAND_ADD_TEXT_PAGE:
                break;
        }

    }

    //Result=A[1:1]+B[1:1]+A[2:4]+B[2:2]
    //
    private static ResultPart[] getResultSequence(String resultToken){

        String resultTemplate = resultToken.split("=")[1].trim().replaceAll(" +", " ");
        String[] arrayPages = resultTemplate.split(RESULT_SEQUENCE_SEPARATOR);
        ResultPart[] res = new ResultPart[arrayPages.length];
        for(int i=0;i<arrayPages.length;i++){

            Pattern p = Pattern.compile("(.*)\\[(.*):(.*)\\]");
            Matcher m = p.matcher(arrayPages[i]);
            m.matches();
            res[i] = new ResultPart(m.group(1),Integer.valueOf(m.group(2)),Integer.valueOf(m.group(3)));

        }

        return res;

    }

    private static boolean mix(HashMap<String,PDDocument> files, ResultPart[] resSequence, String resultFileNAme){

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

    @Deprecated
    public static boolean add_text_page(HashMap<String,PDDocument> files, ArrayList<String> text, int pagePosition, String resultFileNAme ){

        for(PDDocument doc:files.values()){

            PDPage newPage = new PDPage();
            PDPageTree allPages = doc.getDocumentCatalog().getPages();
            allPages.insertBefore(newPage,doc.getPage(pagePosition-1));
            try {
                PDType0Font font = PDType0Font.load(doc, new File("C:\\Windows\\Fonts\\Arial.ttf"));
                PDPageContentStream contentStream = new PDPageContentStream(doc, newPage);

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



