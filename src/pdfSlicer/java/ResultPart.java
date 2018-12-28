package pdfSlicer.java;

public class ResultPart {

    String alias;
    int start;
    int end;

    ResultPart(String alias,int start,int end){
        this.alias=alias;
        this.start=start;
        this.end = end;
    }

}
