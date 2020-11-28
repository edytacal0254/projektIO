import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

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
                //obejdzie się bez nich

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hier = new Mat();
            Imgproc.findContours(diff,contours,hier,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);



            ArrayList<Mat> potentialHumans = new ArrayList<>();

            Rect r;
            ArrayList<Rect> rect_array = new ArrayList<>();
            //double maxArea = 5000; //100*50
            //int maxAreaIdx = -1;
            for (int idx = 0; idx < contours.size(); idx++) {
                //Mat contour = contours.get(idx);
                //double contourarea = Imgproc.contourArea(contour);
                //if (contourarea >= maxArea) {
                //maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(idx));
                    if (r.width>=50 && r.height>=100) {
                        rect_array.add(r);
                    }
                //}
            }
            if (rect_array.size() > 0) {
                Iterator<Rect> it2 = rect_array.iterator();
                while (it2.hasNext()) {
                    Rect obj = it2.next();
                    Imgproc.rectangle(frame2, obj.br(), obj.tl(),
                            new Scalar(0, 255, 0), 1);

                    Mat temp1 = new Mat(frame2,obj);

                    potentialHumans.add(temp1.clone());

                    //hog.detectMultiScale(temp,found,foundWeights);

                    /*ArrayList<Rect> found_array = new ArrayList<>();
                    found_array.addAll(Arrays.asList(found.toArray()));
                    Iterator<Rect> it3 = found_array.iterator();
                    while (it3.hasNext()){
                        Rect obj3 = it3.next();
                        Imgproc.rectangle(frame2,obj3.br(),obj3.tl(),new Scalar(255,0,0),1);
                    }*/


                }

            }
            MatOfRect found = new MatOfRect();
            MatOfDouble foundWeights = new MatOfDouble();

            //próba nr1
            /*
            HOGDescriptor hog = new HOGDescriptor();
            hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
            Iterator<Mat> it3 = potentialHumans.iterator();
            while (it3.hasNext()){
                Mat temp = it3.next();
                hog.detectMultiScale(temp,found,foundWeights);
                //uszkadza  stos.....

            }*/


            //próba nr2 (z wątkami)
            /*
            for(Mat t : potentialHumans){

                //HOGDescriptor hog = new HOGDescriptor();
                //hog._winSize = Size(48, 96);
                //HOGDescriptor hog = new HOGDescriptor(new Size(48,96),new Size(12,12),new Size(6,6),new Size(6,6),9);
                //hog.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());
                // powinien być daimler ale nie da się go załadować <?>
                HOGDescriptor hog = new HOGDescriptor();
                hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

                //HighGui.imshow("framhjhhhe2",t);
                Detection det = new Detection(t,hog,found,foundWeights);
                Thread thread = new Thread(det);
                //hog.detectMultiScale(t,found,foundWeights);
                //uszkadza stos.....
                thread.start();
            }
            */



            HighGui.imshow("frame2",frame2);

            capture.read(frame1);
            HighGui.imshow("orginal",frame1);

            HighGui.imshow("motion", diff);

            // tylko do HighGui
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
