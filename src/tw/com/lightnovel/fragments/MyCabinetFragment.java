package tw.com.lightnovel.fragments;

import java.util.List;

import tw.com.lightnovel.activities.BaseNovelIndexActivity;
import tw.com.lightnovel.activities.LocalNovelIndexActivity;
import tw.com.lightnovel.activities.OnlineNovelIndexActivity;
import tw.com.lightnovel.adapters.MainListDataAdapter;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageManager;
import tw.com.thinkso.novelreaderapp.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MyCabinetFragment extends Fragment {
	public static final String TAG = "mycabinet_fragment_tag";

	private ImageManager mImgManager;
	private ApplicationContext mApp;

	private ListView mCabinetList;
	private List<ListRowData> mCabinetRecords;
	private MainListDataAdapter mListAdapter;

	private static MyCabinetFragment mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mycabinet, container,
				false);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mCabinetRecords = mApp.getNovelRecords();

		setList(mCabinetRecords);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		mCabinetList = (ListView) view.findViewById(R.id.cabinet_list);
		mCabinetList.setOnItemClickListener(mListItemClickListener);
		mCabinetList.setOnItemLongClickListener(mListItemLongClickListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;
		mApp = ApplicationContext.getInstance();
		mImgManager = new HttpImageManager();		
	}

	private OnItemClickListener mListItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (mCabinetRecords == null)
				return;

			ListRowData novel = mCabinetRecords.get(arg2);
			Intent intent = new Intent(getActivity(), OnlineNovelIndexActivity.class);
			intent.putExtra(TSONovelURLs.NOVEL_URL_TAG, novel.getUri());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
		}
	};

	private OnItemLongClickListener mListItemLongClickListener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {

			ListRowData record = mCabinetRecords.get(position);
			ListDialogFragment.newInstance(position, record).show(
					getFragmentManager(), "dialog");

			return true;
		}
	};

	private void setList(List<ListRowData> records) {
		mListAdapter = new MainListDataAdapter(getActivity(), records,
				mImgManager);
		mCabinetList.setAdapter(mListAdapter);
	}

	private void deleteListItem(int index) {
		mCabinetRecords.remove(index);
		mListAdapter.notifyDataSetChanged();
	}

	private void deleteRecord(String id) {
		mApp.deleteNovelRecord(id);
	}

	private static void delete(int index, String id) {
		if (mContext != null) {
			mContext.deleteRecord(id);
			mContext.deleteListItem(index);
		}
	}

	public static class ListDialogFragment extends DialogFragment {

		public static ListDialogFragment newInstance(int index,
				ListRowData record) {
			ListDialogFragment fragment = new ListDialogFragment();
			Bundle args = new Bundle();
			args.putParcelable("record", record);
			args.putInt("index", index);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ListRowData record = getArguments().getParcelable("record");
			final int index = getArguments().getInt("index");

			String[] items = new String[] {
					getString(R.string.dialog_novel_index),
					getString(R.string.dialog_delete) };
			return new AlertDialog.Builder(getActivity())
					.setTitle(record.getTitle())
					.setItems(items, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								Intent intent = new Intent(getActivity(),
										BaseNovelIndexActivity.class);
								intent.putExtra(TSONovelURLs.NOVEL_URL_TAG,
										record.getUri());
								startActivity(intent);

								break;

							case 1:
								delete(index, record.getUri());

								break;

							default:
								break;
							}
						}
					})
					.setNegativeButton(getString(R.string.dialog_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();
		}
	}
}
