package pdfSlicer.java;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.util.HashMap;

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