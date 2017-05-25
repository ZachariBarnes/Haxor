package com.h4x0r.h4x0r;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.*;


public class Game extends AppCompatActivity implements View.OnClickListener {

    private int startX = 0;
    private int startY = 0;
    public Coordinate goal;
    private static int sizeX = 9;
    private static int sizeY = 9;
    private static TableLayout grid;
    private static Gridpoint gridpoint[][];
    private Context context;
    private int currID;
    private static TextView victory;
    private int difficulty;
    private static int breakers; // Used to break blockers on the map
    static MediaPlayer bgs = null;
    private static MediaPlayer errfx = null;
    private static MediaPlayer pivotfx = null;
    private static MediaPlayer breakfx = null;
    private int desiredPathSize;
    private static TextView gameText;
    private static TextView scoreQtyText;
    private static TextView prompt;
    private static int score;
    private int volume1;
    private int volume0;
    private int bgsfile;
    static int numMoves = 0;
    public static Coordinate currentCord = null;
    private static RelativeLayout btnLayout;
    private static Boolean hasMoved = null;
    private static boolean tapEnabled;
    private static boolean swipe = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout); //Sets the Layout for the Game screen
        context = this; //Saves the context of the App for use in methods later
        gameText = (TextView) findViewById(R.id.gameText); //Points the gameText to the specified text field in the layout
        scoreQtyText = (TextView) findViewById(R.id.scoreQtytxt); //Points the Score Text to the specified text field in the layout
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1337898371561744~9336184112"); // Initializes the ad with the application ID
        AdView mAdView = (AdView) findViewById(R.id.adView);//Finds the Adview in the layout
        AdRequest adRequest = new AdRequest.Builder() // builds the Add
                .build();
        //mAdView.loadAd(adRequest); // Loads the Add
        Appodeal.disableNetwork(this, "cheetah");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Locks portrait mode
        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        StartGame(); //Starts the Game
    }

    @Override
    public void onPause() {
        super.onPause();
        bgs.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        bgs.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (gridpoint == null) {
            StartGame();
        }
    }

    @Override // Empties the game fiedld if the app is closed or the back button is pressed.
    public void onStop() {
        super.onStop();
        grid.removeAllViewsInLayout();
        bgs.setLooping(false);
        bgs.stop();
        startX = 0;
        startY = 0;
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                gridpoint[x][y] = null;
            }
        }

    }


    public void StartGame() {
        SetDifficulty(); //Calls a method to set the difficulty based on the prefrences set from the options screen on the main menu

        //Locates all needed Views from the layout and sets some initial values
        prompt = new TextView(this);
        victory = (TextView) findViewById(R.id.victoryTxt);
        victory.setVisibility(View.INVISIBLE);
        gameText.setTextSize(20);
        gameText.setTextColor(Color.CYAN);
        btnLayout = (RelativeLayout) findViewById(R.id.BtnLayout);
        btnLayout.setVisibility(View.INVISIBLE);
        score = 0;
        grid = (TableLayout) findViewById(R.id.gridTable);
        assert grid != null;
        grid.setGravity(View.TEXT_ALIGNMENT_CENTER);


        gridpoint = new Gridpoint[sizeX + 1][sizeY + 1]; // Sets the size of the game board based on difficulty
        gridpoint = InitializeGrid(gridpoint); //set all grid points to Blank
        CalculateGrid(gridpoint);//Calculate all grid locations
        currID = 0; //Created so every Item in teh grid has a unique ID

       //This Loop takes all of the grid Items and 'paints' them to the screen by creating a view and assigning an ID
        for (int x = 0; x <= sizeX; x++) {
            TableRow newRow = new TableRow(this);
            for (int y = 0; y <= sizeY; y++) {
                View currView = gridpoint[x][y].getView(new View(this), newRow);
                newRow.addView(currView, y);
                if (x == startX && y == startY) {
                    gridpoint[x][y].Animate();
                }
                newRow.getChildAt(y).setClickable(true);
                newRow.getChildAt(y).setId(currID);
                currID++;

            }
            newRow.setClickable(true);
            grid.addView(newRow, x);
        }
        SetupMedia(); //Sets up Sound Effects and music for the game Based on Volume and sound settings in the options screen

    }

    private void SetupMedia() { //Based on prefrences Volume and sounds files are set
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int tmpvol;
        if(SP.getString("music", "000").equals("000 (file1.mp3)")){
            bgsfile = 000;
        }
        else if(SP.getString("music", "000").equals("010 (file2.mp3)")){
            bgsfile = 010;
        }
        else if(SP.getString("music", "000").equals("101 (file3.mp3))")){
            bgsfile = 101;
        }
        else{
            bgsfile = Integer.parseInt(SP.getString("music", "000"));
        }

        if(SP.getString("volume", "100").equals("100%")){
            tmpvol=100;
        }
        else if(SP.getString("volume", "100").equals("75%")){
            tmpvol=75;
        }
        else if(SP.getString("volume", "100").equals("50%")){
            tmpvol=50;
        }
        else if(SP.getString("volume", "100").equals("25%")){
            tmpvol=25;
        }
        else if(SP.getString("volume", "100").equals("0% (muted)")){
            tmpvol=0;
        }
        else if(SP.getString("volume", "100").equals("Mute Music Only")){
            tmpvol=-5;
        }
        else if(SP.getString("volume", "100").equals("Mute Sound Effects Only")){
            tmpvol=-10;
        }
        else{
            tmpvol = Integer.parseInt(SP.getString("volume", "100"));
        }
        int maxVolume = 101;
        //Set Music File
        System.out.println("BGS File: " + bgsfile);
        switch (bgsfile) {
            case 000:
                bgsfile = R.raw.endless_pain;
                break;
            case 10:
                bgsfile = R.raw.music1;
                break;
            case 101:
                bgsfile = R.raw.music2;
                break;
            default:
                bgsfile = R.raw.endless_pain;
                break;
        }
        //Set Volume Levels
        switch (tmpvol) {
            case 100:
                volume0 = 100;
                volume1 = 100;
                break;
            case 75:
                volume0 = 75;
                volume1 = 75;
                break;
            case 50:
                volume0 = 50;
                volume1 = 50;
                break;
            case 25:
                volume0 = 25;
                volume1 = 25;
                break;
            case 0:
                volume0 = 0;
                volume1 = 0;
                break;
            case -5:
                volume0 = 0;
                volume1 = 100;
                break;
            case -10:
                volume0 = 100;
                volume1 = 0;
                break;
            default:
                volume0 = 100;
                volume1 = 100;
                break;
        }
        pivotfx = MediaPlayer.create(this, R.raw.pivot_turn);
        breakfx = MediaPlayer.create(this, R.raw.breaker);
        errfx = MediaPlayer.create(this, R.raw.error);
        if (volume1 != 0) { //sets Sound Effect volume
            float log1 = (float) (Math.log(maxVolume - volume1) / Math.log(maxVolume));
            errfx.setVolume(1 - log1, 1 - log1);
            breakfx.setVolume(1 - log1, 1 - log1);
            pivotfx.setVolume(1 - log1, 1 - log1);
        }
        bgs = MediaPlayer.create(this, bgsfile); // Sets Bg music volume
        float log0 = (float) (Math.log(maxVolume - volume0) / Math.log(maxVolume));
        bgs.setVolume(1 - log0, 1 - log0);
        bgs.setLooping(true);
        bgs.start();

        // bgs.prepareAsync();
    }

    private void SetDifficulty() { // Sets Difficulty and grid size based on prefrences
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        difficulty = Integer.parseInt(SP.getString("difficulty", "10"));
        System.out.println("difficulty!: " + difficulty);
        sizeY = 8;
        switch (difficulty) {
            case 15:
                numMoves = 7;
                sizeX = 12;
                breakers = 6;
                gameText.setText("Breakers: " + breakers + " Moves: " + numMoves);
                desiredPathSize = 20;
                tapEnabled=false;
                break;
            case 10:
                numMoves = 9;
                sizeX = 10;
                breakers = 8;
                gameText.setText("Breakers: " + breakers + " Moves: " + numMoves);
                desiredPathSize = 40;
                tapEnabled=false;
                break;
            case 5:
                numMoves = 10;
                sizeX = 5;
                breakers = 10;
                gameText.setText("Breakers: " + breakers + " Moves: " + numMoves);
                desiredPathSize = 50;
                tapEnabled=true;
                break;
            default:
                numMoves = 10;
                sizeX = 8;
                breakers = 10;
                gameText.setText("Breakers: " + breakers + " Moves: " + numMoves);
                desiredPathSize = 30;
                tapEnabled=true;
                break;
        }

    }

    public static void longClickHandler(View view) { // Handles Click and Hold Events
        if (view != null) {
            if (view.getTag() != null) {
                Coordinate current = (Coordinate) view.getTag(); //Gets the target point
                Gridpoint point = gridpoint[current.x][current.y];
                changeGridPoint(point, current);//Modifies the Grid point based on what kind of point it is
            }
        }
    }

    public static void clickHandlerCell(View view) { // Handles single click update
        if (view != null) {
            if (view.getTag() != null) {
                Coordinate current = (Coordinate) view.getTag(); //gets Target point
                Gridpoint point = gridpoint[current.x][current.y];
                CalculateMove(point, current);//Calculates actions Based on the type of point and its location
            }
        }
    }


    private static void changeGridPoint(Gridpoint point, Coordinate current) {
        if (point.img == R.drawable.ram) { // If the point is a blocker
            if (breakers > 0) { // And Breakers are remainging
                gridpoint[current.x][current.y].CreateNode(); //Break the blocker and replace it with a node
                breakers--;//Decrement Breakers
                gameText.setText("Breakers: " + breakers + " Moves: " + numMoves); //Update Text
                score += 100; // Gain points
                scoreQtyText.setText(Integer.toString(score)); // Update score
                gridpoint[current.x][current.y].getView(gridpoint[current.x][current.y].holder.gridimage, gridpoint[current.x][current.y].parent); //Update View
                breakfx.start();// Play Sound Effect
                if(lossDetected()){ // Check for valid moves from ths location
                    Loss();//If no more moves are available, you Lose
                }
            } else { //If point is a blocker and out of breakers
                errfx.start(); //Play Error sound effect
            }
        }
        else if (point.img == R.drawable.newpivot) {// If point is a pivot
            if (point.getCoord().isConnected == null || (!point.coord.isConnected)) {
                System.out.println("pivot Cord: " + point.getCoord().isConnected);
                gridpoint[current.x][current.y].RotatePoint(); // Rotate the Pivot 90 Degress
                pivotfx.start(); // Play sound Effect
            } else { //If point is a pivot that is already active
                errfx.start(); //Play Error sound effect
            }
        }
        else { //If point is a pivot that is already active
            errfx.start(); //Play Error sound effect
        }
    }

