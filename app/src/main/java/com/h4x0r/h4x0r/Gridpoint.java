package com.h4x0r.h4x0r;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Random;


/**
 * Created by Zachari on 10/11/2016.
 */

public class Gridpoint {

    Coordinate coord;
    boolean isStart;
    boolean isGoal;
    boolean isUsed;
    Shape shape;
    Context context;
    int img;
    ViewHolder holder = null;
    View pt = null;
    ViewGroup parent = null;
    Activity act;
    private float rotation = 0;
    private float fromRotation=0;
    private boolean tutorialPoint = false;
    private boolean isBlock =false;
    private boolean animated = false;
    private boolean blink = false;

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }


    public boolean isTutorialPoint() {
        return tutorialPoint;
    }

    public void setTutorialPoint(boolean tutorialPoint) {
        this.tutorialPoint = tutorialPoint;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setIsBlock(boolean blocker) {
        this.isBlock = blocker;
    }

    public  float getRotation() {
        return rotation;
    }

    public  void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public  float getFromRotation() {
        return fromRotation;
    }

    public  void setFromRotation(float fromRotation) {
        this.fromRotation = fromRotation;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setIsGoal(boolean isGoal) {
        this.isGoal = isGoal;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setAct(Activity act) {
        this.act = act;
    }


    Gridpoint(Context context, int img, Coordinate coord) {
        //setShape(shape);
        this.img = img;
        isStart = false;
        isGoal = false;
        isUsed = false;
        this.context = context;
        this.coord = coord;
        View pt = null;
        ViewGroup parent = null;
    }

    Gridpoint(Context context, Coordinate coord) {
        isStart = false;
        isGoal = false;
        isUsed = false;
        setTutorialPoint(false);
        this.context = context;
        img = R.drawable.schip;
        this.coord = coord;
        CreateBlank();
    }

    public void CreateBlank() {
        img = R.drawable.schip;
        if (holder != null) {
            holder.gridimage.setImageResource(R.drawable.schip);
            if (parent != null) {
                pt = getView(pt, parent);
            }
        }
    }


    public void CreateGoal() {    //Create goal point and set properties
        this.img = R.drawable.newgoal;
        this.setIsGoal(true);
        this.setIsUsed(false);
    }

    public void CreateStart() {    //Create Start point and set properties
        this.img = R.drawable.start;
        this.setIsStart(true);
        this.setIsUsed(true);
    }

    //Creates a blocking volume
    public void CreateBlock() {
        this.setIsStart(true);
        this.img = R.drawable.ram;
        setIsBlock(true);
    }


    public void CreateNode() {
        this.img = R.drawable.newnode;
        this.setIsUsed(true);
        this.setIsBlock(false);
    }

    public void CreateCache() {
        this.img = R.drawable.cache;
        this.setIsUsed(true);
    }

    public void CreateDeflector() {
        this.img = R.drawable.newpivot;
        this.setIsUsed(true);
    }

    public void CreateWall() {
        this.img = R.drawable.fan;
        this.setIsGoal(false);
        this.setIsUsed(false);
        this.setIsBlock(false);
    }


    public Context context() {
        return context;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public View getView(View convertView, ViewGroup parent) {
        this.parent = parent;
        View item = convertView;
        if (item == null || holder == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder(item);
        }
        holder.gridimage.setImageResource(this.img);
        item.setTag(coord);
        if(this.isTutorialPoint()) {
            item.setOnClickListener(TutClickListener());
            item.setOnLongClickListener(TutLongClickListener());
            item.setOnTouchListener(TutOnTouchListener());
                }
        else {
            item.setOnClickListener(ClickListener());
            item.setOnLongClickListener(LongClickListener());
            item.setOnTouchListener(OnTouchListener());
        }
        item.setOnDragListener(DragListener());
        SetupRotation(item);
        pt = item;
        return item;
    }

    private void SetupRotation(View item) {
       // if (img == R.drawable.newpivot || img == R.drawable.ram) {
        if (img == R.drawable.ram) {
                Random r = new Random();
                switch (r.nextInt(4)) {
                    case 0:
                        item.setRotation(0);
                        rotation = 0;
                        break;
                    case 1:
                        item.setRotation(90);
                        rotation = 90;
                        break;
                    case 2:
                        item.setRotation(180);
                        rotation = 180;
                        break;
                    case 3:
                        item.setRotation(270);
                        rotation = 270;
                        break;
                }
        }

    }

    private View.OnClickListener ClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.clickHandlerCell(v);
            }
        };
    }

    private View.OnLongClickListener LongClickListener() {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Game.longClickHandler(v);
                return true;
            }
        };
    }


    private View.OnTouchListener OnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Game.TouchEvent(event);
                return false;
            }
        };
    }

    private View.OnClickListener TutClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tutorial.clickHandlerCell(v);
            }
        };
    }

    private View.OnLongClickListener TutLongClickListener() {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Tutorial.longClickHandler(v);
                return true;
            }
        };
    }


    private View.OnTouchListener TutOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tutorial.TouchEvent(event);
                return false;
            }
        };
    }



    private View.OnDragListener DragListener() {
        return new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Toast.makeText(context,"I'm Being Draged!", Toast.LENGTH_LONG);
                System.out.println("Drag detected!!!!!");
                return false;
            }
        };
    }

    public void BlinkAnimate() {
        Animation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(300); // duration
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        holder.gridimage.startAnimation(blinkanimation);
     }
    public void ClearAnim(){
        holder.gridimage.clearAnimation();
        blink=false;
    }

    public void Animate() {
        this.coord.isConnected = true;
        this.setAnimated(true);
        if(isTutorialPoint()) {
            Tutorial.currentCord = this.coord;
        }
        else {
            Game.currentCord = this.coord;
        }
            AnimationDrawable lightningAnim2;
        ImageView currImage2 = holder.gridimage;
        currImage2.setBackgroundResource(img);
        currImage2.setImageResource(R.drawable.lightning);
        lightningAnim2 = (AnimationDrawable) currImage2.getDrawable();
        lightningAnim2.start();



    }

    public void RotatePoint() {
        fromRotation=rotation;
        rotation = rotation + 90;
        if(fromRotation==270){
            rotation=0;}
        RotateAnimation rotate = new RotateAnimation(fromRotation, rotation,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setFillAfter(true);
        pt.startAnimation(rotate);
   }

    class ViewHolder {
        ImageView gridimage;
        ViewHolder(View v) {
            gridimage = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}

