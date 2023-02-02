import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.util.*;
import java.lang.String;
public class VectorQuantization {
    //setters for vectorWidth and vectorWidth and codeBlockSize
    int quantizedSize ;
    int codeBlockSize;
    int updatedHeight,updatedWidth;
    int vectorHeight,vectorWidth;
    int height;
    int width;
    HashMap<String ,Vector<Integer>>mp=new HashMap<>();
    RW rw=new RW();
    int [][]image;
    Vector<Vector<Integer>>allVectors=new Vector<>();

    public void setVectorHeight(int vectorHeight) {
        this.vectorHeight = vectorHeight;
    }

    public void setVectorWidth(int vectorWidth) {
        this.vectorWidth = vectorWidth;
    }
    public void setCodeBlockSize(int codeBlockSize) {
        this.codeBlockSize=codeBlockSize;
        this.quantizedSize=(int)Math.pow(2,codeBlockSize);
    }

    public String [] compress(String imgfile) throws IOException {
        image=rw.readImage(imgfile);
        height = image.length;
        width = image[0].length;
        if(height%vectorHeight==0){
            updatedHeight=height;
        }
        else{
            updatedHeight= (int) (Math.ceil((double) height/vectorHeight)*vectorHeight);
        }
        if(width%vectorWidth==0){
            updatedWidth=width;
        }
        else{
            updatedWidth= (int) (Math.ceil((double) width/vectorWidth)*vectorWidth);
        }
        int [][]compressedImg=new int[updatedHeight][updatedWidth];
        for (int i = 0; i < updatedHeight; i++) {
            for (int j = 0; j < updatedWidth; j++) {
                if(i>=height){
                    if(j>=width){
                        compressedImg[i][j] = image[height-1][width-1];
                    }
                    else{
                        compressedImg[i][j] = image[height-1][j];
                    }
                }
                else{
                    if(j>=width){
                        compressedImg[i][j] = image[i][width-1];
                    }
                    else{
                        compressedImg[i][j] = image[i][j];
                    }
                }
            }
        }


        for (int i = 0; i < updatedHeight; i+=vectorHeight) {
            for (int j = 0; j < updatedWidth; j += vectorWidth) {
                Vector<Integer> v = new Vector<>();
                int h = i;
                while (h < i + vectorHeight) {
                    int w = j;
                    while (w < j + vectorWidth)
                        v.add(compressedImg[h][w++]);
                    h++;
                }
                allVectors.add(v);
            }
        }
        Vector<Vector<Integer>>quantized=new Vector<Vector<Integer>>();
        HashMap<Integer,Integer>nearest=new HashMap<>();
        for (int i = 0; i < allVectors.size(); i++) {
            nearest.put(i,-1);
        }
        quantized.add(getAverage(allVectors));
        while (quantized.size()!=quantizedSize){
            Vector<Vector<Integer>>copy=new Vector<Vector<Integer>>();
            for (int i = 0; i < quantized.size(); i++) {
                copy.add(quantized.get(i));
            }
            quantized.clear();
            //Splitting
            for (int i = 0; i < copy.size(); i++) {
                Vector<Integer>v1=new Vector<Integer>();
                Vector<Integer>v2=new Vector<Integer>();
                for (int j = 0; j < copy.get(i).size(); j++) {
                    v1.add(Math.max(copy.get(i).get(j)-1,0));
                    v2.add(copy.get(i).get(j)+1);
                }
                quantized.add(v1);
                quantized.add(v2);
            }
            //getting nearest vectors
            for (int i = 0; i <allVectors.size() ; i++) {
                int pos=-1, mn=1000000000;
                for (int j = 0; j < quantized.size(); j++) {
                    int diff=getDifference(allVectors.get(i),quantized.get(j));
                    if(diff<mn){
                        mn=diff;
                        pos=j;
                    }
                }
                nearest.put(i,pos);
            }
            //getting average
            //true isA
            HashMap<Integer,Vector<Vector<Integer>>>nearestVectors=new HashMap<Integer,Vector<Vector<Integer>>>() ;
            for (Map.Entry<Integer,Integer>entry: nearest.entrySet()) {
                nearestVectors.put(entry.getValue(),new Vector<Vector<Integer>>());
            }
            for (Map.Entry<Integer,Integer>entry: nearest.entrySet()) {
                    nearestVectors.get(entry.getValue()).add(allVectors.get(entry.getKey()));
            }
            quantized.clear();
            for (int i = 0; i < nearestVectors.size(); i++) {
                    quantized.add(getAverage(nearestVectors.get(i)));
                }
        }
        Vector<Vector<Integer>>prev=new Vector<Vector<Integer>>();
        String []compressedArr=new String[allVectors.size()];
        while (!quantized.equals(prev)){
            prev=quantized;
            //getting nearest vectors
            for (int i = 0; i <allVectors.size() ; i++) {
                int pos=-1, mn=1000000000;
                for (int j = 0; j < quantized.size(); j++) {
                    int diff=getDifference(allVectors.get(i),quantized.get(j));
                    if(diff<mn){
                        mn=diff;
                        pos=j;
                    }
                }
                nearest.put(i,pos);
            }
            //getting average
            //true isA
            HashMap<Integer,Vector<Vector<Integer>>>nearestVectors=new HashMap<Integer,Vector<Vector<Integer>>>() ;
            for (Map.Entry<Integer,Integer>entry: nearest.entrySet()) {
                nearestVectors.put(entry.getValue(),new Vector<Vector<Integer>>());
            }
            for (Map.Entry<Integer,Integer>entry: nearest.entrySet()) {
                nearestVectors.get(entry.getValue()).add(allVectors.get(entry.getKey()));
            }
            quantized.clear();
            for (int i = 0; i < nearestVectors.size(); i++) {
                quantized.add(getAverage(nearestVectors.get(i)));
            }
            if(quantized.equals(prev)){
                for (int i = 0; i < quantized.size(); i++) {
                    mp.put(binary(i),quantized.get(i));
                }
                for (Map.Entry<Integer,Integer>entry: nearest.entrySet()) {
                    String ss=binary(entry.getValue());
                    compressedArr[entry.getKey()]=ss;
                }
                break;
            }
        }
        return compressedArr;
    }
    public void decompress(String [] compressedArr,String outimgfile) {
        int[][] decompressedImage = new int[updatedHeight][updatedWidth];
        for (int i = 0; i < allVectors.size(); i++) {
            Vector<Integer>v=mp.get(compressedArr[i]);
            int x = (i / (updatedWidth / vectorWidth))*vectorHeight;
            int y = (i % (updatedWidth / vectorWidth))*vectorWidth;
            int idx = 0;
            for (int j = x; j < x + vectorHeight; j++) {
                for (int k = y; k < y + vectorWidth; k++) {
                    decompressedImage[j][k] = v.get(idx);
                    idx++;
                }
            }
        }
        rw.writeImage(decompressedImage,outimgfile);
    }
  public int getDifference(Vector<Integer> v1, Vector<Integer> v2)
  {
        int diff = 0;
        for (int i = 0; i < v1.size(); i++) {
            diff += Math.pow(v1.get(i) - v2.get(i),2);
        }
        diff=(int) Math.sqrt(diff);
        return diff;
  }

