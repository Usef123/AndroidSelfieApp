package com.example.homeactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageSaver extends Thread{
    private File file;
    private Context context;
    private FaceOverlay filter;
    private ImageCaptureCallback imageCaptureCallback;
    private View thumbnailView;

    ImageSaver(Context context,File file,FaceOverlay filter,ImageCaptureCallback imageCaptureCallback, View thumbnailView){
        this.context=context;
        this.file=file;
        this.filter= filter;
        this.imageCaptureCallback =imageCaptureCallback;
        this.thumbnailView = thumbnailView;
    }

    @Override
    public void run() {
        Bitmap pic;
        pic = Helper.rotateandflipBitmap(file, 90);


        double[] filterXY= filter.getScaledCentre();
        int width=(int)(pic.getHeight()*filter.getScaleXY());
        int leftframe= (pic.getWidth()/2)-(width/2);
        int rightframe = (pic.getWidth()/2)+(width/2);

        Rect frame = new Rect(leftframe,0,rightframe,pic.getHeight());

        int left= (int)(filterXY[0]*frame.width());
        int top= (int)(filterXY[1]*pic.getHeight());
        int right= (int)( filterXY[2]*frame.width());
        int bottom= (int)(filterXY[3]*pic.getHeight());


        Rect rect = new Rect(left,top,right,bottom);

        Helper helper=new Helper();

        Bitmap newPic = helper.createPic(context, pic, filter,frame,rect , file);
        final int THUMBSIZE = 500;
        Bitmap thumb= ThumbnailUtils.extractThumbnail(newPic,
                thumbnailView.getWidth()-20, thumbnailView.getHeight()-20);

        imageCaptureCallback.onImageCaptured(file,thumb,helper.getFileURL());

            /*
            detectImage.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<FirebaseVisionFace>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                                    if (firebaseVisionFaces != null && firebaseVisionFaces.size() != 0) {
                                        for (FirebaseVisionFace face : firebaseVisionFaces) {

                                            Rect rect = face.getBoundingBox();
                                            System.out.println("Rect:" + rect.width() + " " + rect.height()+" "+pic.getWidth()+" "+pic.getHeight());
                                            int scalingfact = filter.getFilter().getScalingfact();
                                            float scaleX=(float)rect.width()/pic.getWidth();
                                            float scaleY=(float)rect.height()/pic.getHeight();
                                            int width= Math.round(scaleX*scalingfact);
                                            int height= Math.round(scaleY*scalingfact);
                                            System.out.println("width:"+width+ " "+ "height:"+height+" "+ scaleX+" "+scaleY);
                                            rect.set(rect.centerX()-width/2,rect.centerX()-height/2,rect.centerX()+width/2,rect.centerY()+height/2);
                                            Bitmap newPic = Helper.createPic(context, pic, filter.getFilter().getPic(), rect, file);
                                            final int THUMBSIZE = 500;
                                            Bitmap thumb= ThumbnailUtils.extractThumbnail(newPic,
                                                    thumbnailView.getWidth()-10, thumbnailView.getHeight()-10);
                                            Helper.notifyNewFileToSystem(context,file);
                                            //Bitmap scaled = Helper.resize(newPic,thumbnailView.getWidth(),thumbnailView.getHeight());
                                            imageCaptureCallback.onImageCaptured(file,thumb);


                                        }
                                    }
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    )
            ;

             */



    }
}