//Detects if a Pivot is connected to another point
    private static boolean pivotConnected(Gridpoint pivot, Gridpoint node) {
        int pivX = pivot.getCoord().x; // Saves the Coordinates
        int pivY = pivot.getCoord().y;
        int rot = 5; // Rot 0 = the node is above the pivot, 1 to the Right, 2 below, 3 left

        // comapres the Location of the Node(or Connected point) to the pivot
        Coordinate nodeCord = node.getCoord();
        if (nodeCord.y == pivY) {
            if (nodeCord.x == pivX - 1) {
                rot = 0;
            } else if (nodeCord.x == pivX + 1) {
                rot = 2;
            }
        } else if (nodeCord.x == pivX) {
            if (nodeCord.y == pivY + 1) {
                rot = 1;
            } else if (nodeCord.y == pivY - 1) {
                rot = 3;
            }
        }
        float rotDegree = pivot.getRotation(); // Checks to see if the Pivot is facing in the Direction of the Node
        switch (rot) {
            case 0:
                if (rotDegree == 0f || rotDegree == 360f || rotDegree == 270f)
                    return true;
                break;
            case 1:
                if (rotDegree == 0f || rotDegree == 90f || rotDegree == 360f)
                    return true;
                break;
            case 2:
                if (rotDegree == 180f || rotDegree == 90f)
                    return true;
                break;
            case 3:
                if (rotDegree == 180f || rotDegree == 270f)
                    return true;
                break;
            default:
                break;
        }
        return false;
    }

    //This Method Determines what to do with a point when it is clicked/activated
    private static void CalculateMove(Gridpoint point, Coordinate current) {
        if(tapEnabled || swipe) {
            if (point.img == R.drawable.newnode) { // If the point actiavted is a node
                if (ValidMove(point) && point.coord.isConnected == null) { // If the point is not already connected
                    ArrayList<Gridpoint> connectedPoints = getConnectedPoints(point); // Get all points connected to this node
                    boolean pivotFound = false; // Initialize
                    for (Gridpoint currPt : connectedPoints) { // For each connected point
                        if (currPt.img == R.drawable.newpivot) { // Check if the point is a pivot
                            pivotFound = true; // pivot is found
                            if (pivotConnected(currPt, point)) { //If the pivot is connected to the node (rotated correctly)
                                gridpoint[current.x][current.y].Animate();// Animate (connect) the node and update Current location
                                score += 50; // Add points
                                scoreQtyText.setText(Integer.toString(score)); // Update score
                                if (lossDetected()) { // Check for valid moves from ths location
                                    Loss();//If no more moves are available, you Lose
                                }
                            }
                        }
                    }
                    if (!pivotFound) { // If no pivot found
                        gridpoint[current.x][current.y].Animate(); // Connect this point normally
                        score += 50; //Add points
                        scoreQtyText.setText(Integer.toString(score)); //update text
                        if (lossDetected()) {// Check for valid moves from ths location
                            Loss();//If no more moves are available, you Lose
                        }
                    }
                }
            }
        }
        if (point.img == R.drawable.ram) {
            if (!ValidMove(point)) {
                changeGridPoint(point, current);
            }
            else if(ValidMove(point) && !swipe){
                changeGridPoint(point, current);
            }

        }
        if(tapEnabled || swipe) {
            if (point.img == R.drawable.newgoal) { //If point is a goal
                if (ValidMove(point) && point.coord.isConnected == null) { //If valid move
                    ArrayList<Gridpoint> connectedPoints = getConnectedPoints(point); //Pivot Processing See processing for node above
                    boolean pivotFound = false;
                    for (Gridpoint currPt : connectedPoints) {
                        if (currPt.img == R.drawable.newpivot) {
                            pivotFound = true;
                            if (pivotConnected(currPt, point)) {
                                gridpoint[current.x][current.y].Animate();
                                score += 1500; // Add points
                                scoreQtyText.setText(Integer.toString(score));
                                Victory(); // You win!
                            }
                        }
                    }
                    if (!pivotFound) {
                        gridpoint[current.x][current.y].Animate();
                        score += 1000;
                        scoreQtyText.setText(Integer.toString(score));
                        Victory();
                    }
                }
            }
        }
        if (point.img == R.drawable.newpivot) { // If node is a pivot
            if (ValidMove(point)) {
                if(tapEnabled || swipe) {// pivot is a vlaid move
                    ArrayList<Gridpoint> connectedPoints = getConnectedPoints(point);
                    for (Gridpoint currPt : connectedPoints) {
                        if (currPt.img == R.drawable.newnode || currPt.img == R.drawable.newgoal || currPt.img == R.drawable.start || currPt.img == R.drawable.cache) { //check each connected point
                            if (pivotConnected(point, currPt)) {
                                gridpoint[current.x][current.y].Animate(); // activate pivot
                                score += 75;
                                scoreQtyText.setText(Integer.toString(score));
                                if (lossDetected()) {// Check for valid moves from ths location
                                    Loss();//If no more moves are available, you Lose
                                }
                            }
                        } else if (currPt.img == R.drawable.newpivot) { // Allows connecting multiple pivots
                            if (pivotConnected(point, currPt) && pivotConnected(currPt, point)) {
                                gridpoint[current.x][current.y].Animate();
                                score += 175;
                                scoreQtyText.setText(Integer.toString(score));
                                if (lossDetected()) {// Check for valid moves from ths location
                                    Loss();//If no more moves are available, you Lose
                                }
                            }
                        }
                    }
                }
            }
            else if (!ValidMove(point)){
                changeGridPoint(point, current);
            }
            if (ValidMove(point) && !pivotConnected(point,gridpoint[current.x][current.y]) && !swipe){
                if(!gridpoint[current.x][current.y].isAnimated()) {
                    changeGridPoint(point, current);
                }
            }

        }
        if(tapEnabled || swipe) {
            if (point.img == R.drawable.cache) { // If the point actiavted is a node
                if (ValidMove(point) && point.coord.isConnected == null) { // If the point is not already connected
                    ArrayList<Gridpoint> connectedPoints = getConnectedPoints(point); // Get all points connected to this node
                    boolean pivotFound = false; // Initialize
                    for (Gridpoint currPt : connectedPoints) { // For each connected point
                        if (currPt.img == R.drawable.newpivot) { // Check if the point is a pivot
                            pivotFound = true; // pivot is found
                            if (pivotConnected(currPt, point)) { //If the pivot is connected to the node (rotated correctly)
                                gridpoint[current.x][current.y].Animate();// Animate (connect) the node and update Current location
                                score += 50; // Add points
                                scoreQtyText.setText(Integer.toString(score)); // Update score
                                if (lossDetected()) { // Check for valid moves from ths location
                                    Loss();//If no more moves are available, you Lose
                                }
                            }
                        }
                    }
                    if (!pivotFound) { // If no pivot found
                        gridpoint[current.x][current.y].Animate(); // Connect this point normally
                        score += 1337; //Add points
                        scoreQtyText.setText(Integer.toString(score)); //update text
                        if (lossDetected()) {// Check for valid moves from ths location
                            Loss();//If no more moves are available, you Lose
                        }
                    }
                }
            }
        }
    }

    //Returns an ArrayList of all points connected to a given gridpoint
    private static ArrayList<Gridpoint> getConnectedPoints(Gridpoint point) {
        ArrayList<Gridpoint> validPoints = new ArrayList<Gridpoint>();
        Gridpoint tmpPt = null;
        if (isBelowConnected(point)) {
            tmpPt = null;
            if (ValidPoint(point.getCoord().x + 1, sizeX) && gridpoint[point.getCoord().x + 1][point.getCoord().y] != null) {
                tmpPt = gridpoint[point.getCoord().x + 1][point.getCoord().y];
                validPoints.add(tmpPt);
            }
        }
        if (isAboveConnected(point)) {
            tmpPt = null;
            if (ValidPoint(point.getCoord().x - 1, sizeX) && gridpoint[point.getCoord().x - 1][point.getCoord().y] != null) {
                tmpPt = gridpoint[point.getCoord().x - 1][point.getCoord().y];
                validPoints.add(tmpPt);
            }
        }
        if (isLeftConnected(point)) {
            tmpPt = null;
            if (ValidPoint(point.getCoord().y - 1, sizeY) && gridpoint[point.getCoord().x][point.getCoord().y - 1] != null) {
                tmpPt = gridpoint[point.getCoord().x][point.getCoord().y - 1];
                validPoints.add(tmpPt);
            }
        }
        if (isRightConnected(point)) {
            tmpPt = null;
            if (ValidPoint(point.getCoord().y + 1, sizeY) && gridpoint[point.getCoord().x][point.getCoord().y + 1] != null) {
                tmpPt = gridpoint[point.getCoord().x][point.getCoord().y + 1];
                validPoints.add(tmpPt);
            }
        }
        return validPoints;
    }

    private static void Victory() {
        bgs.stop();
        grid.removeAllViews();
        victory.setGravity(View.TEXT_ALIGNMENT_CENTER);
        victory.setText(R.string.Victory);
        victory.setTextColor(Color.WHITE);
        btnLayout.setVisibility(View.VISIBLE);
        victory.setVisibility(View.VISIBLE);

    }

    private static void Loss() {
        bgs.stop();
        grid.removeAllViews();
        victory.setText(R.string.Loss);
        victory.setGravity(View.TEXT_ALIGNMENT_CENTER);
        victory.setTextColor(Color.WHITE);
        btnLayout.setVisibility(View.VISIBLE);
        victory.setVisibility(View.VISIBLE);
    }

    private static boolean ValidMove(Gridpoint pt) {
        if (isBelowConnected(pt)) {
            return true;
        }
        if (isAboveConnected(pt)) {
            return true;
        }
        if (isLeftConnected(pt)) {
            return true;
        }
        if (isRightConnected(pt)) {
            return true;
        }
        return false;
    }

    //Creates the literal grid (Containing points goals and blockers)
    private Gridpoint[][] InitializeGrid(Gridpoint[][] gridpoint) {
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                gridpoint[x][y] = new Gridpoint(context, new Coordinate(x, y));
                gridpoint[x][y].setAct(this);
            }
        }
        return gridpoint;
    }

    //Fills the Grid with points
    Gridpoint[][] CalculateGrid(Gridpoint gridpoint[][]) {
        //Initialize and randomize Variables
        Random r = new Random();
        int currX = 0;
        int currY = 0;
        startX = 0;
        startY = 0;
        currX = (startX + (r.nextInt(3)));
        currY = (startY + (r.nextInt(3)));
        int goalX = (sizeX - (r.nextInt(3)));
        int goalY = (sizeY - (r.nextInt(3)));
        goal = new Coordinate(goalX, goalY);
        Coordinate start = new Coordinate(currX, currY);
        gridpoint[currX][currY].CreateStart();
        gridpoint[goalX][goalY].CreateGoal();
        startX = currX;
        startY = currY;
        boolean difficultEnough = false;

        //Recalculate Victory Path until it is short enough to meet difficulty guidelines
        while (!difficultEnough) {
            ArrayList<Coordinate> path = new ArrayList<Coordinate>();
            if (CreatePath(gridpoint[currX][currY], start, path, gridpoint)) { // Create victory Path
            }
            if (path.size() <= desiredPathSize) {
                difficultEnough = true;
            }
        }
        FillGrid(gridpoint); // Fill the rest of the grid
        return gridpoint;
    }

    private void reRoll(Gridpoint point){
        Random r = new Random();
        switch (r.nextInt(6)) {
            case 0:
                point.CreateBlock();
                break;
            case 1:
                point.CreateDeflector();
                break;
            case 2:
                point.CreateWall();
                break;
            case 3:
                point.CreateNode();
                break;
            case 4:
                reRoll(point);
                break;
            case 5:
                point.CreateCache();
                break;
        }
    }

    //Fills the blank spots in the grid with walls and blockers
    void FillGrid(Gridpoint[][] grid) {
        int tempB = breakers + (difficulty * 2);
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                if (!grid[x][y].isGoal && !grid[x][y].isUsed) {
                    Random r = new Random();
                    switch (r.nextInt(6)) { // Increment to 6 when Datacache is added.
                        case 0:
                            grid[x][y].CreateBlock();
                            break;
                        case 1:
                            grid[x][y].CreateDeflector();
                            break;
                        case 2:
                            grid[x][y].CreateWall();
                            break;
                        case 3:
                            grid[x][y].CreateNode();
                            break;
                        case 4:
                            grid[x][y].CreateBlock();
                            break;
                        case 5: // Reroll for Data cache Object
                            reRoll(grid[x][y]);
                            break;
                    }
                } else if (grid[x][y].isUsed() && !grid[x][y].isStart()) { // Overwrites parts of the victory path
                    Random r = new Random();
                    int add = difficulty / 5;
                    switch ((r.nextInt(5)) + add) {
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            if (isPivotConnected(grid[x][y])) {
                                grid[x][y].CreateDeflector();
                            } else if (tempB > 0) {
                                grid[x][y].CreateBlock();
                                tempB--;
                            }
                            break;
                        case 4:
                            if (isPivotConnected(grid[x][y])) {
                                grid[x][y].CreateDeflector();
                            } else if (tempB > 0) {
                                grid[x][y].CreateBlock();
                                tempB--;
                            }
                            break;
                        case 5:
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return;
    }

    //Uses Recursion - ensures a victory path exists
    boolean CreatePath(Gridpoint cPt, Coordinate cPtLoc, ArrayList<Coordinate> path, Gridpoint[][] grid) {//recursive method
        if (cPt.isGoal) {
            return true;
        }
        path.add(cPtLoc);
        ArrayList<Coordinate> vPs = Randomize(CheckPoints(cPtLoc, path, grid)); //Create an array of Valid movements from this point
        if (vPs != null) {
            for (Coordinate nxtPt : vPs) { //Randomly picks a point to connect next
                if (null == nxtPt.isConnected) {
                    nxtPt.isConnected = CreatePath(grid[nxtPt.x][nxtPt.y], nxtPt, path, grid);//If this point is Valid, Find the next point (Recursion)
                }
                if (nxtPt.isConnected) {
                    cPtLoc.isConnected = true; //If the next point is connected to this point, Add it to the Victory path and create a node
                    if (!cPt.isStart) {
                        cPt.CreateNode();
                    }
                    return true;
                }
            }
            for (int i = 0; i < vPs.size(); i++) {
                if (!vPs.get(i).isConnected) {
                    vPs.remove(i); // If a dead end is reached Remove this point and go back to pick another point
                }
            }
        }
        if (vPs.isEmpty()) {
            cPtLoc.isConnected = false; // If there are no valid moves from this point, Return False ( to make the previous interation pick a different point instead.
            path.remove(cPtLoc);
            return false;
        }

        return false;
    }

    //Returns an Array of valid points to be used when determining victory path exists
    ArrayList<Coordinate> CheckPoints(Coordinate cPtLoc, ArrayList<Coordinate> path, Gridpoint[][] grid) {
        ArrayList<Coordinate> validPoints = new ArrayList<Coordinate>();
        Coordinate[] point = new Coordinate[4];
        int i = 0;
        if (ValidPoint(cPtLoc.x + 1, sizeX) && ValidPoint(cPtLoc.y, sizeY)) {
            point[i] = new Coordinate(cPtLoc.x + 1, cPtLoc.y);
            int w = point[i].x;
            int z = point[i].y;
            point[i] = ValidateLocation(grid[w][z], w, z, path);
            if (point[i].isValid) {
                if (grid[w][z].isGoal) {
                    point[i].isConnected = true;
                }
                validPoints.add(point[i]);
            }
        }
        i++;
        if (ValidPoint(cPtLoc.x - 1, sizeX) && ValidPoint(cPtLoc.y, sizeY)) {
            point[i] = new Coordinate(cPtLoc.x - 1, cPtLoc.y);
            int w = point[i].x;
            int z = point[i].y;
            point[i] = ValidateLocation(grid[w][z], w, z, path);
            if (point[i].isValid) {
                if (grid[w][z].isGoal) {
                    point[i].isConnected = true;
                }
                validPoints.add(point[i]);
            }
        }
        i++;
        if (ValidPoint(cPtLoc.x, sizeX) && ValidPoint(cPtLoc.y + 1, sizeY)) {
            point[i] = new Coordinate(cPtLoc.x, cPtLoc.y + 1);
            int w = point[i].x;
            int z = point[i].y;
            point[i] = ValidateLocation(grid[w][z], w, z, path);
            if (point[i].isValid) {
                if (grid[w][z].isGoal) {
                    point[i].isConnected = true;
                }
                validPoints.add(point[i]);
            }
        }
        i++;
        if (ValidPoint(cPtLoc.x, sizeX) && ValidPoint(cPtLoc.y - 1, sizeY)) {
            point[i] = new Coordinate(cPtLoc.x, cPtLoc.y - 1);
            int w = point[i].x;
            int z = point[i].y;
            point[i] = ValidateLocation(grid[w][z], w, z, path);
            if (point[i].isValid) {
                if (grid[w][z].isGoal) {
                    point[i].isConnected = true;
                }
                validPoints.add(point[i]);
            }
        }
        i++;
        for (Coordinate pt : validPoints) {
            if (pt.isConnected != null) {
                if (pt.isConnected) {
                    ArrayList<Coordinate> connected = new ArrayList<Coordinate>();
                    connected.add(pt);
                    return connected;
                }
            }
        }
        return validPoints;
    }

    //Validates all 4 adjacent locations and returns only locations that exist and are valid
    private Coordinate ValidateLocation(Gridpoint gridPoint, int x, int y, ArrayList<Coordinate> path) {
        Coordinate thispoint = new Coordinate(x, y);
        if (!gridPoint.isUsed) {
            thispoint.isValid = true;
        } else {
            thispoint.isValid = false;
        }
        for (Coordinate pt : path) {
            if (thispoint.x == pt.x && thispoint.y == pt.y) {
                thispoint.isValid = false;
            }
        }

        return thispoint;
    }

    //Validates the one specific location is within the grid bounds
    private static boolean ValidPoint(int i, int size) {

        if (i >= 0 && i <= size) {
            return true;
        } else {
            return false;
        }
    }

    //Randomizes a list of Coordinates
    private ArrayList<Coordinate> Randomize(ArrayList<Coordinate> checkPoints) {
        ArrayList<Coordinate> randomList = new ArrayList<Coordinate>();
        Random rand = new Random();
        for (int i = 0; i < checkPoints.size(); i++) {
            int r = rand.nextInt(checkPoints.size());
            randomList.add(checkPoints.get(r));
            checkPoints.remove(r);
        }
        return randomList;
    }

    @Override
    public void onClick(View v) {
        swipe=false;
        clickHandlerCell(v);
    }

    //Checks the Gridpoint Above to see if it is connected
    static boolean isBelowConnected(Gridpoint point) {
        Coordinate pos = point.getCoord();
        Gridpoint tmpPt = null;
            if (ValidPoint(pos.x + 1, sizeX) && gridpoint[pos.x + 1][pos.y] != null) {
                tmpPt = gridpoint[pos.x + 1][pos.y];
                // If within Range and is connected to the Start/Path Then is valid move = true
                if (tmpPt.getCoord().isConnected != null && tmpPt.getCoord().isConnected) {
                    //System.out.println("Below Connect True X: " + Integer.toString(pos.x + 1) + " Y: " + Integer.toString(pos.y));
                    if (tmpPt.coord.equals(currentCord)) {
                        return true;
                    }
                }
            }
        return false;
    }

    //Checks the Gridpoint Below to see if it is connected
    static boolean isAboveConnected(Gridpoint point) {
        Coordinate pos = point.getCoord();
        Gridpoint tmpPt = null;
            if (ValidPoint(pos.x - 1, sizeX) && gridpoint[pos.x - 1][pos.y] != null) {
                tmpPt = gridpoint[pos.x - 1][pos.y];
                // If within Range and is connected to the Start/Path Then is valid move = true
                if (tmpPt.getCoord().isConnected != null && tmpPt.getCoord().isConnected) {
                    //System.out.println("Above Connect True X: " + Integer.toString(pos.x - 1) + " Y: " + Integer.toString(pos.y));
                    if (tmpPt.coord.equals(currentCord)) {
                        return true;
                    }
                }
            }
        return false;
    }

    //Checks the Gridpoint to the Left to see if it is connected
    static boolean isRightConnected(Gridpoint point) {
        Coordinate pos = point.getCoord();
        Gridpoint tmpPt = null;
            //Reset Temp Pont, Try next location
            if (ValidPoint(pos.y + 1, sizeY) && gridpoint[pos.x][pos.y + 1] != null) {
                tmpPt = gridpoint[pos.x][pos.y + 1];
                if (tmpPt != null) {
                    // If within Range and is connected to the Start/Path Then is valid move = true
                    if (tmpPt.getCoord().isConnected != null && tmpPt.getCoord().isConnected) {
                        //System.out.println("Right Connect True X: " + Integer.toString(pos.x) + " Y: " + Integer.toString(pos.y + 1));
                        if (tmpPt.coord.equals(currentCord)) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    //Checks the Gridpoint to the Right to see if it is connected
    static boolean isLeftConnected(Gridpoint point) {
        Coordinate pos = point.getCoord();
        Gridpoint tmpPt = null;
            //Reset Temp Pont, Try next location
            if (ValidPoint(pos.y - 1, sizeY) && gridpoint[pos.x][pos.y - 1] != null) {
                tmpPt = gridpoint[pos.x][pos.y - 1];
                if (tmpPt != null) {
                    // If within Range and is connected to the Start/Path Then is valid move = true
                    if (tmpPt.getCoord().isConnected != null && tmpPt.getCoord().isConnected) {
                        // System.out.println("Left Connect True X: " + Integer.toString(pos.x) + " Y: " + Integer.toString(pos.y - 1));
                        if (tmpPt.coord.equals(currentCord)) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    //Checks if a pivot is connected to the current location
    private static boolean isPivotConnected(Gridpoint point) {
        if (isAboveConnected(point) && isRightConnected(point)) {
            return true;
        }
        if (isRightConnected(point) && isBelowConnected(point)) {
            return true;
        }
        if (isBelowConnected(point) && isLeftConnected(point)) {
            return true;
        }
        if (isLeftConnected(point) && isAboveConnected(point)) {
            return true;
        }
        return false;
    }

    //Called if the Restart(New Level) button is pressed.
    //Restarts the game(generates a new level)
    public void Restart(View v) {
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                gridpoint[x][y].CreateBlank();
            }
        }
        //bgs.stop();
        grid.removeAllViewsInLayout();
        StartGame();
    }

    // Called Touch Event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TouchEvent(event);
        return super.onTouchEvent(event);

    }

    //Event that handles all Swipes
    public static void TouchEvent(MotionEvent event) {
        if (numMoves > 0) {
            float x1 = 0f, x2 = 0f, y1 = 0f, y2 = 0f, deltaX = 0f, deltaY = 0f;
            int SWIPE_THRESHOLD = 150;
            float diffY = event.getY() - y1;
            float diffX = event.getX() - x1;
            if (event.getDownTime() >= 1000) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        hasMoved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        diffY = event.getY() - y1;
                        diffX = event.getX() - x1;
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                                hasMoved = true;
                            } else {
                                hasMoved = false;
                            }
                        } else {
                            if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                                hasMoved = true;
                            } else {
                                hasMoved = false;
                            }
                        }
                        diffX = 0;
                        diffY = 0;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (hasMoved != null && hasMoved) {
                            x2 = event.getX();
                            y2 = event.getY();
                            deltaX = x2 - x1;
                            deltaY = y2 - y1;
                            Direction direction = getDirection(x1, y1, x2, y2);
                            executeSwipe(direction);
                            hasMoved = false;
                        }
                    default:
                        x1 = 0f;
                        x2 = 0f;
                        y1 = 0f;
                        y2 = 0f;
                        deltaX = 0f;
                        diffX = 0;
                        deltaY = 0f;
                        diffY = 0;
                        break;
                }
            }
        }
    }

    //Detects if the game is lost
    private static boolean lossDetected() {
        if (gridpoint[currentCord.x][currentCord.y] != null && gridpoint[currentCord.x][currentCord.y].img != R.drawable.newgoal) {
            Gridpoint currPt = gridpoint[currentCord.x][currentCord.y];
            if (numMoves == 0 ) {
                return true;
            }
            ArrayList<Gridpoint> allPoints = new ArrayList<Gridpoint>();
            allPoints = allTouchingPoints(currPt);
            boolean noValidMoves = true;
            for (Gridpoint tmp : allPoints) {
                boolean tmPointValid = false;
                if (ValidMove(tmp) && tmp.coord.isConnected==null) {
                    if(tmp.isAnimated()==false) {
                        tmPointValid = true;
                    }
                    else if(tmp.isAnimated()){
                        tmPointValid=false;
                    }
                    if (!tmp.isUsed() && !tmp.isGoal() && !tmp.isBlock()) {
                        tmPointValid =false;
                    }
                }
                else{
                    tmPointValid=false;
                }
                if (tmPointValid) {
                    if (tmp.isBlock()) {
                        if (breakers == 0) {
                            tmPointValid = false;
                        }
                        else if(breakers>0){
                            tmPointValid = true;
                        }
                    }
                    if (currPt.img == R.drawable.newpivot) {
                        if (!tmp.isUsed() && !tmp.isGoal() && !tmp.isBlock()) {
                            if (pivotConnected(currPt, tmp)) {
                                return true;
                            }
                        }
                        else if (tmp.isBlock()) {
                            if (pivotConnected(currPt, tmp)) {
                                if (breakers == 0) {
                                    tmPointValid=false;
                                }
                                else if(breakers>0){
                                    tmPointValid=true;
                                }
                            }
                            else if(!pivotConnected(currPt,tmp)){
                                tmPointValid = false;
                            }
                        }
                        else if(!pivotConnected(currPt,tmp)){
                            tmPointValid = false;
                        }
                    }
                }
                if(tmPointValid){
                    noValidMoves=false;
                }
            }
            return noValidMoves;
        }
        return false;
    }


    private static ArrayList<Gridpoint> allTouchingPoints(Gridpoint point) {
        ArrayList<Gridpoint> validPoints = new ArrayList<Gridpoint>();
        Gridpoint tmpPt = null;
        if (ValidPoint(point.getCoord().x + 1, sizeX) && gridpoint[point.getCoord().x + 1][point.getCoord().y] != null) {
            tmpPt = gridpoint[point.getCoord().x + 1][point.getCoord().y];
            validPoints.add(tmpPt);
        }
        tmpPt = null;
        if (ValidPoint(point.getCoord().x - 1, sizeX) && gridpoint[point.getCoord().x - 1][point.getCoord().y] != null) {
            tmpPt = gridpoint[point.getCoord().x - 1][point.getCoord().y];
            validPoints.add(tmpPt);
        }
        tmpPt = null;
        if (ValidPoint(point.getCoord().y - 1, sizeY) && gridpoint[point.getCoord().x][point.getCoord().y - 1] != null) {
            tmpPt = gridpoint[point.getCoord().x][point.getCoord().y - 1];
            validPoints.add(tmpPt);
        }
        tmpPt = null;
        if (ValidPoint(point.getCoord().y + 1, sizeY) && gridpoint[point.getCoord().x][point.getCoord().y + 1] != null) {
            tmpPt = gridpoint[point.getCoord().x][point.getCoord().y + 1];
            validPoints.add(tmpPt);
        }
        return validPoints;
    }

    @Override
    public void onBackPressed() { // Called when back button is pressed
        setResult(this.RESULT_OK);
        finish();
    }

    public void Quit(View v) { //Called when Quit button is pressed
        setResult(this.RESULT_OK);
        finish();
    }

    public static void executeSwipe(Direction direction) {
        swipe=true;
        if (currentCord != null) {
            Coordinate current = currentCord;
            int cordX = current.x;
            int cordY = current.y;
            Gridpoint point = gridpoint[cordX][cordY];
            switch (direction){
                case left:
                    cordY--;
                    break;
                case right:
                    cordY++;
                    break;
                case up:
                    cordX--;
                    break;
                case down:
                    cordX++;
                    break;
            }
            int count = -1;
            boolean repeat;
            if (ValidPoint(cordX, sizeX) && ValidPoint(cordY, sizeY)) {
                point = gridpoint[cordX][cordY];
                repeat = true;
            } else {
                repeat = false;
            }
            while (repeat) {
                if (ValidMove(point)) {
                    CalculateMove(point, point.coord);
                    if (point.isGoal()) {
                        repeat = false;
                    }
                } else {
                    repeat = false;
                }
                switch (direction){
                    case left:
                        cordY--;
                        break;
                    case right:
                        cordY++;
                        break;
                    case up:
                        cordX--;
                        break;
                    case down:
                        cordX++;
                        break;
                }
                if (ValidPoint(cordX, sizeX) && ValidPoint(cordY, sizeY)) {
                    point = gridpoint[cordX][cordY];
                } else {
                    repeat = false;
                }
                count++;
            }
            if (count > 1) {
                numMoves--;
                if(lossDetected()){
                    Loss();
                }
            }
            // Toast.makeText(point.context().getApplicationContext(), "Down to Up swipe [Previous]", Toast.LENGTH_SHORT).show();
            gameText.setText("Breakers: " + breakers + " Moves: " + numMoves);
            swipe=false;
        }
    }

//Gets the diresction of a swipe if passed coordinates of the start and end points
    public static Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public static double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

}








