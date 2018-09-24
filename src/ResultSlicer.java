import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.HashMap;

class ResultSlicer{


    HashMap<String,PDDocument> collection;

    ResultSlicer(HashMap<String,PDDocument> collection){
        this.collection = collection;
    }


    public boolean add(PDDocument doc,String alias,int start,int end){

        PDDocument donor = collection.getOrDefault(alias,null);
        int donorlength = donor.getNumberOfPages();

        if(donor==null){
            System.err.println("invalid file alias " + alias);
            return false;
        }

        if(start==-1){
            start = donorlength-1;
        }else{
           start--;
        }
        if(end==-1){
            end = donorlength-1;
        }else{
            end--;
        }

        for(int i = start;i<=end;i++){
            doc.addPage(donor.getPage(i));
        }
        return true;


    }


}