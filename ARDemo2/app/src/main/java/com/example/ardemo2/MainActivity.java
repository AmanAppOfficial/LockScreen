package com.example.ardemo2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) ->
        {
            Anchor anchor = hitResult.createAnchor();
            ModelRenderable.builder()
                    .setSource(this , Uri.parse("ArcticFox_Posed.sfb"))
                    .build()
                    .thenAccept(modelRendrable -> setModelToScene(anchor , modelRendrable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return  null ;
                    });
        });

    }

    private void setModelToScene(Anchor anchor, ModelRenderable modelRendrable)
    {
         AnchorNode anchorNode = new AnchorNode(anchor);     //will get best position where ever tap.
         TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());                                                      //transformable node can be zoom in and other.
         transformableNode.setParent(anchorNode);
         transformableNode.setRenderable(modelRendrable);
         arFragment.getArSceneView().getScene().addChild(anchorNode);
         transformableNode.select();
    }
}
