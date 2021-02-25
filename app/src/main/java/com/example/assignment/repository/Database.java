package com.example.assignment.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.assignment.converters.DateConverter;
import com.example.assignment.models.Contact;
import com.example.assignment.models.User;

@androidx.room.Database(entities = {User.class,Contact.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class Database extends RoomDatabase {

    private static Database database;

    public static Database getInstance(Context context) {
        if (database == null)
//            database = Room.databaseBuilder(context, Database.class, "User_Database").build();
            database = Room.databaseBuilder(context, Database.class, "User_Database")
                    .addMigrations(MIGRATION_1_2).build();
        return database;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table 'userdb' add 'date' integer ");
        }
    };

    public abstract UserDao userDao();

    public abstract ContactDao contactDao();
}