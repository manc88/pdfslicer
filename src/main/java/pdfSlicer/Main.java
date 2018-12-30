package pdfSlicer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

public class Main {

    private static final String COMMAND_KEY_WORD = "COMMAND";
    private static final String INVALID_ARGUMENTS = "Invalid args!";
    private static final String RESULT_KEY_WORD = "RESULT";
    private static final String FILE_KEY_WORD = "FILE";
    private static final String FILE_ALIAS_SEPARATOR = "=";
    private static final String COMMAND_MIX = "MIX";
    private static final String OUT_KEY_WORD = "OUT";
    private static final String COMMAND_ADD_TEXT_PAGE = "ADDINFOPAGE";

    static{
        try {
            System.setErr(new PrintStream(new File("err.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //command mix /file A=test.pdf /file B=logo.pdf /Result=A[1:1]+B[1:1]+A[2:4]+B[2:2] /out=res.pdf
    public static void main(String[] args) {

        if(args.length<1){ System.out.println(INVALID_ARGUMENTS);return; }

        StringBuilder sb = new StringBuilder();
        for(String arg:args){
            sb.append(arg.trim().replaceAll(" +", " "));
            sb.append(" ");
        }

        String command = sb.toString();
        String[] commandArgs = command.split("/");

        HashMap<String, String> files = new HashMap<>();
        String action = "";
        String out = "";
        String  resKW = "";

        for(String word:commandArgs){

            if(word.toUpperCase().contains(COMMAND_KEY_WORD)){
                action = word.toUpperCase().replace(COMMAND_KEY_WORD,"");
            }
            if(word.toUpperCase().contains(FILE_KEY_WORD)){
                String[] fileParam = word.split(FILE_ALIAS_SEPARATOR);
                if(fileParam.length!=2){System.out.println(INVALID_ARGUMENTS);return;}
                String fAlias = fileParam[0].replace("file","").trim();
                String fName = fileParam[1].trim();
                files.put(fAlias,fName);
            }

            if(word.toUpperCase().contains(RESULT_KEY_WORD)){
                resKW = word;
            }

            if(word.toUpperCase().contains(OUT_KEY_WORD)){
                out = word.split("=")[1];
            }

        }

        Slicer slicer = new Slicer();

        switch (action.toUpperCase().trim()){
            case COMMAND_MIX:
                if (!out.equals("") && !files.isEmpty() && !resKW.equals("")){
                    slicer.mix(files,resKW,out);
                }
                else{
                    System.err.println(INVALID_ARGUMENTS);
                }
                break;
            case COMMAND_ADD_TEXT_PAGE:
                break;
        }

    }

}
