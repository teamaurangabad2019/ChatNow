
package com.teammandroid.chatnow.interfaces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.utils.Utility;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class MessageSwipeController extends ItemTouchHelper.SimpleCallback {
    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     */

    Context context;
    private View mView;
    Drawable imageDrawable;
    private boolean swipeBack;
    private RecyclerView.ViewHolder currentItemViewHolder;

    private float dX = 0f;

    SwipeControllerActions swipeControllerActions;
    private boolean startTracking;
    private long lastReplyButtonAnimationTime = 0;
    private float replyButtonProgress = 0;
    private boolean isVibrate;

    public MessageSwipeController(Context context,SwipeControllerActions swipeControllerActions, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        this.context = context;
        this.swipeControllerActions = swipeControllerActions;
    }

    /*public MessageSwipeController(Context context,SwipeControllerActions swipeControllerActions, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        this.context = context;
        this.swipeControllerActions = swipeControllerActions;
    }*/

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("DIRECTION", direction + "");
        if (direction == 8) {
            //mMovieAdapter.remove(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        mView = viewHolder.itemView;
        imageDrawable = context.getDrawable(R.drawable.ic_reply_black_24dp);
        return ItemTouchHelper.Callback.makeMovementFlags(ACTION_STATE_IDLE, RIGHT);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       /* View itemView = viewHolder.itemView;
        LinearLayout swipe_bg = itemView.findViewById(R.id.ll_left);
        swipe_bg.setY(itemView.getTop());
        if (isCurrentlyActive) {
            swipe_bg.setVisibility(View.VISIBLE);
        } else {
            swipe_bg.setVisibility(View.GONE);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);*/

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder);
        }

        if (mView.getTranslationX() < convertTodp(130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            this.dX = dX;
            startTracking = true;
        }
        currentItemViewHolder = viewHolder;
        drawReplyButton(c);
    }

    private void setTouchListener( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (Math.abs(mView.getTranslationX()) >= MessageSwipeController.convertTodp(100)) {
                        swipeControllerActions.showReplyUI(viewHolder.getAdapterPosition());
                    }
                }
                return false;
            }
        });
    }

    private void drawReplyButton( Canvas canvas) {
        if (currentItemViewHolder == null) {
            return;
        }
        float translationX = mView.getTranslationX();
        long newTime = System.currentTimeMillis();
        float dt = Math.min(17, newTime - lastReplyButtonAnimationTime);
        lastReplyButtonAnimationTime = newTime;
        boolean showing = translationX >= convertTodp(30);
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f;
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f;
                } else {
                    mView.invalidate();
                }
            }
        } else if (translationX <= 0.0f) {
            replyButtonProgress = 0f;
            startTracking = false;
            isVibrate = false;
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f;
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f;
                } else {
                    mView.invalidate();
                }
            }
        }
        int alpha;
        Float scale;
        if (showing) {
             if (replyButtonProgress <= 0.8f) {
                 scale = 1.2f * (replyButtonProgress / 0.8f);
            } else {
                 scale = 1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f);
            }
            alpha = (int) Math.min(255f, 255 * (replyButtonProgress / 0.8f));
        } else {
            scale = replyButtonProgress;
            alpha = (int) Math.min(255f, 255 * replyButtonProgress);
        }

        alpha = imageDrawable.getAlpha();
        if (startTracking) {
            if (!isVibrate && mView.getTranslationX() >= convertTodp(100)) {
                mView.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                );
                isVibrate = true;
            }
        }

        int x = 0;
        if (mView.getTranslationX() > convertTodp(130)) {
            x = convertTodp(130) / 2;
        } else {
            x = (int)(mView.getTranslationX() / 2);
        }

        float  y = (mView.getTop() + mView.getMeasuredHeight() / 2);
       /* shareRound.colorFilter =
                PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorE), PorterDuff.Mode.MULTIPLY)

        shareRound.setBounds(
                (x - convertTodp(18) * scale).toInt(),
                (y - convertTodp(18) * scale).toInt(),
                (x + convertTodp(18) * scale).toInt(),
                (y + convertTodp(18) * scale).toInt()
        )
        shareRound.draw(canvas)
        imageDrawable.setBounds(
                (x - convertTodp(12) * scale).toInt(),
                (y - convertTodp(11) * scale).toInt(),
                (x + convertTodp(12) * scale).toInt(),
                (y + convertTodp(10) * scale).toInt()
        )*/
        imageDrawable.draw(canvas);
        /*shareRound.alpha = 255
        imageDrawable.alpha = 255*/
    }


    private static int convertTodp(int pixel) {
        return Utility.dp((float)pixel);
    }

}
