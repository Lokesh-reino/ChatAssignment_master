package com.example.assignment.viewmodel;

import android.app.Application;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.assignment.R;
import com.example.assignment.helpers.SyncNativeContacts;
import com.example.assignment.models.Contact;
import com.example.assignment.models.User;
import com.example.assignment.repository.LocalRepository;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateEntryViewModel extends AndroidViewModel {

    public final static String TAG = "TAG";

    SyncNativeContacts syncNativeContacts;
    public LiveData<PagedList<Contact>> contactList;
    public LiveData<PagedList<Contact>> queryContactList;

    public LiveData<PagedList<User>> userList;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<User> user = new MutableLiveData<>();
    private LocalRepository repository = new LocalRepository(getApplication());
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public LiveData<PagedList<User>> queriedUserList;
    Toast toast;

    public static MutableLiveData<String> queryString = new MutableLiveData<>();

    public static void setQueryString(String query) {
        queryString.setValue(query);
    }

    public LiveData<String> getQueryString() {
        return queryString;
    }


    public void contactInit() {

        PagedList.Config config = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(10).build();

        contactList = new LivePagedListBuilder<>(repository.getAllContacts(), config).build();
    }

    public void queryContactInit(String query) {
        repository = new LocalRepository(getApplication());

        PagedList.Config config = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(10).build();

        queryContactList = new LivePagedListBuilder<>(repository.getQueryContact(query), config).build();
    }


    private CompositeDisposable disposable = new CompositeDisposable();

    public CreateEntryViewModel(@NonNull Application application) {
        super(application);
        repository = new LocalRepository(getApplication());
    }

    public void refresh() {
        fetchDataFromDatabase();
    }

    public void fetchDataFromDatabase() {
        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .build();

        userList = new LivePagedListBuilder<>(repository.getAllUsers(), config).build();

    }


    public void saveToDatabase(User user) {
        repository.insert(user).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            }

            @Override
            public void onComplete() {
                fetchDataFromDatabase();
                //  Toast.makeText(getApplication(),"User Saved",Toast.LENGTH_SHORT).show();
                Log.e("Tab1Viewmodel", "<---------- onComplete: User Saved in DB ---------->");
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
                Log.e("Tab1Viewmodel", "onError: ---->" + e.getMessage());

            }
        });

    }

    public void fetchDetailsFromDatabase(int id) {
        repository.getUserById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull User user1) {
                        user.setValue(user1);

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();

                    }
                });

    }

    public void deleteUserFromDatabase(int id) {
        repository.deleteUserfromDb(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        Log.d("TAG", "Inside onSubscribe of deleteUser in ViewModel");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "Inside onComplete of deleteUser in ViewModel");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.d("TAG", "Inside onError of deleteUser in ViewModel");
                        e.printStackTrace();
                    }
                });

    }

    public void updateUser(String u_name, String u_bday, String u_phonenumber, int Id) {
        repository.updateUserById(u_name, u_bday, u_phonenumber, Id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        Log.d("TAG", "Inside onSubscribe of updateUser in ViewModel");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "Inside onComplete of updateUser in ViewModel");
                        successToast("User Data updated successfully");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.d("TAG", "Inside onError of updateUser in ViewModel");
                        failureToast(e.getMessage());
                        e.printStackTrace();

                    }
                });
    }

    private static MutableLiveData<Boolean> isMultiSelectOn = new MutableLiveData<>();

    public void setIsMultiSelect(boolean isMultiSelect) {
        isMultiSelectOn.setValue(isMultiSelect);
    }

    public static LiveData<Boolean> getIsMultiSelectOn() {
        return isMultiSelectOn;
    }


    public LiveData<User> getUser() {
        return user;
    }

    public void queryInit(String query) {

        repository = new LocalRepository(getApplication());

        PagedList.Config config = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(10).build();
        queriedUserList = new LivePagedListBuilder<>(repository.queryAllUser(query), config).build();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    private void successToast(String message) {

        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();

        view.getBackground().setColorFilter(ContextCompat.getColor(getApplication(), R.color.teal_200), PorterDuff.Mode.SRC_IN);

        toast.show();
    }

    private void failureToast(String message) {

        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();

        view.getBackground().setColorFilter(ContextCompat.getColor(getApplication(), R.color.red), PorterDuff.Mode.SRC_IN);

        toast.show();
    }

    public void completeContactSync() {
        syncNativeContacts = new SyncNativeContacts(getApplication());
        syncNativeContacts.getContactArrayList().doAfterSuccess(newlist -> addContactListToDB(newlist))
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Contact>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        Log.e(TAG, "onSubscribe: Inside complete sync  ");
                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull List<Contact> contactList) {
                        Log.e(TAG, "onSuccess: Inside complete sync   -->>  " + contactList.size());
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "onError: Inside complete sync error ->> " + e.getMessage());
                    }
                });

    }
    public void addContactListToDB(List<Contact> contactList) {

        repository.addListOfContact(contactList)
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        Log.d("TAG", "Inside onSubscribe of addContactListDB in SyncNativeContacts");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "Inside onComplete of addContactListDB in SyncNativeContacts");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.d("TAG", "Inside onError of addContactListDB in SyncNativeContacts.: " + e.getMessage());
                    }
                });
    }
}