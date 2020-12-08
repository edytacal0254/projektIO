import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class program {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static final ArrayList<double[]> colorRGB=new ArrayList<>();
    private static final ArrayList<String> colorNames=new ArrayList<>();
    private static String path;
    private static Size frameSize;
    private static String tempVideoPath;
    private static String tempVideoFormat;
    private static File logs;
    private static PrintWriter logsWriter;


    public static void setPath(String path) throws IOException {
        String tempPath = path.substring(0,path.length()-4);
        String format = path.substring(path.length()-4);
        String logsPath = tempPath + "_logs.txt";

        program.path = path;
        program.tempVideoPath = tempPath + "_tempOutput" + format;
        program.tempVideoFormat = format;
        program.logs = new File(logsPath);
        program.logsWriter = new PrintWriter(logsPath, StandardCharsets.UTF_8);
    }

    public static String getPath() {
        return path;
    }

    public static String getTempVideoPath() {
        return tempVideoPath;
    }

    public static String getTempVideoFormat() {
        return tempVideoFormat;
    }


    public static void setFrameSize(Mat frame) {
        program.frameSize = frame.size();
    }
    public static Size getFrameSize() {
        return frameSize;
    }


    public static void saveVideoAs(String path){
        if (!tempVideoPath.isEmpty()){
            if (!path.endsWith(".mp4") || !path.endsWith(".avi")) {
                path = path + getTempVideoFormat();
            }

            File tempVideo = new File(getTempVideoPath());
            tempVideo.renameTo(new File(path));

        }
    }

    public static void deleteTempVideo(){
        File tempVideo = new File(getTempVideoPath());
        if (tempVideo.isFile()){
            tempVideo.delete();
            System.out.println("deleted");
        }
    }

    public static String calcTimeStamp(double frameMs){
        double miliseconds = frameMs%1000/2;
        int seconds = (int) (((frameMs-miliseconds)/1000)%60);
        int minutes = (int) ((((frameMs-miliseconds)/1000)-seconds)/60);
        String delim =":";
        String time = minutes + delim + seconds + delim + (int) miliseconds;
        return time;
    }

    public static void setColorArray(){
        colorRGB.add(new double[]{255,255,255});
        colorNames.add("white");
        //colorRGB.add(new double[]{192,192,192});
        //colorNames.add("silver");
        colorRGB.add(new double[]{128,128,128});
        colorNames.add("grey");
        colorRGB.add(new double[]{0,0,0});
        colorNames.add("black");
        colorRGB.add(new double[]{255,0,0});
        colorNames.add("red");
        colorRGB.add(new double[]{128,0,0});
        colorNames.add("maroon");
        colorRGB.add(new double[]{255,255,0});
        colorNames.add("olive");
        colorRGB.add(new double[]{0,255,0});
        colorNames.add("lime");
        colorRGB.add(new double[]{0,128,0});
        colorNames.add("green");
        colorRGB.add(new double[]{0,255,255});
        colorNames.add("aqua");
        colorRGB.add(new double[]{0,128,128});
        colorNames.add("teal");
        colorRGB.add(new double[]{0,0,255});
        colorNames.add("blue");
        colorRGB.add(new double[]{0,0,128});
        colorNames.add("navy");
        colorRGB.add(new double[]{255,0,255});
        colorNames.add("fuchsia");
        colorRGB.add(new double[]{128,0,128});
        colorNames.add("purple");
    }

    public static String getMeanRGBColor(Mat img){
        float pixels = img.rows()*img.cols();
        float red = 0;
        float green=0;
        float blue=0;

        //Wyliczamy średni kolor
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                double[] rgb = img.get(i,j);
                red +=((int) rgb[0])/pixels;
                green += ((int) rgb[1])/pixels;
                blue += ((int) rgb[2])/pixels;
            }
        }

        //Sprawdzamy który kolor z listy jest najbliższy do wyliczonego koloru
        double distance=442; //(0,0,0)--(255,255,255) ok. 441.6
        int colIdx=0;
        for (int j=0; j <colorRGB.size();j++){
            double redT=colorRGB.get(j)[0];
            double greenT=colorRGB.get(j)[1];
            double blueT=colorRGB.get(j)[2];
            double tempDistance= Math.sqrt(Math.pow(redT-red,2)+Math.pow(greenT-green,2)+Math.pow(blueT-blue,2));
            if (tempDistance<distance){
                distance=tempDistance;
                colIdx=j;
            }
        }

        return (colorNames.get(colIdx));
    }


    public static void processVideo() throws InterruptedException, IOException {
        System.loadLibrary("opencv_videoio_ffmpeg450_64");

        BackgroundSubtractor backSub = Video.createBackgroundSubtractorKNN();

        String path = getPath();
        VideoCapture capture = new VideoCapture(path);
        if (!capture.isOpened()) {
            System.out.println("Unable to open: " + path);
            logsWriter.close();
            logs.delete();
            return;
        }

        Mat frameForModifing = new Mat();

        HOGDescriptor hog = new HOGDescriptor(new Size(48,96), new Size(16,16), new Size(8,8), new Size(8,8), 9);
        hog.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());

        capture.read(frameForModifing);
        setFrameSize(frameForModifing);

        //Ustalamy rozszerzenie i inicjujemy VideoWriter
        int fourcc;
        if (getTempVideoFormat().equals(".avi")) {
            fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G' );
        }else {
            fourcc= VideoWriter.fourcc('m','p','4','v');
        }
        VideoWriter videoWriter= new VideoWriter(getTempVideoPath(),fourcc,capture.get(Videoio.CAP_PROP_FPS),getFrameSize());

        String colorsInFrame="";
        int humansInFrame=0;

        //właściwe przetwarzanie wideos
        while (capture.isOpened()){
            capture.read(frameForModifing);
            if (frameForModifing.empty()) {
                break;
            }
            Mat diff = new Mat();
            backSub.apply(frameForModifing,diff);
            Imgproc.GaussianBlur(diff,diff,new Size(5,5),0);
            Imgproc.threshold(diff,diff,20,255,Imgproc.THRESH_BINARY);
            Imgproc.morphologyEx(diff,diff,Imgproc.MORPH_OPEN,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE,new Size(3,3)));

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hier = new Mat();
            Imgproc.findContours(diff,contours,hier,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

            //zbieramy Rect z wykrytym ruchem
            Rect motionRect;
            ArrayList<Rect> motionRectArray = new ArrayList<>();
            for (int idx = 0; idx < contours.size(); idx++) {
                motionRect = Imgproc.boundingRect(contours.get(idx));
                    if (motionRect.width>=50 && motionRect.height>=100 && !(motionRect.size().equals(getFrameSize()))) {
                        motionRectArray.add(motionRect);
                    }
            }


            //Przetwarzamy każdy Rect z wykrytym ruchem
            MatOfRect found = new MatOfRect();
            MatOfDouble foundWeights = new MatOfDouble();

            if (motionRectArray.size() > 0) {
                Iterator<Rect> it2 = motionRectArray.iterator();
                while (it2.hasNext()) {
                    Rect rectForDetecting = it2.next();

                    Mat frameForDetector = new Mat(frameForModifing,rectForDetecting);

                    //Todo dopasować parametry [przy domyślych jest nadgorliwy]
                    hog.detectMultiScale(frameForDetector,found,foundWeights);//,0,new Size(8,8),new Size(4,4),1.05,1,true);
                    Rect[] humans = found.toArray();

                    //Zaznaczamy wykrytych ludzi
                    for (int i=0;i<humans.length;i++){
                        Rect rectForOrginalImage = new Rect(rectForDetecting.x + humans[i].x, rectForDetecting.y + humans[i].y, humans[i].width, humans[i].height);
                        Imgproc.rectangle(frameForModifing, rectForOrginalImage, new Scalar(0, 0, 255), 1);
                        Mat forColor = new Mat(frameForModifing,rectForOrginalImage);
                        String color = getMeanRGBColor(forColor.clone());
                        colorsInFrame += color + ", ";
                    }

                    humansInFrame+=humans.length;

                }

            }

            //uzupełnianie plików wynikowych dla konkretnej ramki

            videoWriter.write(frameForModifing);

            if (!colorsInFrame.equals("")) {
                colorsInFrame = colorsInFrame.substring(0, colorsInFrame.length() - 2);
            }

            String delim = " - ";
            String log = calcTimeStamp(capture.get(Videoio.CAP_PROP_POS_MSEC)) + delim +
                    humansInFrame + delim + colorsInFrame;
            logsWriter.println(log);
            logsWriter.flush();
            colorsInFrame="";
            humansInFrame=0;

        }

        capture.release();
        videoWriter.release();

    }

    //do testów bez GUI
    public static void main(String[] args) throws InterruptedException, IOException {
        EventQueue.invokeLater(() ->
        {
           var gui = new GUI();
           gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           gui.setTitle("Analizator monitoringu");
           gui.setResizable(false);
           gui.setVisible(true);
        });
//        //ścieżka do pliku musi być absolutna
//        String input = "C:\\Users\\Stasz\\IdeaProjects\\projektIO\\src\\grupaB1.avi";
//        setColorArray();
//        setPath(input);
//        processVideo();
//        saveVideoAs("C:\\Users\\Stasz\\IdeaProjects\\projektIO\\src\\testutest");
//        deleteTempVideo();
//        System.exit(0);
    }

}
