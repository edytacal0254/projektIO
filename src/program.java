import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class program {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


    public static ArrayList<double[]> colorRGB=new ArrayList<>();
    public static ArrayList<String> colorNames=new ArrayList<>();
    public static void fillColorArrays(){
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

        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                double[] rgb = img.get(i,j);
                red +=((int) rgb[0])/pixels;
                green += ((int) rgb[1])/pixels;
                blue += ((int) rgb[2])/pixels;
            }
        }

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

    public static void processVideo(String path) throws InterruptedException {
        System.loadLibrary("opencv_videoio_ffmpeg450_64");
        BackgroundSubtractor backSub = Video.createBackgroundSubtractorKNN();
        //na podstawie przykładowego filmiku
        //trudno stwierdzić,który lepiej sobie radzi ten czy MOG2

        VideoCapture capture = new VideoCapture(path);
        if (!capture.isOpened()) {
            System.err.println("Unable to open: " + path);

            //do testów
            System.exit(0);
        }


        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        HOGDescriptor hog = new HOGDescriptor(new Size(48,96), new Size(16,16), new Size(8,8), new Size(8,8), 9);
        hog.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());

        capture.read(frame2);
        frame1=frame2.clone();
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




            Rect motionRect;
            ArrayList<Rect> motionRectArray = new ArrayList<>();
            //double maxArea = 5000; //100*50
            //int maxAreaIdx = -1;
            for (int idx = 0; idx < contours.size(); idx++) {
                //Mat contour = contours.get(idx);
                //double contourarea = Imgproc.contourArea(contour);
                //if (contourarea >= maxArea) {
                //maxAreaIdx = idx;
                motionRect = Imgproc.boundingRect(contours.get(idx));
                    if (motionRect.width>=50 && motionRect.height>=100) {
                        motionRectArray.add(motionRect);
                    }
                //}
            }


            MatOfRect found = new MatOfRect();
            MatOfDouble foundWeights = new MatOfDouble();

            if (motionRectArray.size() > 0) {
                Iterator<Rect> it2 = motionRectArray.iterator();
                while (it2.hasNext()) {
                    Rect tempRect1 = it2.next();

                    //do testów
                    Imgproc.rectangle(frame2, tempRect1.br(), tempRect1.tl(), new Scalar(0, 255, 0), 1);

                    Mat temp1 = new Mat(frame2,tempRect1);

                    //do testów
                    HighGui.imshow("rero",temp1);

                    //Todo dopasować parametry [przy domyślych jest nadgorliwy]
                    hog.detectMultiScale(temp1,found,foundWeights);
                    Rect[] humans = found.toArray();

                    for (int i=0;i<humans.length;i++){
                        //chyba już nie trzeba sprawdzać wymiarów  (daimler)
                        //if(humans[i].height>=100 && humans[i].width>=50) {
                        Rect rectForOrginalImage = new Rect(tempRect1.x + humans[i].x, tempRect1.y + humans[i].y, humans[i].width, humans[i].height);
                            Imgproc.rectangle(frame2, rectForOrginalImage, new Scalar(255, 0, 0), 1);

                        Mat forColor = new Mat(frame1,rectForOrginalImage);
                        String color = getMeanRGBColor(forColor.clone());

                        //do testów, trzeba poprawić parametry, zaznacza za duży obszar
                        //if (!color.equals("grey")){
                        //  System.out.println(color);
                        //}
                    }
                }

            }


            //* do testów
            HighGui.imshow("frame2",frame2);
            //capture.read(frame1);
            HighGui.imshow("original",frame1);
            HighGui.imshow("motion", diff);

            ////do HighGui
            int keyboard = HighGui.waitKey(30);
            if (keyboard == 'q' || keyboard == 27) {
                break;
            }
            Thread.sleep(1000);
            //*
        }
        //do testów
        HighGui.waitKey();
        System.exit(0);
    }
    public static void main(String[] args) throws InterruptedException {
        String input = "C:\\Users\\edyta\\IdeaProjects\\projektIO\\src\\grupaB1.mp4";
        //String input = "grupaB1.mp4";
        //processVideo("grupaB1.mp4");
        fillColorArrays();
        processVideo(input);
        //nie chce wczytać pliku ze ścieżki względnej <?>
    }

}
