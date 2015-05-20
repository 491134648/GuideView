package har.lanyan.guideview;

import har.lanyan.guideview.GuideView.OnPageChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GuideView mGuideView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		mGuideView=(GuideView)findViewById(R.id.guideView1);
		mGuideView.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageChange(int currentPage) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "page:  "+currentPage, Toast.LENGTH_SHORT).show();
			}
		});
	}

	
}
