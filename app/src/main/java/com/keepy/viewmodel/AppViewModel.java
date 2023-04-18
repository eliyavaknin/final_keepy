package com.keepy.viewmodel;

import static android.service.controls.ControlsProviderService.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.keepy.Constants;
import com.keepy.Setting;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/*
    This class is the heart of the app
    It is a ViewModel, which means that it is destroyed when the app is closed and recreated when the app is opened
    We use live data, which means that it can be observed by other classes and they will be notified when the data changes
 */
public class AppViewModel extends ViewModel {

    // -------------------- App Data ------------------------------------------------------------ //
    private final List<String> searchKeeperTypes = new ArrayList<>();

    private String searchLocation = "";
    private final MutableLiveData<User> _userMutableLiveData = new MutableLiveData<>();
    public final LiveData<User> userLiveData = _userMutableLiveData;
    private final MutableLiveData<Exception> _errMutableLiveData = new MutableLiveData<>();
    public final LiveData<Exception> errLiveData = _errMutableLiveData;
    private final MutableLiveData<String> _loadingLivaData = new MutableLiveData<>();
    public final LiveData<String> loadingLivaData = _loadingLivaData;
    private final MutableLiveData<List<ServiceRequest>> _incomingRequests = new MutableLiveData<>();
    public final LiveData<List<ServiceRequest>> incomingRequests = _incomingRequests;

    private final MutableLiveData<List<ServiceRequest>> _outgoingRequests = new MutableLiveData<>();
    public final LiveData<List<ServiceRequest>> outgoingRequests = _outgoingRequests;

    private final MutableLiveData<List<User>> _keeperRecommendations = new MutableLiveData<>();
    public final LiveData<List<User>> keeperRecommendations = _keeperRecommendations;

    private final MutableLiveData<List<User>> _keepersByType = new MutableLiveData<>();
    public final LiveData<List<User>> keepersByType = _keepersByType;

    // -------------------- ---------- ------------------------------------------------------------ //
    public void addSearchKeeperType(String type) {
        searchKeeperTypes.add(type);
    }

    public void setSearchLocation(String location) {
        searchLocation = location;
    }


    // This method is used to check if the search location
    // is set to all or to a specific location or to the default value
    public boolean isSearchLocationAll() {
        return searchLocation == null || searchLocation.equals("All");
    }


    // this method is used to toggle the search types of the keepers for client search
    public void toggleSearchKeeperType(String type) {
        searchKeeperTypes.clear();
        searchKeeperTypes.add(type);
    }

    // this method is used to remove a keeper type from the search types of the keepers for client search
    public void removeSearchKeeperType(String type) {
        searchKeeperTypes.remove(type);
    }

    // this method is used to clear the search types of the keepers for client search
    public void clearSearchKeeperTypes() {
        searchKeeperTypes.clear();
    }

    // this method is used to get the search types of the keepers for client search
    public List<String> getSearchKeeperTypes() {
        return searchKeeperTypes;
    }


