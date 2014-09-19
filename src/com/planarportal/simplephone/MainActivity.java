
/*
 * This file is part of the simplePhone application.
 *
 * (c) Jean-Michel VILOMET <jmvilomet@faeryscape.com>
 *
 * For the full copyright and license information, please view the LICENSE.txt
 * file that was distributed with this source code.
 */
package com.planarportal.simplephone;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button btnSearch;
    Button btnCall;
    Button btnSMS;
    Button btnConv;
    Button btnTake;
    Button btnSee;
    Button btnMMS;
    String emptyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        TextView pPhone = (TextView) findViewById(R.id.pickedPhone);
        emptyValue = pPhone.getText().toString().trim();

        btnSearch = (Button) this.findViewById(R.id.btnSearch);
        btnCall = (Button) this.findViewById(R.id.btnCall);
        btnSMS = (Button) this.findViewById(R.id.btnSMS);
        btnConv = (Button) this.findViewById(R.id.btnConv);
        btnTake = (Button) this.findViewById(R.id.btnTake);
        btnSee = (Button) this.findViewById(R.id.btnSee);
        btnMMS = (Button) this.findViewById(R.id.btnMMS);

        btnSearch.setOnClickListener(new myOwnClickListener());
        btnCall.setOnClickListener(new myOwnClickListener());
        btnSMS.setOnClickListener(new myOwnClickListener());
        btnConv.setOnClickListener(new myOwnClickListener());
        btnTake.setOnClickListener(new myOwnClickListener());
        btnSee.setOnClickListener(new myOwnClickListener());
        btnMMS.setOnClickListener(new myOwnClickListener());

    	super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data != null) {
	    	if (requestCode==1){ // retrieve contact phone and name
		        Uri uri = data.getData();
		        if (uri != null) {
		            Cursor c = null;
		            try {
		                c = getContentResolver().query(uri, new String[]{
		                            ContactsContract.CommonDataKinds.Phone.NUMBER,
		                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
		                        null, null, null);

		                if (c != null && c.moveToFirst()) {
		                    String number = c.getString(0);
		                    String name = c.getString(1);
		                    TextView pContact = (TextView) findViewById(R.id.pickedContact);
		                    TextView pPhone = (TextView) findViewById(R.id.pickedPhone);
		                    pContact.setText(name);
		                    pPhone.setText(number);
		                }
		            } finally {
		                if (c != null) {
		                    c.close();
		                }
		            }
		        }
	    	}else if(requestCode==2){ // attach the selected picture to an MMS
                TextView pPhone = (TextView) findViewById(R.id.pickedPhone);
	    		Uri uri = data.getData();
	    		Intent sendIntent = new Intent(Intent.ACTION_SEND);
	    		sendIntent.putExtra("address", pPhone.getText().toString().trim());
	    		sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
	    		sendIntent.setType("image/png");
	    		startActivity(sendIntent);
	    	}
	    }
	}

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
	
    public class myOwnClickListener implements OnClickListener {

    	Intent intent;
    	TextView pPhone;
    	String url;
    	String noContact = getResources().getString(R.string.noContact);
    	String noCamera  = getResources().getString(R.string.noCamera);

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.btnSearch: // search a contact
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
					startActivityForResult(intent, 1);
					break;
				case R.id.btnCall: // call
					pPhone = (TextView) findViewById(R.id.pickedPhone);
					if (pPhone.getText().toString().trim().length()==0){ // no contact selected ?
						Toast.makeText(v.getContext(), noContact, Toast.LENGTH_SHORT).show();
					}else{
						url = "tel:"+pPhone.getText().toString().trim();
						intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
						startActivity(intent);
					}
					break;
				case R.id.btnSMS: // send SMS
					pPhone = (TextView) findViewById(R.id.pickedPhone);
					if (pPhone.getText().toString().trim().length()==0){
						Toast.makeText(v.getContext(), noContact, Toast.LENGTH_SHORT).show();
					}else{
						url = "sms:"+pPhone.getText().toString().trim();
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(intent);
					}
					break;
				case R.id.btnConv: // browse SMS history
					pPhone = (TextView) findViewById(R.id.pickedPhone);
					if (pPhone.getText().toString().trim().length()==0){
						Toast.makeText(v.getContext(), noContact, Toast.LENGTH_SHORT).show();
					}else{
						intent = new Intent(Intent.ACTION_SENDTO);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setType("vnd.android-dir/mms-sms");
						intent.setData(Uri.parse("sms:" + pPhone.getText().toString().trim()));
						startActivity(intent);
					}
					break;
				case R.id.btnTake: // take picture
					Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					try {
						PackageManager pm = v.getContext().getPackageManager();
						final ResolveInfo mInfo = pm.resolveActivity(i, 0);
						Intent intent = new Intent();
						intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
						intent.setAction(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						startActivity(intent);
					} catch (Exception e){
						Toast.makeText(v.getContext(), noCamera, Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.btnSee: // browse gallery
					intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivity(intent);
					break;

				case R.id.btnMMS: // send MMS with attached picture
					pPhone = (TextView) findViewById(R.id.pickedPhone);
					if (pPhone.getText().toString().trim().length()==0){
						Toast.makeText(v.getContext(), noContact, Toast.LENGTH_SHORT).show();
					}else{
						intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent,2);
					}
					break;
			}
		}

    }
}