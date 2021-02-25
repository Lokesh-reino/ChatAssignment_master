package com.example.assignment.repository;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assignment.models.User;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UserDao {
    @Insert
    Completable insert(User user);


    @Query("SELECT * FROM User")
    DataSource.Factory<Integer,User> getAllUser();

    @Query("Delete From User")
    Completable deleteAllUsers();

    @Query("DELETE  FROM User  WHERE id = :Id")
    Completable deleteUser(int Id);

    @Delete
    Completable deleteUserfromDb(User user);

    @Query("UPDATE User SET name=:u_name,birthday=:u_bday,phoneNumber =:u_phonenumber WHERE id = :Id ")
    Completable  updateUserById(String u_name,String u_bday,String u_phonenumber,int Id);


    @Query("SELECT * FROM User WHERE id = :Id")

    Single<User> getUserById(int Id);

    @Query("select * from User where name like :query or phoneNumber like :query")
    DataSource.Factory<Integer, User> queryAllUser(String query);


}
