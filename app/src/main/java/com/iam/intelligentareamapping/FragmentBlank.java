package com.iam.intelligentareamapping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.iam.intelligentareamapping.R.layout.fragment_blank;

/**
 * Created by Javier on 11/22/2015.
 */
public class FragmentBlank extends BaseFragment {

    public static FragmentBlank getInstance() {
        fragmentLayout = R.layout.fragment_blank;
        FragmentBlank fragment = new FragmentBlank();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(fragmentLayout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*if(mEventDate != null) {
            if(mUserName!=null) {
                mUserName.setText(Session.getSelectedStudentName());
            }
            StudentActivitiesModel activity = Session.getSelectedActivity();
            mEventDate.setText(activity.Day + "/" + activity.Month + "/" + activity.Year );
            mAudience.setText(activity.Audience);
            mTitle.setText(activity.Title);
            mDescription.setText("                            " + activity.Description);

            mReturnButton = (Button) rootView.findViewById(R.id.return_button);
            mReturnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, EventsFragment.getInstance()).commit();
                }
            });
        }*/
    }
}
