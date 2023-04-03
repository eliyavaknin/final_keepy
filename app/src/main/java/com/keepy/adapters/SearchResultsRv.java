package com.keepy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keepy.KeeperProfile;
import com.keepy.R;
import com.keepy.behaviour.IKeeperProfile;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SearchResultsRv extends RecyclerView.Adapter<SearchResultsRv.ViewHolder> {

    private final List<User> keepers;

    private final User client;
    private final List<ServiceRequest> existingRequestsList;
    private final IRequests iRequests;
    private final IKeeperProfile iSearchResultsRv;

    public SearchResultsRv(
            User client,
            List<User> keepers,
            List<ServiceRequest> existingRequestsList,
            IRequests iRequests,
            IKeeperProfile iSearchResultsRv) {
        this.keepers = keepers;
        this.client = client;
        this.iSearchResultsRv = iSearchResultsRv;
        this.iRequests = iRequests;
        this.existingRequestsList = existingRequestsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keeper_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(client,
                keepers.get(position),
                iSearchResultsRv,
                existingRequestsList);
    }

    @Override
    public int getItemCount() {

        return keepers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, type;
        private final LinearLayout price;
        private final RatingBar rating;
        private final Button requestButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.keeperName_ClientSearch_rv);
            type = itemView.findViewById(R.id.keeperType_ClientSearch_rv);
            price = itemView.findViewById(R.id.keeperPrice_ClientSearch_rv);
            rating = itemView.findViewById(R.id.keeperRating_ClientSearch_rv);
            requestButton = itemView.findViewById(R.id.sendRequestBtn);
        }

        // method used to bind data to view holder
        // and set click listeners
        public void bind(
                User client,
                User keeper,
                IKeeperProfile iSearchResultsRv,
                List<ServiceRequest> existingRequestsList) {
            name.setText("Name: " + (keeper.getmFullName().isEmpty() ? "Annonymous" : keeper.getmFullName()));
            String concatType = "Type: ";
            if (keeper.getmKeeperData() == null || keeper.getmKeeperData().getFees() == null)
                return;
            Set<String> types = keeper.getmKeeperData().getFees().keySet();
            for (int i = 0; i < types.size(); i++) {
                concatType += types.toArray()[i];
                if (i < types.size() - 1)
                    concatType += ", ";
            }
            if (concatType.length() > 1 && concatType.charAt(concatType.length() - 2) == ',')
                concatType = concatType.substring(0, concatType.length() - 1);
            type.setText(concatType);
            HashMap<String, Integer> feesMap = keeper.getmKeeperData().getFees();

            for (Map.Entry<String, Integer> entry : feesMap.entrySet()) {
                TextView textView = new TextView(itemView.getContext());
                textView.setText(entry.getKey() + ": " + entry.getValue() + "₪");
                price.addView(textView);
            }

            rating.setRating(keeper.getmKeeperData().getRating());


            requestButton.setOnClickListener(view -> {
                KeeperProfile dialog = new KeeperProfile(
                        itemView.getContext(),
                        client,
                        keeper,
                        existingRequestsList,
                        iRequests,
                        iSearchResultsRv);
                dialog.show();
            });
        }
    }

}
