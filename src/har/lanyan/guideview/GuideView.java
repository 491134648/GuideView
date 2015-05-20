package har.lanyan.guideview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class GuideView extends ViewGroup {

	/**
	 * 
	 <attr name="orientation"> <enum name="horizontal" value="1" /> <enum
	 * name="vertical" value="2" /> </attr>
	 **/

	/** �������� */
	private int mOrientation = 0;

	/** ˮƽ���� */
	private int mHorientation = 0;

	/** ��ֱ���� */
	private int mVertical = 1;

	/** ��Ļ��� */
	private int mScreenWidth;

	/** ��Ļ�߶� */
	private int mScreenHeight;

	/** ����״̬ */
	private boolean isScrolling;

	/** ���������� */
	private Scroller mScroller;

	/** ��¼��ǰ��x/y��ֵ */
	private PointF mPointF;

	/** ��¼��һ�ε�x��yֵ */
	private PointF mLastPointF;

	/** Scroller ��Ӧ�Ŀ�ʼ���� */
	private Point mScrollStartPoint;

	/** Scroller ��Ӧ�Ľ������� */
	private Point mScrollStopPoint;

	/** ��¼�����ľ��� */
	private PointF mDistancePointF;

	/**ScrollXY �Ĳ�ֵ*/
	private Point mDistanceScrollPoint;

	/** ���ٶȼ�� */
	private VelocityTracker mVelocityTracker;

	/**�л���Ļʱ�Ļص�����*/
	private OnPageChangeListener mOnPageChangeListener;

	/**
	 * ��¼��ǰҳ
	 */
	private int currentPage = 0;

	public GuideView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		// ��ȡ�Զ�������
		TypedArray mTypeArray = context.obtainStyledAttributes(
				attrs, R.styleable.GuideView_orientation);

		mOrientation = mTypeArray.getInteger(
				R.styleable.GuideView_orientation_orientation, mOrientation);

		mTypeArray.recycle();
		// ��ȡ��Ļ���
		initialScreen(context);

		mScroller = new Scroller(context);

		mPointF = new PointF();
		mLastPointF = new PointF();
		mScrollStartPoint = new Point();
		mScrollStopPoint = new Point();
		mDistancePointF = new PointF();
		mDistanceScrollPoint=new Point();
	}

	public GuideView(Context context, AttributeSet attrs) {
		this(context, attrs, 1);
		// TODO Auto-generated constructor stub
	}

	public GuideView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��ȡ�Ӳ��֣����²����Ӳ��ֿ��
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

		if (changed) {
			// ���²���layout��λ��
			MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
			int childCount = getChildCount();
			if (mOrientation == mHorientation) {
				params.width = mScreenWidth * getChildCount();
				setLayoutParams(params);

				for (int i = 0; i < childCount; i++) {
					View view = getChildAt(i);
					if (view.getVisibility() != View.GONE) {
						view.layout(i * mScreenWidth, t, i * mScreenWidth
								+ mScreenWidth, b);
					}
				}
			} else if (mOrientation == mVertical) {

				params.height = mScreenHeight * getChildCount();
				setLayoutParams(params);

				for (int i = 0; i < childCount; i++) {
					View view = getChildAt(i);
					// view û�����ص��������¶�λ
					if (view.getVisibility() != View.GONE) {
						view.layout(l, i * mScreenHeight, r, i * mScreenHeight
								+ mScreenHeight);
					}
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// �Ƚ����¼��ж�����
		if(currentPage==getChildCount()-1){
			Toast.makeText(getContext(), "finish", Toast.LENGTH_SHORT).show();
			return super.onTouchEvent(event);
		}
		
		if (isScrolling) 
			return super.onTouchEvent(event);
		
			mPointF.x = event.getX();
			mPointF.y = event.getY();

			// ��ʼ�����ٶȼ����
			initialVelocity(event);

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// ���û�����ʱ��¼��������Ϣ
				Log.i("info"," *******mPoint value****"+"x:"+mPointF.x+"y:"+mPointF.y);
				getStartScrollXY();
				mLastPointF.x = mPointF.x;
				mLastPointF.y = mPointF.y;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				Log.i("info"," *******mLastPoint value****"+"x:"+mLastPointF.x+"y:"+mLastPointF.y);
				Log.i("info"," *******mPoint value****"+"x:"+mPointF.x+"y:"+mPointF.y);
				Log.i("info"," *******************************************");
				Log.i("info"," *******************************************");
				/**
				 * Stops the animation. Contrary to
				 * {@link #forceFinished(boolean)}, aborting the animating cause
				 * the scroller to move to the final x and y position Դ��˵����
				 * mScroller.abortAnimation() ���������û�н�������ô����ֹ������
				 * 
				 * @see #forceFinished(boolean)
				 */
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}

				mDistancePointF.x = mLastPointF.x - mPointF.x;
				mDistancePointF.y = mLastPointF.y - mPointF.y;

				Log.i("info"," *******mDistancePointF value ******"+"dx: "+mDistancePointF.x+" dy: "+mDistancePointF.y);
				getStopScrollXY();

				// ���жϻ����ķ���ȷ�������ľ��� scrollBy��x,y��

				// 1.y��---���ϻ���--��һ����ͼ
				// 2.y��---���»���--��һ����ͼ
				// 3.x��---���󻬶�--��һ����ͼ
				// 4.x��---���һ���--��һ����ͼ

				/**
				 * 320*480 -8 mlasty=-10 currenty=-2
				 * distance=mlasty-currenty=-8��0 scrolly+distance<0?
				 * 
				 * ����������ʱ��ȷ����ͼ���ϻ��������� ��һ����ͼ
				 * 
				 * ���¶���distanceY��ֵ�Ա���ScrollBy(x,y)����
				 * 
				 * ����˵����
				 * getScrollX()˵��:=�ֻ���Ļ��ʾ�������Ͻ�x�����ȥMultiViewGroup��ͼ���Ͻ�x����=320
				 * 
				 * getScrollY()˵��:=�ֻ���Ļ��ʾ�������Ͻ�y�����ȥMultiViewGroup��ͼ���Ͻ�y����=0(
				 * ��Ϊ����ͼ�ĸ߶Ⱥ��ֻ���Ļ�߶�һ��)
				 * 
				 * 
				 **/

				if (mOrientation == mHorientation) {
					if (mDistancePointF.x > 0
							&& mScrollStopPoint.x + mDistancePointF.x > getWidth()-mScreenWidth) {
						mDistancePointF.x = getWidth() - mScreenWidth -mScrollStopPoint.x;
					} else if (mDistancePointF.x < 0
							&& mScrollStopPoint.x + mDistancePointF.x < 0) {
						mDistancePointF.x = - mScrollStopPoint.x;
					}
					scrollBy((int) mDistancePointF.x, 0);
				} else if (mOrientation == mVertical) {
					if (mDistancePointF.y < 0
							&& mScrollStopPoint.y + mDistancePointF.y < 0) {
						mDistancePointF.y = -mScrollStopPoint.y;
					}
					if (mDistancePointF.y > 0
							&& mScrollStopPoint.y + mDistancePointF.y > getHeight()
							- mScreenHeight) {
						mDistancePointF.y = getHeight() - mScreenHeight
								- mScrollStopPoint.y;
					}
					scrollBy(0, (int) mDistancePointF.y);
				}
				mLastPointF.x = mPointF.x;
				mLastPointF.y = mPointF.y;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				getStopScrollXY();
				getDistanceScrollXY();
				//�Ƚϻ�����������
				//�ж����ϻ��������»���
				if(checkDirection()){
					//�ϻ��������ظ��ࡷ
					if(isScrollToNext()){
						//�ܻ�������һҳ
						if(mOrientation==mHorientation){
							mScroller.startScroll(getScrollX(), 0,mScreenWidth - mDistanceScrollPoint.x,0);
						}else if (mOrientation==mVertical){
							mScroller.startScroll(0, getScrollY(), 0, mScreenHeight
									- mDistanceScrollPoint.y);
						}
					}else{
						//���ܻ�������һҳ
						if(mOrientation==mHorientation){
							mScroller.startScroll(getScrollX(), 0,-mDistanceScrollPoint.x,0);
						}else if (mOrientation==mVertical){
							mScroller.startScroll(0, getScrollY(), 0, -mDistanceScrollPoint.y);
						}
					}
				}else{
					//���»�����ˢ�¡�
					if(isScrollToprivew()){
						//�ܻ�������һҳ
						if(mOrientation==mHorientation){
							mScroller.startScroll( getScrollX(), 0,
									-mScreenWidth - mDistanceScrollPoint.x,0);
						}else if (mOrientation==mVertical){
							mScroller.startScroll(0, getScrollY(), 0,
									-mScreenHeight - mDistanceScrollPoint.y);
						}
					}else{
						//���ܻ�������һҳ
						if(mOrientation==mHorientation){
							mScroller.startScroll(getScrollX(),0, -mDistanceScrollPoint.x, 0);
						}else if (mOrientation==mVertical){
							mScroller.startScroll(0, getScrollY(), 0, -mDistanceScrollPoint.y);
						}
					}
				}

				isScrolling = true;
				postInvalidate();
				recycleVelocity();
			}

			return true;
	}

	/**
	 * Called by a parent to request that a child update its values for mScrollX
	 * and mScrollY if necessary. This will typically be done if the child is
	 * animating a scroll using a {@link android.widget.Scroller Scroller}
	 * object.
	 * 
	 * Ϊ�����ڿ��ƻ������ƣ�Android����ṩ�� computeScroll()����ȥ����������̡��ڻ���Viewʱ������draw()���̵��ø�
	 * ��������ˣ� �����ʹ��Scrollerʵ�������ǾͿ��Ի�õ�ǰӦ�õ�ƫ�����꣬�ֶ�ʹView/ViewGroupƫ�����ô���
	 * computeScroll()����ԭ�����£��÷���λ��ViewGroup.java����   
	 */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();

		if (mOrientation== mVertical) {
			if (mScroller.computeScrollOffset()) {
				scrollTo(0, mScroller.getCurrY());
				postInvalidate();
			} else {
				int position = getScrollY() / mScreenHeight;
				if (position != currentPage) {
					if (mOnPageChangeListener != null) {
						currentPage = position;
						mOnPageChangeListener.onPageChange(currentPage);
					}
				}
			}
		} else if (mOrientation== mHorientation) {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), 0);
				postInvalidate();
			} else {
				int position = getScrollX() / mScreenWidth;
				if (position != currentPage) {
					if (mOnPageChangeListener != null) {
						currentPage = position;
						mOnPageChangeListener.onPageChange(currentPage);
					}
				}
			}
		}
		isScrolling = false;
	}
	/************************************ Method *********************************************/

	/**
	 * ��ȡ��Ļ���
	 */
	public void initialScreen(Context context) {
		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
	}

	/**
	 * ��ʼ�����ٶȼ����
	 * 
	 * @param event
	 */
	private void initialVelocity(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	/**
	 * ��ʼ��scrollX scrollY
	 */
	private void getStartScrollXY(){
		mScrollStartPoint.x = getScrollX();
		mScrollStartPoint.y = getScrollY();
	}
	/**
	 * ֹͣ�������ScrollX ScrollY
	 */
	private void getStopScrollXY(){
		mScrollStopPoint.x = getScrollX();
		mScrollStopPoint.y = getScrollY();
	}
	/**
	 * �Ƚϻ�����ScrollX ScrollY��ֵ
	 */
	private void getDistanceScrollXY(){
		mDistanceScrollPoint.x = mScrollStopPoint.x-mScrollStartPoint.x;
		mDistanceScrollPoint.y = mScrollStopPoint.y-mScrollStartPoint.y;
	}
	/**
	 * ��黬������
	 * @return  true ���ظ���  false ˢ��
	 */
	public boolean checkDirection(){
		boolean mDirection =false;
		if (mOrientation == mVertical) {
			mDirection = mDistanceScrollPoint.y > 0 ? true : false;
		} else if (mOrientation== mHorientation) {
			mDirection = - mDistanceScrollPoint.x < 0 ? true : false;
		}
		return mDirection;
	}
	/**
	 * ���ݻ��������ж� �Ƿ��ܹ���������һ��
	 *  ���ظ���
	 * @return
	 */
	private boolean isScrollToNext() {
		boolean isScrollTo = false;
		if (mOrientation == mVertical) {
			isScrollTo = mDistanceScrollPoint.y > mScreenHeight / 2
					|| Math.abs(getVelocity()) > 600;
		} else if (mOrientation == mHorientation) {
			isScrollTo = mDistanceScrollPoint.x > mScreenWidth / 2
					|| Math.abs(getVelocitx()) > 600;
		}
		return isScrollTo;
	}

	/**
	 * ���ݻ��������ж� �Ƿ��ܹ���������һ��
	 * ˢ��
	 * @return
	 */
	private boolean isScrollToprivew() {
		boolean isScrollTo = false;
		if (mOrientation == mVertical) {
			isScrollTo = -mDistanceScrollPoint.y > mScreenHeight / 2
					|| Math.abs(getVelocity()) > 600;
		} else if (mOrientation == mHorientation) {
			isScrollTo = -mDistanceScrollPoint.x > mScreenWidth / 2
					|| Math.abs(getVelocitx()) > 600;
		}
		return isScrollTo;
	}
	/**
	 * ��ȡx����ļ��ٶ�
	 * 
	 * @return
	 */
	private int getVelocitx() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocitx = (int) mVelocityTracker.getXVelocity(1000);
		velocitx = (int) mVelocityTracker.getXVelocity(1000);
		return velocitx;
	}
	/**
	 * ��ȡy����ļ��ٶ�
	 * 
	 * @return
	 */
	private int getVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getYVelocity(1000);
		velocity = (int) mVelocityTracker.getYVelocity(1000);
		return velocity;
	}
	/**
	 * �ͷ���Դ
	 */
	private void recycleVelocity() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	/**
	 * ���ûص��ӿ�
	 * 
	 * @param onPageChangeListener
	 */
	public void setOnPageChangeListener(
			OnPageChangeListener onPageChangeListener) {
		mOnPageChangeListener = onPageChangeListener;
	}

	/**
	 * �ص��ӿ�
	 * 
	 * @author zhy
	 * 
	 */
	public interface OnPageChangeListener {
		void onPageChange(int currentPage);
	}
}
