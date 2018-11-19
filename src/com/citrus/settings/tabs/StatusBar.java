/*
 * Copyright (C) 2016 Cardinal-AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citrus.settings.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import android.telephony.TelephonyManager;

import android.text.Spannable;
import android.text.TextUtils;

import android.widget.EditText;

import com.android.settings.R;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.citrus.settings.preference.CustomSeekBarPreference;
import com.citrus.settings.preference.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";

    private Preference mCustomCarrierLabel;

    private String mCustomCarrierLabelText;

    private ListPreference mCustomLogoStyle;
    private ListPreference mCustomLogoPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar_tab);

        ContentResolver resolver = getActivity().getContentResolver();
/*
        mCustomLogoStyle = (ListPreference) findPreference("status_bar_custom_logo_style");
        int customLogoStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mCustomLogoStyle.setValue(String.valueOf(customLogoStyle));
        mCustomLogoStyle.setSummary(mCustomLogoStyle.getEntry());
        mCustomLogoStyle.setOnPreferenceChangeListener(this);

        mCustomLogoPos = (ListPreference) findPreference("status_bar_custom_logo_position");
        int customLogoPos = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_LOGO_POSITION, 0,
                UserHandle.USER_CURRENT);
        mCustomLogoPos.setValue(String.valueOf(customLogoPos));
        mCustomLogoPos.setSummary(mCustomLogoPos.getEntry());
        mCustomLogoPos.setOnPreferenceChangeListener(this);
*/
        // custom carrier label
        mCustomCarrierLabel = (Preference) findPreference(CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CUSTOM_SQUASH;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
/*        if (preference == mCustomLogoStyle) {
            int val = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
		            Settings.System.STATUS_BAR_CUSTOM_LOGO_STYLE, val,
                    UserHandle.USER_CURRENT);
            int index = mCustomLogoStyle.findIndexOfValue((String) objValue);
            mCustomLogoStyle.setSummary(mCustomLogoStyle.getEntries()[index]);
            return true;
        }  else if (preference == mCustomLogoPos) {
            int val = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
		            Settings.System.STATUS_BAR_CUSTOM_LOGO_POSITION, val,
                    UserHandle.USER_CURRENT);
            int index = mCustomLogoPos.findIndexOfValue((String) objValue);
            mCustomLogoPos.setSummary(mCustomLogoPos.getEntries()[index]);
            return true;
        }*/
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;
        if (preference.getKey() == null) return false;
        if (preference.getKey().equals(CUSTOM_CARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);
            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(getActivity().getContentResolver(),
                                    Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.statusbar_tab;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}
