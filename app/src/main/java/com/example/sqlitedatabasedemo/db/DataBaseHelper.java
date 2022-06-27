package com.example.sqlitedatabasedemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sqlitedatabasedemo.db.entity.Contact;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="contact.db";

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    //implement the functions
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Contact.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contact.TABLE_NAME);
        onCreate(sqLiteDatabase);



    }

    //Insert Data into Database

    public long insertContact(String name,String email){


        SQLiteDatabase db=this.getReadableDatabase();
        ContentValues values=new ContentValues();


        values.put(Contact.COLUMN_NAME,name);
        values.put(Contact.COLUMN_EMAIL,email);

        long id= db.insert(Contact.TABLE_NAME,null,values);

        db.close();
        return id;


    }

    //Getting the data from the database
    public Contact getContact(long id){
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.query(Contact.TABLE_NAME,new String[]{
                Contact.COLUMN_ID,
                Contact.COLUMN_NAME,
                Contact.COLUMN_EMAIL},
                Contact.COLUMN_ID+"=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        Contact contact=new Contact(
                cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_EMAIL)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Contact.COLUMN_ID)));

        cursor.close();
        return contact;




    }

    //Getting all contact
    public ArrayList<Contact> getAllContact(){
        ArrayList<Contact> contacts=new ArrayList<>();

        String selectQuery="SELECT * FROM "+Contact.TABLE_NAME
                +" ORDER BY "+Contact.COLUMN_EMAIL+" DESC";

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Contact contact=new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Contact.COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME)));
                contact.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_EMAIL)));
                contacts.add(contact);
            }while (cursor.moveToNext());
        }
        db.close();
        return contacts;
    }



    //Updating the database

    public int updateContact(Contact contact){
        SQLiteDatabase db=getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put(Contact.TABLE_NAME,contact.getName());
        values.put(Contact.COLUMN_EMAIL,contact.getEmail());

        return db.update(Contact.TABLE_NAME,values,Contact.COLUMN_ID+"=",new String[]{
                String.valueOf(contact.getId())
        });
    }

    //Delete contact
    public void deleteContact(Contact contact){
        SQLiteDatabase db=this.getReadableDatabase();
        db.delete(Contact.TABLE_NAME,Contact.COLUMN_ID+"=?",new String[]{String.valueOf(contact.getId())});
        db.close();
    }
}
