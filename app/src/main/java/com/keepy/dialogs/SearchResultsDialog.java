package com.keepy.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.keepy.R;
import com.keepy.adapters.SearchResultsRv;
import com.keepy.behaviour.IRequests;
import com.keepy.behaviour.ISearchResultsDialog;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;

import java.util.ArrayList;
import java.util.List;




/*
    * This class is a dialog that shows the search results
 */
public class SearchResultsDialog extends AlertDialog {


    public SearchResultsDialog(Context context,
                               User client,
                               List<User> keepers,
                               List<ServiceRequest> existingRequestsList,
                               IRequests iRequests,
                               ISearchResultsDialog iSearchResultsDialog) {
        super(context);

        List<ServiceRequest> existingRequestsList1;
        if (existingRequestsList == null)// if there are no existing requests -> create a empty list
            existingRequestsList1 = new ArrayList<>();
        else //  if there are existing requests -> copy them to a new list
            existingRequestsList1 = existingRequestsList;

        View view = getLayoutInflater().inflate(R.layout.search_results_dialog, null, false);

        setTitle("Search Results");

        setButton(BUTTON_POSITIVE, "Close", (dialog, which) -> {
            dialog.dismiss();
            iSearchResultsDialog.close();
        });


        // if there are no keepers -> show a -no results- message
        // else -> show the keepers
        if (keepers == null || keepers.size() > 0) {
            setView(view);
            RecyclerView rv = view.findViewById(R.id.keeperResults_rv);
            rv.setAdapter(
                    new SearchResultsRv(
                            client,
                            keepers,
                            existingRequestsList1,
                            iRequests,
                            iSearchResultsDialog
                    ));
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            setMessage("No results found");
        }
    }
}