    // this method is used to save the user to the database
    // it can be used as a sign up or as an update
    // if the user is already in the database, it will be updated
    public void saveUser(User user) {
        _loadingLivaData.postValue("Saving user...");
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo(Constants.UsersCollectionEmailField, user.getmEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (!value.isEmpty()) {
                        DocumentSnapshot snapshot = value.getDocuments().get(0);
                        snapshot.getReference().set(user);
                    }
                    _loadingLivaData.postValue(null);
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    // On initialization, we check if the user is already logged in
    // If so, we get the user from the database
    // we listen to different collections in the database based on
    // the user type (client or keeper)
    public AppViewModel() {
        FirebaseAuth.getInstance()
                .addAuthStateListener((auth) -> {
                    if (auth.getCurrentUser() != null)
                        FirebaseFirestore.getInstance()
                                .collection(Constants.UsersCollection)
                                .document(FirebaseAuth.getInstance().getUid())
                                .addSnapshotListener((value, error) -> {
                                    if (error != null || value == null) {
                                        _errMutableLiveData.postValue(error);
                                        return;
                                    }

                                    User user = value.toObject(User.class);
                                    _userMutableLiveData.postValue(user);

                                    if (user != null) {
                                        if (user.getmIsClient()) {
                                            getOutgoingRequests(value.getReference());
                                        }

                                        if (user.getmIsKeeper()) {
                                            getIncomingRequests(value.getReference());
                                        }
                                    }
                                });
                });
    }


    // this method is used to get the incoming requests of a keeper
    public void getIncomingRequests(DocumentReference userRef) {
        _loadingLivaData.postValue("Loading incoming requests...");
        userRef.collection(Constants.ServiceRequestsCollection_incoming)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        _errMutableLiveData.postValue(error);
                        return;
                    }
                    if (_loadingLivaData.getValue() != null)
                        _loadingLivaData.postValue(null);
                    _incomingRequests.postValue(value.toObjects(ServiceRequest.class));
                });
    }


    // this method is used to get the outgoing requests of a client
    public void getOutgoingRequests(DocumentReference userRef) {
        _loadingLivaData.postValue("Loading outgoing requests...");
        userRef.collection(Constants.ServiceRequestsCollection_outgoing)
                .addSnapshotListener((value, error) -> {
                    if (_loadingLivaData.getValue() != null)
                        _loadingLivaData.postValue(null);
                    if (error != null || value == null) {
                        _errMutableLiveData.postValue(error);
                        return;
                    }
                    _outgoingRequests.postValue(value.toObjects(ServiceRequest.class));
                });
    }


    // this method is used to send a request to a keeper
    // we update the request in the client's outgoing requests collection
    // and in the keeper's incoming requests collection
    // P.S : currently we don't use the client and keeper references
    // but we can use them to update the requests in the client and keeper
    // and we may need them in the future
    public void sendRequest(
            User client,
            User keeper,
            ServiceRequest request) {

        _loadingLivaData.postValue("Sending request...");
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo(Constants.UsersCollectionEmailField, request.getKeeperEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such keeper"));
                        return;
                    }
                    DocumentReference keeperRef = value.getDocuments().get(0).getReference();
                    keeperRef.collection(Constants.ServiceRequestsCollection_incoming)
                            .add(request)
                            .addOnSuccessListener(value1 -> {
                                String id = value1.getId();
                                request.setId(id);
                                value1.update("id", id);
                                FirebaseFirestore.getInstance()
                                        .collection(Constants.UsersCollection)
                                        // we get the client by email
                                        .whereEqualTo(Constants.UsersCollectionEmailField, request.getClientEmail())
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(value2 -> {
                                            if (value2.isEmpty()) {
                                                _errMutableLiveData.postValue(new Exception("No such client"));
                                                return;
                                            }
                                            DocumentReference clientRef = value2.getDocuments().get(0).getReference();
                                            clientRef.collection(Constants.ServiceRequestsCollection_outgoing)
                                                    .add(request)
                                                    .addOnSuccessListener(value3 -> {
                                                        _loadingLivaData.postValue(null);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        _errMutableLiveData.postValue(e);
                                                        _loadingLivaData.postValue(null);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            _errMutableLiveData.postValue(e);
                                            _loadingLivaData.postValue(null);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                _errMutableLiveData.postValue(e);
                                _loadingLivaData.postValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    // this method is used to approve a request
    // we update the request in the client's outgoing requests collection
    // and in the keeper's incoming requests collection
    public void approveRequest(
            ServiceRequest request
    ) {
        _loadingLivaData.postValue("Approving request...");
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo(Constants.UsersCollectionEmailField, request.getKeeperEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such keeper"));
                        return;
                    }
                    DocumentReference keeperRef = value.getDocuments().get(0).getReference();
                    keeperRef.collection(Constants.ServiceRequestsCollection_incoming)
                            .whereEqualTo("id", request.getId())
                            .limit(1)
                            .get()
                            .addOnSuccessListener(value1 -> {
                                if (value1.isEmpty()) {
                                    _errMutableLiveData.postValue(new Exception("No such request"));
                                    return;
                                }
                                DocumentReference requestRef = value1.getDocuments().get(0).getReference();
                                requestRef.update(Constants.ServiceRequestsCollection_statusField, 1)
                                        .addOnSuccessListener(value2 -> {
                                            FirebaseFirestore.getInstance()
                                                    .collection(Constants.UsersCollection)
                                                    .whereEqualTo(Constants.UsersCollectionEmailField, request.getClientEmail())
                                                    .limit(1)
                                                    .get()
                                                    .addOnSuccessListener(value3 -> {
                                                        if (value3.isEmpty()) {
                                                            _errMutableLiveData.postValue(new Exception("No such client"));
                                                            return;
                                                        }
                                                        DocumentReference clientRef = value3.getDocuments().get(0).getReference();
                                                        clientRef.collection(Constants.ServiceRequestsCollection_outgoing)
                                                                .whereEqualTo("id", request.getId())
                                                                .limit(1)
                                                                .get()
                                                                .addOnSuccessListener(value4 -> {
                                                                    if (value4.isEmpty()) {
                                                                        _errMutableLiveData.postValue(new Exception("No such request"));
                                                                        return;
                                                                    }
                                                                    DocumentReference requestRef2 = value4.getDocuments().get(0).getReference();
                                                                    requestRef2.update(Constants.ServiceRequestsCollection_statusField, ServiceRequest.Status.APPROVED)
                                                                            .addOnSuccessListener(value5 -> {
                                                                                _loadingLivaData.postValue(null);
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                _errMutableLiveData.postValue(e);
                                                                                _loadingLivaData.postValue(null);
                                                                            });
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    _errMutableLiveData.postValue(e);
                                                                    _loadingLivaData.postValue(null);
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        _errMutableLiveData.postValue(e);
                                                        _loadingLivaData.postValue(null);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            _errMutableLiveData.postValue(e);
                                            _loadingLivaData.postValue(null);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                _errMutableLiveData.postValue(e);
                                _loadingLivaData.postValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    // this method is used to reject a request
    // we update the request in the client's outgoing requests collection
    public void declineRequest(
            ServiceRequest request) {
        _loadingLivaData.postValue("Declining request...");
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo(Constants.UsersCollectionEmailField, request.getKeeperEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such keeper"));
                        return;
                    }
                    DocumentReference keeperRef = value.getDocuments().get(0).getReference();
                    keeperRef.collection(Constants.ServiceRequestsCollection_incoming)
                            .whereEqualTo("id", request.getId())
                            .limit(1)
                            .get()
                            .addOnSuccessListener(value1 -> {
                                if (value1.isEmpty()) {
                                    _errMutableLiveData.postValue(new Exception("No such request"));
                                    return;
                                }
                                DocumentReference requestRef = value1.getDocuments().get(0).getReference();
                                requestRef.update(Constants.ServiceRequestsCollection_statusField, ServiceRequest.Status.DECLINED)
                                        .addOnSuccessListener(value2 -> {
                                            FirebaseFirestore.getInstance()
                                                    .collection(Constants.UsersCollection)
                                                    .whereEqualTo(Constants.UsersCollectionEmailField, request.getClientEmail())
                                                    .limit(1)
                                                    .get()
                                                    .addOnSuccessListener(value3 -> {
                                                        if (value3.isEmpty()) {
                                                            _errMutableLiveData.postValue(new Exception("No such client"));
                                                            return;
                                                        }
                                                        DocumentReference clientRef = value3.getDocuments().get(0).getReference();
                                                        clientRef.collection(Constants.ServiceRequestsCollection_outgoing)
                                                                .whereEqualTo("id", request.getId())
                                                                .limit(1)
                                                                .get()
                                                                .addOnSuccessListener(value4 -> {
                                                                    if (value4.isEmpty()) {
                                                                        _errMutableLiveData.postValue(new Exception("No such request"));
                                                                        return;
                                                                    }
                                                                    DocumentReference requestRef2 = value4.getDocuments().get(0).getReference();
                                                                    requestRef2.update(Constants.ServiceRequestsCollection_statusField, ServiceRequest.Status.DECLINED)
                                                                            .addOnSuccessListener(value5 -> {
                                                                                _loadingLivaData.postValue(null);
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                _errMutableLiveData.postValue(e);
                                                                                _loadingLivaData.postValue(null);
                                                                            });
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    _errMutableLiveData.postValue(e);
                                                                    _loadingLivaData.postValue(null);
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        _errMutableLiveData.postValue(e);
                                                        _loadingLivaData.postValue(null);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            _errMutableLiveData.postValue(e);
                                            _loadingLivaData.postValue(null);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                _errMutableLiveData.postValue(e);
                                _loadingLivaData.postValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    // this method is used to search keepers
    // the search is biased towards the client's preferred keeper type and location
    public void searchKeepers() {
        _loadingLivaData.postValue("Loading keepers...");
        User user = _userMutableLiveData.getValue();
        if (user == null) {
            _errMutableLiveData.postValue(new Exception("You are not logged in"));
            return;
        }
        Query q = FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo("mIsKeeper", true);

        if (!isSearchLocationAll()) {
            q = q.whereEqualTo("mLocation", searchLocation);
        }
        // sort by rating
        q = q.orderBy("mKeeperData.rating", Query.Direction.DESCENDING);
        q.get().addOnSuccessListener(value -> {
                    List<User> keepers = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) { // O(n)
                        User keeper = document.toObject(User.class);
                        if (keeper == null || keeper.getmKeeperData() == null) continue;
                        Set<String> keeperTypes = keeper.getmKeeperData().getFees().keySet();
                        List<String> seen = new ArrayList<>();
                        // if the client has no preferred keeper types
                        for (String type : keeperTypes) { // O(1) fixed size
                            if (searchKeeperTypes.contains(type)
                                    && !seen.contains(keeper.getmEmail())) {
                                keepers.add(keeper);
                                seen.add(keeper.getmEmail());
                            }
                        }
                    }
                    _keepersByType.postValue(keepers);
                    _loadingLivaData.postValue(null);
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    // this method is used to clear the search values
    public void clearSearchValues() {
        _keepersByType.postValue(null);
        _keeperRecommendations.postValue(null);
    }

    // this method is used to get the recommended keepers
    // biased towards the client's preferred keeper type and location
    public void getRecommendedKeepers() {
        User user = _userMutableLiveData.getValue();
        if (user == null) return;
        _loadingLivaData.postValue("Loading keepers...");
        Query q = FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo("mIsKeeper", true);


        if (!isSearchLocationAll()) {
            q = q.whereEqualTo("mLocation", searchLocation);
        }

        q.whereGreaterThan("mKeeperData.rating", Constants.RatingRecommendationMinRequirement)
                .orderBy("mKeeperData.rating", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(value -> {
                    List<User> keepers = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) { // O(n)
                        User keeper = document.toObject(User.class);
                        if (keeper == null) continue;
                        Set<String> keeperTypes = keeper.getmKeeperData().getFees().keySet();
                        List<String> seen = new ArrayList<>();
                        for (String type : keeperTypes) // O(1)
                            if (user.getClientPrefs().contains(type)
                                    && !seen.contains(keeper.getmEmail())) {                                // O(1)
                                keepers.add(keeper);
                                seen.add(keeper.getmEmail());
                            }
                    }
                    _keeperRecommendations.postValue(keepers);
                    _loadingLivaData.postValue(null);
                })
                .addOnFailureListener(e -> {
                    _errMutableLiveData.postValue(e);
                    _loadingLivaData.postValue(null);
                });
    }


    /*
        this method is used to rate a keeper
        the rating is calculated by the average of all the ratings
     */
    public void rateKeeper(User client, User keeper, float rating) {
        if (client == null) return;
        List<String> rated = client.getRatedKeepers();
        if (rated == null) rated = new ArrayList<>();
        if (rated.contains(keeper.getmEmail())) {
            _errMutableLiveData.postValue(new Exception("You have already rated this keeper"));
            return;
        }
        rated.add(keeper.getmEmail());
        saveUser(client);
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo("mEmail", keeper.getmEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such keeper"));
                        return;
                    }
                    DocumentReference keeperRef = value.getDocuments().get(0).getReference();
                    float newRating = (keeper.getmKeeperData().getRating() * keeper.getmKeeperData().getRatingCount() + rating) / (keeper.getmKeeperData().getRatingCount() + 1);
                    keeperRef.update("mKeeperData.rating",
                            newRating,
                            "mKeeperData.ratingCount",
                            keeper.getmKeeperData().getRatingCount() + 1);

                });
    }

    public void wantToBecomeClient() {
        User user = _userMutableLiveData.getValue();
        if (user == null) return;
        user.setmIsClient(true);
        saveUser(user);
    }

    public void wantToBecomeKeeper() {
        User user = _userMutableLiveData.getValue();
        if (user == null) return;
        user.setmIsKeeper(true);
        saveUser(user);
    }

    public void cancelRequest(ServiceRequest request) {
        // delete the request from the client's requests
        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo("mEmail", request.getClientEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such client"));
                        return;
                    }
                    DocumentReference clientRef = value.getDocuments().get(0).getReference();
                    clientRef.collection(Constants.ServiceRequestsCollection_outgoing)
                            .whereEqualTo("id", request.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    _errMutableLiveData.postValue(new Exception("No such request"));
                                    return;
                                }
                                DocumentReference requestRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                requestRef.delete();
                            });
                });

        // delete the request from the keeper's requests

        FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .whereEqualTo("mEmail", request.getKeeperEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(value -> {
                    if (value.isEmpty()) {
                        _errMutableLiveData.postValue(new Exception("No such keeper"));
                        return;
                    }
                    DocumentReference clientRef = value.getDocuments().get(0).getReference();
                    clientRef.collection(Constants.ServiceRequestsCollection_incoming)
                            .whereEqualTo("id", request.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    _errMutableLiveData.postValue(new Exception("No such request"));
                                    return;
                                }
                                DocumentReference requestRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                requestRef.delete();
                            });
                });
    }



    // Deletes a user
    public void deleteUser(AppCompatActivity activity) {
        // [START delete_document]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.UsersCollection)
                .whereEqualTo("mEmail",FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots.forEach(user -> user.getReference().delete());
                        activity.finish();
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(activity.getApplicationContext(), "Your user has been delete successfully", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        // [END delete_document]
    }
}