    public Vector<Integer> getAverage(Vector<Vector<Integer>> Vectors)
    {
        int[] sums = new int[Vectors.get(0).size()];
        for (Vector<Integer> vector : Vectors ) {
            for (int i = 0; i < vector.size(); i++) {
                sums[i] += vector.get(i);
            }
        }
        Vector<Integer> averageVector = new Vector<>();
        for (int i = 0; i < sums.length; i++) {
            averageVector.add(sums[i] / Vectors.size());
        }
        return averageVector;
    }
    public String binary(int i){
        String s="";
        for (int j = 0; j < codeBlockSize; j++) {
            if(((1<<j)&i)>0){
                s+='1';
            }
            else{
                s+='0';
            }
        }
        String ans="";
        for (int j = s.length()-1; j >=0 ; j--) {
            ans+=s.charAt(j);
        }
        return ans;
    }

    public HashMap<String, Vector<Integer>> getMp() {
        return mp;
    }

    int originalSize(){
        return height*width*8;
    }
    int compressedSize(){
        int numberOfBlocks=(height*width)/(vectorWidth*vectorHeight);
        int labelSize=numberOfBlocks*codeBlockSize;
        int codeBookSize=quantizedSize*(vectorHeight*vectorWidth)*8;
        return codeBookSize+labelSize;
    }
}
