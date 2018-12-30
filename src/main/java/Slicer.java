import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Slicer {

    private static final String RESULT_SEQUENCE_SEPARATOR = "\\+";

    class ResultPart {

        String alias;
        int start;
        int end;

        ResultPart(String alias,int start,int end){
            this.alias=alias;
            this.start=start;
            this.end = end;
        }

    }

    class ResultSlicer{

        private HashMap<String,PDDocument> collection;
        ResultSlicer(HashMap<String,PDDocument> collection){
            this.collection = collection;
        }
        boolean add(PDDocument doc,String alias,int start,int end){

            PDDocument donor = collection.getOrDefault(alias,null);
            int donorLength = donor.getNumberOfPages();

            if(donor==null){
                System.err.println("Invalid file alias " + alias);
                return false;
            }

            if(start==-1){
                start = donorLength-1;
            }else{
                start--;
            }
            if(end==-1){
                end = donorLength-1;
            }else{
                end--;
            }

            for(int i = start;i<=end;i++) doc.addPage(donor.getPage(i));

            return true;

        }

    }

    //Result=A[1:1]+B[1:1]+A[2:4]+B[2:2]
    //
    private ResultPart[] getResultSequence(String resultToken){

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

    public boolean mix(HashMap<String,String> filesAlias, String Result, String resultFileNAme){


        HashMap<String,PDDocument> files = new HashMap<>();
        for(String alias : filesAlias.keySet()){
            String filePath = filesAlias.get(alias);
            try {
                files.put(alias,PDDocument.load(new File(filePath)));
            } catch (IOException e) {
                System.err.println(String.format("Error loading file: %s", filePath));
                e.printStackTrace();
                return false;
            }
        }

        ResultPart[] resSequence;
        resSequence = getResultSequence(Result);

        PDDocument doc = new PDDocument();
        ResultSlicer rs = new ResultSlicer(files);

        for (ResultPart resultPart : resSequence) {
            if (!rs.add(doc, resultPart.alias, resultPart.start, resultPart.end)) {
                System.err.println("Error with page concat");
                return false;
            }
        }

        try {
            doc.save(new File(resultFileNAme));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}



