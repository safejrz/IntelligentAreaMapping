package com.iam.intelligentareamapping;

import com.kontakt.sdk.android.ble.device.BeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by jaime on 11/21/15.
 */
public class SortedByDistanceList extends Vector<IBeaconDevice>
{

    @Override
    public boolean add(IBeaconDevice bd)
    {
       int position = getPositionToInsert(bd, 0,size()-1);
       add(position,bd);
       return true;
    }

    private int getPositionToInsert(IBeaconDevice bd, int startPoint, int endPoint)
    {
        if(startPoint > endPoint ) return startPoint;
        else
        {
            int pivot = (size()-1) / 2;
            if(bd.getDistance() <= get(pivot).getDistance())
                return getPositionToInsert(bd, startPoint,pivot);
            return getPositionToInsert(bd, pivot+1,endPoint);
        }
    }

}
