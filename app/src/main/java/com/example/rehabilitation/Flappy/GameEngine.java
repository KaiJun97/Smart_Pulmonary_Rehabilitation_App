package com.example.rehabilitation.Flappy;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.rehabilitation.Activity.MainActivity;
import com.example.rehabilitation.Activity.SelectGameActivity;
import com.example.rehabilitation.Data.RecordValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.example.rehabilitation.Activity.SelectGameActivity.jump;
import static com.example.rehabilitation.Flappy.MainGame.jump;

public class GameEngine {

    private static final String DEBUG_TAG = "DEBUG_TAG";
    BackgroundImage backgroundImage;
    Bird bird;
    static int gameState;
    ArrayList<Tube> tubes;
    Random random;
    int score; // Stores the score
    int scoringTube; // Keeps track of scoring tube
    int jumpinput;
    Paint scorePaint;
    BluetoothGatt bleGatt;
    private JSONArray array;
    private static final String url_saveRecordedData = MainActivity.ipBaseAddress + "/test_save_data.php";



    public GameEngine() throws InterruptedException {
        backgroundImage = new BackgroundImage();
        bird = new Bird();
        // 0 = Not started
        // 1 = Playing
        // 2 = GameOver
        gameState = 0;
        tubes = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < AppConstants.numberOfTubes; i++) {
            int tubeX = AppConstants.SCREEN_WIDTH + i * AppConstants.distanceBetweenTubes;
            // Get topTubeOffsetY
            int topTubeOffsetY = AppConstants.minTubeOffsetY +
                    random.nextInt(AppConstants.maxTubeOffsetY - AppConstants.minTubeOffsetY + 1);
            // Now create Tube objects
            Tube tube = new Tube(tubeX, topTubeOffsetY);
            tubes.add(tube);
        }
        score = 0;
        scoringTube = 0;
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(100);
        scorePaint.setTextAlign(Paint.Align.LEFT);

    }

    public void updateAndDrawTubes(Canvas canvas) {
        if(gameState==0){
            Log.d(DEBUG_TAG, "JUMP -> " + jump);
            if(jump>20){
                gameState = 1;
            }
        }
        if (gameState == 1) {
            if ((tubes.get(scoringTube).getTubeX() < bird.getX() + AppConstants.getBitmapBank().getBirdWidth())
                    && (tubes.get(scoringTube).getTopTubeOffsetY() > bird.getY()
                    || tubes.get(scoringTube).getBottomTubeY() < (bird.getY() +
                    AppConstants.getBitmapBank().getBirdHeight()))) {
                // Go to GameOver screen
                gameState = 2;
                //Log.d("Game", "Over");
                AppConstants.getSoundBank().playHit();
                Context context = AppConstants.gameActivityContext;
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("score", score);
                SelectGameActivity.bleGatt.close();
                context.startActivity(intent);
                ((Activity) context).finish();
                close();
            } else if (tubes.get(scoringTube).getTubeX() < bird.getX() - AppConstants.getBitmapBank().getTubeWidth()) {
                score++;
                scoringTube++;
                if (scoringTube > AppConstants.numberOfTubes - 1) {
                    scoringTube = 0;
                }
                AppConstants.getSoundBank().playPoint();
            }
            for (int i = 0; i < AppConstants.numberOfTubes; i++) {
                if (tubes.get(i).getTubeX() < -AppConstants.getBitmapBank().getTubeWidth()) {
                    tubes.get(i).setTubeX(tubes.get(i).getTubeX() +
                            AppConstants.numberOfTubes * AppConstants.distanceBetweenTubes);
                    int topTubeOffsetY = AppConstants.minTubeOffsetY +
                            random.nextInt(AppConstants.maxTubeOffsetY - AppConstants.minTubeOffsetY + 1);
                    tubes.get(i).setTopTubeOffsetY(topTubeOffsetY);
                    tubes.get(i).setTubeColor();
                }
                tubes.get(i).setTubeX(tubes.get(i).getTubeX() - AppConstants.tubeVelocity);
                if (tubes.get(i).getTubeColor() == 0) {
                    canvas.drawBitmap(AppConstants.getBitmapBank().getTubeTop(), tubes.get(i).getTubeX(), tubes.get(i).getTopTubeY(), null);
                    canvas.drawBitmap(AppConstants.getBitmapBank().getTubeBottom(), tubes.get(i).getTubeX(), tubes.get(i).getBottomTubeY(), null);
                } else {
                    canvas.drawBitmap(AppConstants.getBitmapBank().getRedTubeTop(), tubes.get(i).getTubeX(), tubes.get(i).getTopTubeY(), null);
                    canvas.drawBitmap(AppConstants.getBitmapBank().getRedTubeBottom(), tubes.get(i).getTubeX(), tubes.get(i).getBottomTubeY(), null);
                }
            }

            canvas.drawText("Pt: " + score, 0, 110, scorePaint);
        }
    }

    public void updateAndDrawBackgroundImage(Canvas canvas) {
        backgroundImage.setX(backgroundImage.getX() - backgroundImage.getVelocity());
        if (backgroundImage.getX() < -AppConstants.getBitmapBank().getBackgroundWidth()) {
            backgroundImage.setX(0);
        }
        canvas.drawBitmap(AppConstants.getBitmapBank().getBackground(), backgroundImage.getX(), backgroundImage.getY(), null);
        if (backgroundImage.getX() < -(AppConstants.getBitmapBank().getBackgroundWidth() - AppConstants.SCREEN_WIDTH)) {
            canvas.drawBitmap(AppConstants.getBitmapBank().getBackground(), backgroundImage.getX() +
                    AppConstants.getBitmapBank().getBackgroundWidth(), backgroundImage.getY(), null);
        }
    }

    public void updateAndDrawBird(Canvas canvas) {
        if (gameState == 1) {
            if (bird.getY() < (AppConstants.SCREEN_HEIGHT - AppConstants.getBitmapBank().getBirdHeight()) || bird.getVelocity() < 0) {
                bird.setVelocity(bird.getVelocity() + AppConstants.gravity);
                bird.setY(bird.getY() + bird.getVelocity());
            }
            //(jump > 15) jumpinput = 1;
            //else jumpinput = 0;
            if(jump > 18){
                bird.setVelocity(-jump/3);
            }
        }
        int currentFrame = bird.getCurrentFrame();
        canvas.drawBitmap(AppConstants.getBitmapBank().getBird(currentFrame), bird.getX(), bird.getY(), null);
        currentFrame++;
        // If it exceeds maxframe re-initialize to 0
        if (currentFrame > bird.maxFrame) {
            currentFrame = 0;
        }
        bird.setCurrentFrame(currentFrame);
    }

    public void close(){
        SelectGameActivity.bleGatt.disconnect();
        SelectGameActivity.bleGattService.getBleGatt().close();
        /*
         * convert array into json format
         * post json \
         * sql statement to insert data
         *
         * */
        array = new JSONArray();
        Log.e("Valarr", SelectGameActivity.valuesArr.toString());
        for (int i = 0; i < SelectGameActivity.valuesArr.size(); i++) {
            JSONObject obj = new JSONObject();
            RecordValue val = SelectGameActivity.valuesArr.get(i);
//                    Log.e("check record id",val.getRecID());
            HashMap<String, String> params = new HashMap<String, String>();
            try {
                obj.put("indexVal", val.getRecDataID());
                obj.put("recId", val.getRecID());
                obj.put("value", val.getValue());
                obj.put("time", val.getsTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }
//                JSONObject valueObj = new JSONObject();
//                try {
//                    valueObj.put("values", array);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
        Log.e("String", array.toString());
        //saveData(url_saveRecordedData);
        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.POST, url_saveRecordedData, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.i("----Response", jsonArray + " " + url_saveRecordedData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("Error", "Error");
                volleyError.printStackTrace();
            }
        });

        SelectGameActivity.valuesArr.clear();
    }

}
