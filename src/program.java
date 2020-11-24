import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class program {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    /*
    //niepotrzebne
    public static File videoFile;
    public static void setVideo(String path){
        File tempVideo = new File(path);
        if(tempVideo.exists() && !tempVideo.isDirectory()) {
            if (path.endsWith(".avi") || path.endsWith(".mp4")) {
                videoFile = tempVideo;
                System.out.println("Loaded video");
            } else {
                throw new IllegalArgumentException("Video format must be either '.avi' or '.mp4'");
            }
        }else{
            throw new IllegalArgumentException("Wrong path");
        }
    }*/

    public static void processVideo(String path) {
        System.loadLibrary("opencv_videoio_ffmpeg450_64");
        BackgroundSubtractor backSub = Video.createBackgroundSubtractorKNN();
        //na podstawie przykładowego filmiku
        //trudno stwierdzić,który lepiej sobie radzi
        VideoCapture capture = new VideoCapture(path);
        if (!capture.isOpened()) {
            System.err.println("Unable to open: " + path);
            System.exit(0);
        }

            Mat frame1 = new Mat();
        Mat frame2 = new Mat();

            //capture.read(frame1);   //poprawić dobrze by było gdyby to było tło
        capture.read(frame2); //poprawić dobrze by było gdyby to było tło
        while (capture.isOpened()){
            capture.read(frame2);
            if (frame2.empty()) {
                break;
            }
            Mat diff = new Mat(); //do wykrycia ruchu
                //Mat gray = new Mat();
                //Mat blur = new Mat();
                //Mat thres = new Mat();
                //Mat dilated = new Mat();
                //Core.absdiff(frame1,frame2,diff);

            backSub.apply(frame2,diff);
            Imgproc.GaussianBlur(diff,diff,new Size(5,5),0);
            Imgproc.threshold(diff,diff,20,255,Imgproc.THRESH_BINARY);
            //chyba wystarczy bez tego,
            //Imgproc.dilate(diff,diff,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE,new Size(3,3)),new Point(-1,-1),3);

            Imgproc.morphologyEx(diff,diff,Imgproc.MORPH_OPEN,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE,new Size(3,3)));

                //Imgproc.cvtColor(diff,gray,Imgproc.COLOR_BGR2GRAY);
                //Imgproc.GaussianBlur(gray,blur,new Size(5,5),0);
                //Imgproc.threshold(blur,thres,20,255,Imgproc.THRESH_BINARY);
                //Imgproc.dilate(thres,dilated,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(3,3)),new Point(-1,-1),3);
                //Imgproc.findContours(thres,3,2);
                //HighGui.imshow("FG Mask", thres);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hier = new Mat();
            Imgproc.findContours(diff,contours,hier,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

            Rect r = null;
            ArrayList<Rect> rect_array = new ArrayList<>();
            double maxArea = 100;
            int maxAreaIdx = -1;
            for (int idx = 0; idx < contours.size(); idx++) {
                Mat contour = contours.get(idx);
                double contourarea = Imgproc.contourArea(contour);
                if (contourarea > maxArea) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                rect_array.add(r);
                }
            }
            if (rect_array.size() > 0) {
                Iterator<Rect> it2 = rect_array.iterator();
                while (it2.hasNext()) {
                    Rect obj = it2.next();
                    Imgproc.rectangle(frame2, obj.br(), obj.tl(),
                            new Scalar(0, 255, 0), 1);
                }

            }

            HighGui.imshow("frame2",frame2);

            capture.read(frame1);
            HighGui.imshow("framdde2",frame1);

            HighGui.imshow("dilated", diff);
            // get the input from the keyboard
            // (nie mam pojęcia po co to ale w każdym tutorialu jest)
            int keyboard = HighGui.waitKey(30);
            if (keyboard == 'q' || keyboard == 27) {
                break;
            }
        }
        HighGui.waitKey();
        System.exit(0);
    }
    public static void main(String[] args) {
        String input = "C:\\Users\\edyta\\IdeaProjects\\projektIO\\src\\grupaB1.mp4";
        //String input = "grupaB1.mp4";
        //processVideo("grupaB1.mp4");
        processVideo(input);
        //nie chce wczytać pliku ze ścieżki względnej <?>
    }

}
