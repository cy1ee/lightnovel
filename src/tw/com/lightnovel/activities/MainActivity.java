package tw.com.lightnovel.activities;

import tw.com.lightnovel.classes.NovelSearchListener;
import tw.com.lightnovel.fragments.AnimaxNovelFragment;
import tw.com.lightnovel.fragments.MyCabinetFragment;
import tw.com.lightnovel.fragments.NewNovelFragment;
import tw.com.thinkso.novelreaderapp.R;
import android.R.color;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private ViewPager mViewPager;
	private NovelPagerAdapter mNovelPagerAdapter;

	private MyCabinetFragment mCabinetFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		mNovelPagerAdapter = new NovelPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mNovelPagerAdapter);

		actionBar.setStackedBackgroundDrawable(new ColorDrawable(color.white));

		for (int i = 0; i < mNovelPagerAdapter.getCount(); i++) {
			Tab tab = actionBar.newTab();
			tab.setText(mNovelPagerAdapter.getPageTitle(i));
			tab.setTabListener(this);
			actionBar.addTab(tab);
		}
	}

	class NovelPagerAdapter extends FragmentPagerAdapter {

		public NovelPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			// TODO Auto-generated constructor stub
		}

		@Override
		public android.support.v4.app.Fragment getItem(int tabIndex) {
			Fragment fragment = null;

			switch (tabIndex) {
			case 0:
				mCabinetFragment = new MyCabinetFragment();
				fragment = mCabinetFragment;
				break;
			case 1:
				fragment = new AnimaxNovelFragment();
				break;
			case 2:
				fragment = new NewNovelFragment();
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabTitle = "";

			switch (position) {
			case 0:
				tabTitle = getResources().getString(R.string.tab_mycabinet);
				break;
			case 1:
				tabTitle = getResources().getString(R.string.tab_animax_novel);
				break;
			case 2:
				tabTitle = getResources().getString(R.string.tab_new_novel);
				break;
			}

			return tabTitle;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actions, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setQueryHint(getResources().getString(R.string.search_hint));
		searchView.setOnQueryTextListener(new NovelSearchListener(this));

		Spinner spnAction = (Spinner) menu.findItem(R.id.action_list)
				.getActionView().findViewById(R.id.spn_action);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.action_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnAction.setAdapter(adapter);
		spnAction.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				switch (index) {
				case 0:
					break;

				case 1:
					Intent intent = new Intent(MainActivity.this,
							LocalNovelActivity.class);
					startActivity(intent);
					finish();
					break;

				default:
					break;
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		return true;
	}

	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
}
