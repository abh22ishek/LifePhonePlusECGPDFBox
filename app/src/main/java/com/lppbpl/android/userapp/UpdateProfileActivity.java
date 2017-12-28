package com.lppbpl.android.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lppbpl.android.userapp.R;

public class UpdateProfileActivity extends AppBaseActivity {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.update_profile);
		setCustomNoIconTitle(R.string.update_profile);

		final TextView updateMobileNo = (TextView) findViewById(R.id.txt_change_mobile_number);
		updateMobileNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent changeNo = new Intent(UpdateProfileActivity.this,
						ChangeMobileNumberActivity.class);
				startActivity(changeNo);
			}
		});

		final TextView emergencyContact = (TextView) findViewById(R.id.txt_emergency_contact);
		emergencyContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent emergencyCont = new Intent(
						UpdateProfileActivity.this, EmergContactActivity.class);
				startActivity(emergencyCont);
			}
		});

		final TextView updateAddress = (TextView) findViewById(R.id.txt_update_address);
		updateAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent updateAddress = new Intent(
						UpdateProfileActivity.this, UpdateAddressActivity.class);
				startActivity(updateAddress);
			}
		});
	}

}
