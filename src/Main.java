import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
class Main {
    public static void main(String[] args) throws IOException {
        String imgfile = "C:\\My Folders\\Abdo\\FCAI\\Subjects\\Third year\\First term\\Data Compression\\Ass\\vector-quantization\\Messi.jpg";
        String outimgfile = "C:\\My Folders\\Abdo\\FCAI\\Subjects\\Third year\\First term\\Data Compression\\Ass\\vector-quantization\\output.jpg";
        VectorQuantization vectorQuantization = new VectorQuantization();
        vectorQuantization.setVectorHeight(2);
        vectorQuantization.setVectorWidth(2);
        vectorQuantization.setCodeBlockSize(2);
        vectorQuantization.decompress(vectorQuantization.compress(imgfile), outimgfile);
        HashMap<String,Vector<Integer>>mp=new HashMap<String,Vector<Integer>>();
        mp=vectorQuantization.getMp();
        int h=vectorQuantization.vectorHeight;
        try {
            FileWriter writer = new FileWriter("output.txt");
            writer.write("Code Book: \n");
            for (Map.Entry<String, Vector<Integer>> entry : mp.entrySet()) {
                writer.write(entry.getKey());
                writer.write("\n");
                for (int i = 0; i < entry.getValue().size(); i++) {
                    writer.write(String.valueOf(entry.getValue().get(i)));
                    writer.write(" ");
                    if ((i + 1) % h == 0) {
                        writer.write("\n");
                    }
                }
            }
            writer.write("\n");
            writer.write("OriginalSize: "+vectorQuantization.originalSize()+" bits.\n");
            writer.write("CompressedSize: "+vectorQuantization.compressedSize()+" bits.\n");
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
