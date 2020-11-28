import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.HOGDescriptor;

public class Detection implements Runnable{
    Mat img;
    HOGDescriptor hog;
    MatOfRect founded;
    MatOfDouble foundedWeights;

    public Detection(Mat img, HOGDescriptor hog, MatOfRect founded, MatOfDouble foundedWeights){
        this.img=img;
        this.hog=hog;
        this.founded=founded;
        this.foundedWeights=foundedWeights;
    }

    @Override
    public void run(){
        hog.detectMultiScale(img,founded,foundedWeights);
        System.out.println("dziala");
    }
}
